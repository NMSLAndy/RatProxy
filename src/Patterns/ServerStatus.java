package Patterns;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;

import Utils.BufferUtils;
import Utils.Config.Config;
import Utils.Player.PlayerManager;

public class ServerStatus {

	private static final Gson gson = new Gson();
	public static ArrayList<PlayerObj> list = new ArrayList<PlayerObj>();
	public static Description motd = new Description("");

	public static ByteBuffer constS00(int version) throws IOException {
		String json = getStatus(version);
		byte[] motdBytes = json.getBytes();
		int length = motdBytes.length + BufferUtils.getVarIntSize(motdBytes.length);
		ByteBuffer buffer = ByteBuffer.allocate(length + 3);
		BufferUtils.write(buffer, length + 1, 0x00, json);
		buffer.flip();
		return buffer;
	}

	public static String getStatus(int ver) {
		Version version = new Version("§cR§6a§et §aP§br§9o§dx§5y§r", ver);
//		List<Player> list = new ArrayList<>();
//		addLine( "§7(§r");
//		addLine( "§7(§r                       Page 1 of 1  ");
//		addLine( "§7(§r");
//		addLine( "§7(§r Your Skyblock profile §6Pear");
//		addLine( "§7(§r §chas §cbeen ratted §ras a co-op");
//		addLine( "§7(§r member was determined to");
//		addLine( "§7(§r use melodysky.");
//		addLine( "§7(§r");
//		addLine( "§7(§r If you believe this to be in");
//		addLine( "§7(§r error, you can contact our");
//		addLine( "§7(§r support team:");
//		addLine( "§7(§r          §9§nmelodysky.xyz");
//		addLine( "§7(§r");
//		addLine( "§7(§r               §2§lDISMISS");
		Players players = new Players(Config.maxPlayers, PlayerManager.getPlayerCount(), list);
//		Description description = new Description(
//				"Your Skyblock Profile §6Pear §chas been ratted §ras a co-op member was determined to use §cR§6a§et §aP§br§9o§dx§5y§r.");
		String favicon = Config.getFavIcon();
		ServerStatus serverInfo = new ServerStatus(version, players, motd, favicon);
		String json = gson.toJson(serverInfo);
		return json;
	}

	public static void addLine(String content) {
		list.add(new PlayerObj(content, UUID.randomUUID().toString()));
	}

	public Version version;
	public Players players;
	public Description description;
	public String favicon;

	public ServerStatus(Version version, Players players, Description description, String favicon) {
		this.version = version;
		this.players = players;
		this.description = description;
		this.favicon = favicon;
	}

	public static class Version {
		public String name;
		public int protocol;

		public Version(String name, int protocol) {
			this.name = name;
			this.protocol = protocol;
		}
	}

	public static class PlayerObj {
		public String name;
		public String id;

		public PlayerObj(String name, String id) {
			this.name = name;
			this.id = id;
		}
	}

	public static class Players {
		public int max;
		public int online;
		public List<PlayerObj> sample;

		public Players(int max, int online, List<PlayerObj> sample) {
			this.max = max;
			this.online = online;
			this.sample = sample;
		}
	}

	public static class Description {
		public String text;

		public Description(String text) {
			this.text = text;
		}
	}
}
