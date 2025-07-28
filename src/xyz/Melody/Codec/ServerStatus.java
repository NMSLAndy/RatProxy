package xyz.Melody.Codec;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;

import xyz.Melody.Config;
import xyz.Melody.Main;
import xyz.Melody.PlayerManager;
import xyz.Melody.Utils.BufferUtils;
import xyz.Melody.Utils.Icons;

public class ServerStatus {

	private static final Gson gson = new Gson();
	private static volatile ArrayList<PlayerObj> list = new ArrayList<PlayerObj>();
	private static volatile ArrayList<Description> motds = new ArrayList<Description>();

	public static void init(Config cfg) { // read the list and motds from the config
		for (String line : cfg.list)
			list.add(new PlayerObj(line, UUID.randomUUID().toString()));
		for (String line : cfg.motds)
			motds.add(new Description(line));
	}

	public static ByteBuffer constS00(int version) throws IOException {
		String json = getStatus(version);
		byte[] motdBytes = json.getBytes(); // json bytes length
		int length = 1 + motdBytes.length + BufferUtils.getVarIntSize(motdBytes.length);// packet length
		ByteBuffer buffer = ByteBuffer.allocate(length + 2);
		BufferUtils.writeVarInt(buffer, length); // write packet length
		buffer.put((byte) 0x00); // write packet id
		BufferUtils.writeString(buffer, json);// write motd json
		buffer.flip();
		return buffer;
	}

	private static String getStatus(int ver) {
		Version version = new Version(Main.RAT_PROXY, ver);
		Players players = new Players(Main.getMaxPlayers(), PlayerManager.getCount(), list);
		String favicon = Icons.getFavIcon();
		ServerStatus serverInfo = new ServerStatus(version, players, getMotd(), favicon);
		String json = gson.toJson(serverInfo);
		return json;
	}

	private static Description getMotd() {
		if (motds.size() == 0)
			return new Description("Powered by " + Main.RAT_PROXY);
		int index = ThreadLocalRandom.current().nextInt(0, motds.size());
		return motds.get(index);
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
