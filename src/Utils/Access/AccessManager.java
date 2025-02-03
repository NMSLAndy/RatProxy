package Utils.Access;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import Patterns.Disconnect;
import Utils.TimerUtil;
import Utils.Config.Config;
import Utils.File.FileManager;
import Utils.File.Log.Log;

public class AccessManager {

	private static final int refetchCD = 60;// seconds
	private static final int ipBanReset = 3 * 60;// seconds

	private static final Path WL_PATH = new File(FileManager.mainDir, "Whitelist.json").toPath();
	private static final Path WL_DIR = FileManager.mainDir.toPath();

	private static final Set<String> nameWhiteList = ConcurrentHashMap.newKeySet();
	private static final Set<UUID> uuidWhiteList = ConcurrentHashMap.newKeySet();
	private static final Map<String, StopWatch> rateMap = new ConcurrentHashMap<>();
	private static final Map<String, StopWatch> loginMap = new ConcurrentHashMap<>();
	private static final Map<String, CachedUUID> uuidMap = new ConcurrentHashMap<>();

	private static final Map<String, Map<String, TimerUtil>> nonUUIDMap = new ConcurrentHashMap<>();
	private static final Map<String, TimerUtil> blockedIPMap = new ConcurrentHashMap<>();

	public static void init() {
		read();
		startFileWatcher();
		Thread.ofVirtual().start(() -> {
			while (true) {
				try {
					rateMap.entrySet().removeIf(entry -> entry.getValue().timer.hasReached(entry.getValue().maxTime));
					loginMap.entrySet().removeIf(entry -> entry.getValue().timer.hasReached(entry.getValue().maxTime));
					uuidMap.entrySet().removeIf(entry -> entry.getValue().timer.hasReached(60 * 60 * 1000));
					nonUUIDMap.entrySet().forEach(entry -> entry.getValue().entrySet()
							.removeIf(e -> e.getValue().hasReached(refetchCD * 1000)));
					nonUUIDMap.entrySet().removeIf(entry -> entry.getValue().isEmpty());
					blockedIPMap.entrySet().removeIf(entry -> entry.getValue().hasReached(ipBanReset * 1000));
					Thread.sleep(500);
				} catch (Exception e) {
					// Ignored
				}
			}
		});
	}

	public static boolean checkRateLimit(String name, SocketChannel sourceChannel) throws IOException {
		String ip = sourceChannel.socket().getInetAddress().toString();
		if (!rateMap.containsKey(ip)) {
			rateMap.put(ip, new StopWatch(new TimerUtil().reset(), 500));
			return true;
		}
		Log.saveDeny(name, "连接频率限制");
		sourceChannel.write(Disconnect.rateLimit());
		return false;
	}

	public static boolean checkAccessibility(String name, UUID uuid_, SocketChannel sourceChannel) throws IOException {
		if (name == null)
			return false;

		String ip = sourceChannel.socket().getInetAddress().toString();
		if (blockedIPMap.containsKey(ip)) {
			TimerUtil timer = blockedIPMap.get(ip);
			int remaining = ipBanReset - (int) (timer.getCurrentMS() - timer.getLastMS()) / 1000;
			sourceChannel.write(Disconnect.custom("你的IP被暂时禁止登陆, 请 " + remaining + " 秒后重试.", "",
					"Your IP is temporarily banned, please retry after " + remaining + " seconds."));
			return false;
		}

		checkRateLimit(name, sourceChannel);

		if (Config.doubleLogin && !loginMap.containsKey(name)) {
			sourceChannel.write(Disconnect.reqDoubleLogin());
			loginMap.put(name, new StopWatch(new TimerUtil().reset(), 10 * 1000));
			return false;
		}

		if (!Config.whiteListEnabled)
			return true;

		if (nameWhiteList.contains(name))
			return true;

		if (nonUUIDMap.containsKey(ip) && nonUUIDMap.get(ip).containsKey(name)) {
			TimerUtil timer = nonUUIDMap.get(ip).get(name);
			int remaining = refetchCD - (int) (timer.getCurrentMS() - timer.getLastMS()) / 1000;
			sourceChannel.write(Disconnect.custom("无法获取您的uuid, 请 " + remaining + " 秒后重试.", "",
					"Failed to fetch your uuid, please retry after " + remaining + " seconds."));
			return false;
		}

		UUID uuid = uuid_ != null ? uuid_ : fetchUUID(name);
		if (uuid != null && uuidWhiteList.contains(uuid))
			return true;

		if (uuid == null) {
			Map<String, TimerUtil> users = new ConcurrentHashMap<>();
			if (nonUUIDMap.containsKey(ip))
				users = nonUUIDMap.get(ip);
			else
				nonUUIDMap.put(ip, users);
			users.put(name, new TimerUtil().reset());

			if (users.size() > 5)
				blockedIPMap.put(ip, new TimerUtil().reset());

			Log.saveDeny(name, "获取uuid失败");
			sourceChannel.write(Disconnect.uuidIssues());
			return false;
		}
		Log.saveDeny(name, "不在白名单内");
		sourceChannel.write(Disconnect.whiteList());
		return false;
	}

	private static UUID fetchUUID(String name) {
		if (uuidMap.get(name) != null)
			return uuidMap.get(name).uuid;
		String uuid = MCApi.getUUID(name);
		if (uuid == null)
			return null;
		UUID finUUID = UUID.fromString(uuid);
		uuidMap.put(name, new CachedUUID(finUUID, new TimerUtil().reset()));
		return finUUID;
	}

	private static void startFileWatcher() {
		Thread.ofVirtual().start(() -> {
			try (WatchService ws = FileSystems.getDefault().newWatchService()) {
				WL_DIR.register(ws, ENTRY_MODIFY);
				while (true) {
					WatchKey key = ws.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						if (!((Path) event.context()).equals(WL_PATH.getFileName()))
							continue;
						Log.warn("Whitelist.json updated, reloading whitelist...");
						read();
					}
					key.reset();
				}
			} catch (Exception e) {

			}
		});
	}

	private static void read() {
		String json = readFile();
		if (json.isEmpty()) {
			write(createDefault());
			return;
		}
		try {
			WLPattern wl = Config.gson.fromJson(json, WLPattern.class);
			nameWhiteList.addAll(wl.names);
			wl.uuids.forEach(uuid -> uuidWhiteList.add(UUID.fromString(uuid)));
		} catch (Exception e) {
			Log.error("Error while parsing whitelist : " + e.toString());
		}
	}

	private static String createDefault() {
		JsonObject json = new JsonObject();

		JsonArray names = new JsonArray();
		json.add("names", names);

		JsonArray uuids = new JsonArray();
		json.add("uuids", uuids);
		return Config.gson.toJson(json);
	}

	private static String readFile() {
		File cfg = WL_PATH.toFile();
		if (!cfg.exists())
			return "";
		try {
			return Files.readString(WL_PATH, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Log.error("Failed to read whitelist: " + e.getMessage());
			return "";
		}
	}

	public static void write(String content) {
		try {
			Files.writeString(WL_PATH, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			Log.error("Failed to write config file: " + e.getMessage());
		}
	}

	record CachedUUID(UUID uuid, TimerUtil timer) {

	}

	record StopWatch(TimerUtil timer, int maxTime) {

	}
}
