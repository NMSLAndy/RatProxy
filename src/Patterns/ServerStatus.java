package Patterns;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import Utils.BufferUtils;
import Utils.Config.Config;
import Utils.Player.PlayerManager;

public class ServerStatus {

	private static final Gson gson = new Gson();
	public static volatile ArrayList<PlayerObj> list = new ArrayList<PlayerObj>();
	public static volatile ArrayList<Description> motds = new ArrayList<Description>();

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
		Version version = new Version(Config.ratProxy, ver);
		Players players = new Players(Config.maxPlayers, PlayerManager.getPlayerCount(), list);
		String favicon = Config.getFavIcon();
		ServerStatus serverInfo = new ServerStatus(version, players, getMotd(), favicon);
		String json = gson.toJson(serverInfo);
		return json;
	}

	public static Description getMotd() {
		if (motds.size() == 0)
			return new Description("Powered by " + Config.ratProxy);
		int index = ThreadLocalRandom.current().nextInt(0, motds.size());
		return motds.get(index);
	}

	public static void addLine(String content) {
		list.add(new PlayerObj(content, UUID.randomUUID().toString()));
	}

	public static void addMOTD(String content) {
		motds.add(new Description(content));
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

		@Override
		public String toString() {
			return name;
		}

		@Override
		public int hashCode() {
			return Objects.hash(name);
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

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Description))
				return false;
			return text.equals(((Description) obj).text);
		}

		@Override
		public String toString() {
			return text;
		}

		@Override
		public int hashCode() {
			return Objects.hash(text);
		}
	}
}
