package xyz.Melody.Codec;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import xyz.Melody.Utils.BufferUtils;

public class Handshake {

	public static final String FML = "\u0000FML\u0000"; // the sign of forge mod loader

	public static HandshakeResult tryDecode(ByteBuffer buffer_) {
		ByteBuffer buffer = buffer_.duplicate();
		BufferUtils.readVarInt(buffer); // skip packet length
		int packetId = BufferUtils.readVarInt(buffer); // read the handshake packet id, is should be 0
		if (packetId != 0)
			return null;
		int protocol = BufferUtils.readVarInt(buffer); // read the protocol version
		String host = BufferUtils.readString(buffer); // read the server address that player connected to
		boolean isFML = false;
		if (host.contains(FML)) {
			host = host.replaceAll(FML, "");
			isFML = true;
		}
		int port = (int) buffer.getChar(); // read the server port
		int nextState = BufferUtils.readVarInt(buffer); // read next state(1 = status, 2 = login)

		if (nextState == 1) // its a status request!
			return new HandshakeResult(protocol, host, isFML, port, nextState, null);
		else if (nextState != 2)
			return null; // if neither 1 or 2 then its a illegal handshake

		BufferUtils.readVarInt(buffer); // skip packet length
		int loginPacketID = BufferUtils.readVarInt(buffer);// read the login start packet id, it should either be 0
		if (loginPacketID != 0)
			return null;
		String name = BufferUtils.readString(buffer); // read player name
		UUID uuid = null;
		// read UUID (only exists after minecraft 1.19, protocol version >= 761)
		if (buffer.hasRemaining()) {
			long most = buffer.getLong();
			long least = buffer.getLong();
			uuid = new UUID(most, least);
		}
		LoginInfo info = new LoginInfo(name, uuid);
		return new HandshakeResult(protocol, host, isFML, port, nextState, info);
	}

	public static ByteBuffer encode(int protocol, String host, boolean isFML, char port, int nextState,
			LoginInfo info) {
		host = host + (isFML ? FML : ""); // check if the client brand is fml, then add the FML signature
		ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
		int hostLength = StandardCharsets.UTF_8.encode(host).array().length;
		// gets the length of the host name(includes the FML sign)
		int length = BufferUtils.getVarIntSize(0) + BufferUtils.getVarIntSize(protocol)
				+ BufferUtils.getVarIntSize(hostLength) + hostLength + 1 + BufferUtils.getVarIntSize(nextState);
		// calculate the whole packet sizes↑, then write to the packet↓
		BufferUtils.writeVarInt(buffer, length);
		buffer.put((byte) 0); // write packet id
		BufferUtils.writeVarInt(buffer, protocol); // write protocol version, for example: 1.8.9 => 47
		BufferUtils.writeString(buffer, host); // write the host name(includes the FML sign)
		buffer.putChar(port); // write the port
		// java has no unsigned short, so I used char (0-65535) to replace it
		BufferUtils.writeVarInt(buffer, nextState); // write next state

		if (nextState != 2) { // if not login, write a empty login start packet
			buffer.put((byte) 1);
			buffer.put((byte) 0);
			return buffer.flip();
		}

		String name = info.name;
		UUID uuid = info.uuid;

		int nameLength = StandardCharsets.UTF_8.encode(name).array().length; // the length of player name
		int loginLength = BufferUtils.getVarIntSize(nameLength) + nameLength + (uuid != null ? 16 : 0);
		// calculate the whole packet sizes↑, then write to the packet↓
		BufferUtils.writeVarInt(buffer, loginLength);
		buffer.put((byte) 0); // write packet id
		BufferUtils.writeString(buffer, name); // write player name
		if (uuid != null)
			BufferUtils.writeUUID(buffer, uuid); // write player uuid(if exists)
		return buffer.flip();
	}

	// what is this
	public record HandshakeResult(int protocol, String host, boolean isFML, int port, int nextState,
			LoginInfo loginInfo) {

		public boolean isComplete() {
			boolean loginCompleted = switch (nextState) {
			case 1 -> true;
			case 2 -> loginInfo.name != null && !loginInfo.name.isEmpty() && !loginInfo.name.isBlank()
					&& (protocol >= 761 ? loginInfo.uuid != null : true);
			default -> false;
			};
			return host != null && !host.isEmpty() && !host.isBlank() && port >= 0 && port <= 65535
					&& (nextState == 1 || nextState == 2) && loginCompleted;
		}

		@Override
		public final String toString() {
			String next = switch (nextState) {
			case 1 -> "Status";
			case 2 -> "Login";
			default -> "UNKNOWN";
			};
			return "Protocol: " + protocol + " | Entry: " + host + ":" + port + " | Next state: " + next + " | isFML: "
					+ isFML;
		}
	}

	public record LoginInfo(String name, UUID uuid) {

	}
}
