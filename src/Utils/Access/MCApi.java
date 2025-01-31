package Utils.Access;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Utils.File.Log.Log;

public class MCApi {

	public static String getUUID(String playerName) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
		try (InputStream input = fetch(url)) {
			JsonObject json = new JsonParser().parse(new InputStreamReader(input, StandardCharsets.UTF_8))
					.getAsJsonObject();
			return strip(json.get("id").getAsString());
		} catch (Exception e) {
			Log.error("Can not fetch uuid for player: " + playerName + ".");
			return null;
		}
	}

	private static InputStream fetch(String url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
		connection.setRequestMethod("GET");
		connection.setDoOutput(true);
		return connection.getInputStream();
	}

	private static String strip(String uuid) {
		return insert(uuid, new int[] { 8, 12, 16, 20 });
	}

	private static String insert(String str, int[] positions) {
		StringBuilder sb = new StringBuilder(str);
		int offset = 0;
		for (int pos : positions) {
			sb.insert(pos + offset, "-");
			offset++;
		}
		return sb.toString();
	}
}
