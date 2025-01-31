package Utils.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import Utils.File.Log.Log;

public class PlayerManager {

	private static final Set<Player> players = ConcurrentHashMap.newKeySet();

	public static boolean join(String name, UUID uuid, int protocolVersion) {
		Log.saveJoin(name);
		Player player = new Player(name, uuid, protocolVersion);
		if (!players.contains(player)) {
			players.add(player);
			return true;
		}
		return false;
	}

	public static void quit(String name, UUID uuid, int protocolVersion) {
		Log.saveQuit(name);
		players.removeIf(player -> player.getName().equals(name) || player.getUuid().equals(uuid));
	}

	public static int getPlayerCount() {
		return players.size();
	}
}
