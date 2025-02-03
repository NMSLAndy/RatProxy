package Utils.Config;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import Patterns.ServerStatus;
import Utils.File.FileManager;
import Utils.File.FavIcon.IconManager;
import Utils.File.Log.Log;

public class Config {

	public static final String ratProxy = "§c§lR§6§la§e§lt §a§lP§b§lr§9§lo§d§lx§5§ly§r";
	public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static int maxPlayers = 10;
	public static boolean doubleLogin = false;
	public static boolean whiteListEnabled = false;
	public static int srcPort = 25565;
	public static String targetHost = "";
	public static int targetPort = 25565;
	public static boolean rewriteHost = false;
	public static String rewrittenHost = "";
	public static int rewrittenPort = 25565;

	public static final String[] rat = { "                  ..----.._    _              ",
			"                .' .--.    '-.(O)_            ", "    '-.__.-'''=:|  ,  _)_ |__ . c\\'-..        ",
			"                 ''------'---''---'-'     " };

	private static final Path CONFIG_PATH = new File(FileManager.mainDir, "Config.json").toPath();
	private static final Path CONFIG_DIR = FileManager.mainDir.toPath();
	private static boolean missingArgument = false;

	public static String getFavIcon() {
		int index = ThreadLocalRandom.current().nextInt(0, IconManager.favIcons.size());
		return IconManager.favIcons.get(index);
	}

	public static void readConfig() {
		try {
			String jsonContent = readFile();
			if (jsonContent.isEmpty()) {
				write(createDefaultCFG());
				Log.warn("Config file created. Please configure it before running.");
				System.exit(-1);
			}
			CFGPattern config = gson.fromJson(jsonContent, CFGPattern.class);
			srcPort = config.srcPort;
			targetHost = config.targetHost;
			targetPort = config.targetPort;
			parseConfig(jsonContent, false);
			startFileWatcher();
		} catch (Exception e) {
			Log.error("Error reading config: " + e.getMessage());
		}
	}

	private static void parseConfig(String json, boolean log) {
		try {
			CFGPattern config = gson.fromJson(json, CFGPattern.class);
			updateField(log, "rewriteHost", config.rewriteHost, v -> rewriteHost = v);
			updateField(log, "rewrittenHost", config.rewrittenHost, v -> rewrittenHost = v);
			updateField(log, "rewrittenPort", config.rewrittenPort, v -> rewrittenPort = v);
			updateField(log, "maxPlayers", config.maxPlayers, v -> maxPlayers = v);
			updateField(log, "whiteListEnabled", config.whiteListEnabled, v -> whiteListEnabled = v);
			updateField(log, "doubleLogin", config.doubleLogin, v -> doubleLogin = v);
			updateMotd(log, config.motds);
			updateList(log, config.list);
			if (missingArgument) {
				write(createDefaultCFG());
				Log.warn("Created default config.");
				missingArgument = false;
			}
		} catch (Exception e) {
			Log.error("Error while parsing config : " + e.toString());
		}
	}

	private static void updateMotd(boolean log, List<String> motds) {
		if (motds == null) {
			Log.error("Missing argument: motds");
			missingArgument = true;
			return;
		}
		ArrayList<String> omotds = new ArrayList<String>();
		ServerStatus.motds.forEach(e -> omotds.add(e.text));
		if (!omotds.equals(motds)) {
			if (log)
				Log.save("Motd: " + ServerStatus.motds + " -> " + motds);
			ServerStatus.motds.clear();
			motds.forEach(ServerStatus::addMOTD);
		}
	}

	private static void updateList(boolean log, List<String> list) {
		if (list == null) {
			Log.error("Missing argument: list");
			missingArgument = true;
			return;
		}
		ArrayList<String> olist = new ArrayList<String>();
		ServerStatus.list.forEach(e -> olist.add(e.name));
		if (!olist.equals(list)) {
			if (log)
				Log.save("List: " + ServerStatus.list + " -> " + list);
			ServerStatus.list.clear();
			list.forEach(ServerStatus::addLine);
		}
	}

	@SuppressWarnings("unchecked")
	private static <T> void updateField(boolean log, String name, T newValue, Consumer<T> updater) {
		try {
			if (newValue == null) {
				Log.error("Missing argument: " + name);
				missingArgument = true;
				return;
			}
			T oldValue = (T) Config.class.getDeclaredField(name).get(null);
			if (!newValue.equals(oldValue)) {
				if (log)
					Log.save(name + ": " + oldValue + " -> " + newValue);
				updater.accept(newValue);
			}
		} catch (Exception ignored) {
		}
	}

	private static void startFileWatcher() {
		Thread.ofVirtual().start(() -> {
			try (WatchService ws = FileSystems.getDefault().newWatchService()) {
				CONFIG_DIR.register(ws, ENTRY_MODIFY);
				while (true) {
					WatchKey key = ws.take();
					for (WatchEvent<?> event : key.pollEvents()) {
						if (!((Path) event.context()).equals(CONFIG_PATH.getFileName()))
							continue;
						Log.warn("Config.json updated, reloading...");
						parseConfig(readFile(), true);
					}
					key.reset();
				}
			} catch (Exception e) {

			}
		});
	}

	private static String readFile() {
		File cfg = CONFIG_PATH.toFile();
		if (!cfg.exists())
			return "";
		try {
			return Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Log.error("Failed to read config file: " + e.getMessage());
			return "";
		}
	}

	public static void write(String content) {
		try {
			Files.writeString(CONFIG_PATH, content, StandardCharsets.UTF_8, StandardOpenOption.CREATE,
					StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			Log.error("Failed to write config file: " + e.getMessage());
		}
	}

	private static String createDefaultCFG() {
		JsonObject json = new JsonObject();
		json.addProperty("srcPort", srcPort);
		json.addProperty("targetHost", targetHost);
		json.addProperty("targetPort", targetPort);
		json.addProperty("rewriteHost", rewriteHost);
		json.addProperty("rewrittenHost", rewrittenHost);
		json.addProperty("rewrittenPort", rewrittenPort);
		json.addProperty("maxPlayers", maxPlayers);
		json.addProperty("doubleLogin", doubleLogin);
		json.addProperty("whiteListEnabled", whiteListEnabled);

		JsonArray motds = new JsonArray();
		if (ServerStatus.motds.isEmpty())
			for (int i = 0; i < 5; i++)
				motds.add(new JsonPrimitive("§" + (i + 3) + "Powered by §cR§6a§et §aP§br§9o§dx§5y§r"));
		else
			ServerStatus.motds.forEach(motd -> motds.add(new JsonPrimitive(motd.text)));
		json.add("motds", motds);

		JsonArray list = new JsonArray();
		if (ServerStatus.list.isEmpty())
			for (int i = 0; i < 5; i++)
				list.add(new JsonPrimitive("§" + (i + 2) + "Line " + i));
		else
			ServerStatus.list.forEach(line -> list.add(new JsonPrimitive(line.name)));
		json.add("list", list);
		return gson.toJson(json);
	}
}
