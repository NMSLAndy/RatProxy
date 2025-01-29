package Utils.Player;

import java.util.ArrayList;
import java.util.UUID;

import Utils.Config.Config;

public class PlayerManager {

	private static final ArrayList<UUID> whiteList = new ArrayList<UUID>();
	private static final ArrayList<Player> players = new ArrayList<Player>();

	public static boolean join(String name, UUID uuid, int protocolVersion) {
		if (Config.whiteListEnabled && !whiteList.contains(uuid))
			return false;
		Player player = new Player(name, uuid, protocolVersion);
		if (!players.contains(player)) {
			players.add(player);
			return true;
		}
		return false;
	}

	public static void quit(String name, UUID uuid, int protocolVersion) {
		Player player = new Player(name, uuid, protocolVersion);
		if (players.contains(player))
			players.remove(player);
	}

	public static int getPlayerCount() {
		return players.size();
	}
}
