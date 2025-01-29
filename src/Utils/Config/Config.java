package Utils.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.concurrent.ThreadLocalRandom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import Patterns.ServerStatus;
import Patterns.ServerStatus.Description;
import Utils.File.FileManager;
import Utils.File.FavIcon.IconManager;

import static Patterns.ServerStatus.addLine;

public class Config {

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
	public static final String name = "Rat Proxy";
	public static final String version = "beta";
	public static int maxPlayers = 0;
	public static boolean whiteListEnabled = false;

	public static int srcPort;

	public static String targetHost;
	public static int targetPort;

	public static boolean rewriteHost;
	public static String rewrittenHost;
	public static int rewrittenPort;

	public static final String[] rat = new String[] { "                  ..----.._    _              ",
			"                .' .--.    '-.(O)_            ", "    '-.__.-'''=:|  ,  _)_ |__ . c\\'-..        ",
			"                 ''------'---''---'-'     " };

	public static String getFavIcon() {
		int index = ThreadLocalRandom.current().nextInt(0, IconManager.favIcons.size());
		return IconManager.favIcons.get(index);
	}

	public static void readConfig() {
		try {
			String json_ = readFile();
			if (json_.length() <= 1) {
				write(createDefaultCFG());
				System.out.println("How 2 use?");
				System.out.println("https://github.com/NMSLAndy/RatProxy");
				System.exit(-1);
			}
			CFGPattern config = gson.fromJson(json_, CFGPattern.class);
			srcPort = config.srcPort;

			targetHost = config.targetHost;
			targetPort = config.targetPort;

			rewriteHost = config.rewriteHost;
			rewrittenHost = config.rewrittenHost;
			rewrittenPort = config.rewrittenPort;

			maxPlayers = config.maxPlayers;
			whiteListEnabled = config.whiteListEnabled;
			ServerStatus.motd = new Description(config.motd);
			for (String line : config.list)
				ServerStatus.addLine(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String createDefaultCFG() {
		ServerStatus.motd = new Description("");
		addLine("尼好, 这里是老鼠代理");
		addLine("Hi, this is §cR§6a§et §aP§br§9o§dx§5y§r.");
		JsonObject json = new JsonObject();
		json.addProperty("srcPort", 25565);
		json.addProperty("targetHost", "");
		json.addProperty("targetPort", 25565);
		json.addProperty("rewriteHost", false);
		json.addProperty("rewrittenHost", "");
		json.addProperty("rewrittenPort", 25565);
		json.addProperty("maxPlayers", 10);
		json.addProperty("whiteListEnabled", false);
		json.addProperty("motd", "server description - 服务器描述");
		JsonArray arr = new JsonArray();
		ServerStatus.list.forEach(player -> arr.add(new JsonPrimitive(player.name)));
		json.add("list", arr);
		return gson.toJson(json);
	}

	public static String readFile() {
		File cfg = new File(FileManager.mainDir, "Config.json");
		StringBuilder result = new StringBuilder();

		try {
			if (!cfg.exists())
				cfg.createNewFile();
			try (FileInputStream fis = new FileInputStream(cfg);
					InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
					BufferedReader br = new BufferedReader(isr)) {
				String line;
				while ((line = br.readLine()) != null)
					result.append(line).append("\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	public static void write(String content) {
		File cfg = new File(FileManager.mainDir, "Config.json");

		try {
			if (!cfg.exists())
				cfg.createNewFile();
			try (FileWriter writer = new FileWriter(cfg, false)) {
				writer.write(content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
