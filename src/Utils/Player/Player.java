package Utils.Player;

import java.util.UUID;

import Utils.Protocols;

public class Player {

	private String name;
	private UUID uuid;
	private int version;

	public Player(String name, UUID uuid, int version) {
		this.name = name;
		this.uuid = uuid;
		this.version = version;
	}

	public String getName() {
		return name;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getVersion() {
		return version;
	}

	@Override
	public String toString() {
		return "Player[ " + name + " : " + uuid + " : " + Protocols.getVersion(version) + "(" + version + ") ]";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Player))
			return false;
		Player p = (Player) obj;
		if (uuid != null && p.uuid != null)
			return this.name.equals(p.getName()) && this.uuid.equals(p.getUuid());
		else if (uuid == null && p.uuid == null)
			return this.name.equals(p.getName());
		else
			return false;
	}
}
