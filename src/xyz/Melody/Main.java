package xyz.Melody;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.InitialDirContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import xyz.Melody.Codec.ServerStatus;
import xyz.Melody.Handlers.Connection;
import xyz.Melody.Utils.Icons;

public class Main {

	private static File mainDir;
	private static File configFile;
	private static Config config;

	public static final String RAT_PROXY = "§c§lR§6§la§e§lt §a§lP§b§lr§9§lo§d§lx§5§ly§r";

	public static void main(String[] args) {
		try {
			mainDir = new File(new File("").getCanonicalPath(), "Rat-Proxy");
			if (!mainDir.exists())
				mainDir.mkdir();
			configFile = new File(mainDir, "Config.json");
			if (!configFile.exists()) {
				configFile.createNewFile();
				config = createDefaultConfig();
				save();
				System.out.println("Check 'Rat-Proxy/Config.json' before launching again.");
				return;
			} else
				config = loadConfig();
			Icons.readIcons();
			ServerStatus.init(config);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (config == null) {
			System.out.println("the field 'config' can not be null.");
			return;
		}

		String host = config.targetHost;
		Hashtable<String, String> hashtable = new Hashtable<String, String>();
		hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
		hashtable.put("java.naming.provider.url", "dns:");
		try {
			Attribute attribute = (new InitialDirContext(hashtable))
					.getAttributes("_Minecraft._tcp." + host, new String[] { "SRV" }).get("srv");
			if (attribute != null) {
				String[] redir = attribute.get().toString().split(" ", 4);
				config.targetHost = redir[3];
				config.targetPort = Integer.parseInt(redir[2]);
			}
		} catch (Exception e) {
			// owo
		}
		startListening();
	}

	public static File getMainDir() {
		return mainDir;
	}

	public static HostContext getHostContext() {
		if (config.rewriteHost)
			return new HostContext(config.rewrittenHost, (char) config.rewrittenPort);
		return null;
	}

	public static int getMaxPlayers() {
		return config.maxPlayers;
	}

	private static void startListening() {
		try (ServerSocketChannel proxyChannel = ServerSocketChannel.open()) {
			proxyChannel.socket().bind(new InetSocketAddress(config.srcPort));
			while (true) {
				try {
					SocketChannel clientChannel = proxyChannel.accept();
					Thread.ofVirtual().start(new Connection(config.targetHost, config.targetPort, clientChannel));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void save() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (FileWriter writer = new FileWriter(configFile)) {
			gson.toJson(config, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Config createDefaultConfig() {
		Config cfg = new Config();
		cfg.srcPort = 25565;
		cfg.targetHost = "mc.hypixel.net";
		cfg.targetPort = 25565;
		cfg.rewriteHost = true;
		cfg.rewrittenHost = "abc.hypixel.net";
		cfg.rewrittenPort = 25565;
		cfg.maxPlayers = 10;
		cfg.doubleLogin = false;
		cfg.whiteListEnabled = false;
		List<String> motds = new ArrayList<String>();
		motds.add("\u00a7aPowered by Melody Proxy");
		motds.add("\u00a7bPowered by Melody Proxy");
		motds.add("\u00a7cPowered by Melody Proxy");
		motds.add("\u00a7dPowered by Melody Proxy");
		cfg.motds = motds;

		List<String> list = new ArrayList<String>();
		list.add("\u00a71M\u00a72e\u00a73l\u00a74o\u00a75d\u00a76y\u00a77-\u00a78P\u00a79r\u00a7ao\u00a7bx\u00a7cy");
		list.add("\u00a7kA\u00a7rExample line 1.\u00a7kA");
		list.add("\u00a7kA\u00a7rExample line 2.\u00a7kA");
		cfg.list = list;
		return cfg;
	}

	private static Config loadConfig() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (FileReader reader = new FileReader(configFile)) {
			return gson.fromJson(reader, Config.class);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public record HostContext(String maskHost, char maskPort) {

	}
}
