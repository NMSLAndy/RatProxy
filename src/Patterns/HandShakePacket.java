package Patterns;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

import Utils.BufferUtils;

public class HandShakePacket extends PacketPattern<HandShakePacket> {

	private int protocolVersion;
	private String host;
	private boolean FML;
	private int port;
	private int state;
	private int infoLength;
	private String name = null;
	private UUID uuid = null;// 只有高版本不是null

	public HandShakePacket() {
		// TODO 自动生成的构造函数存根
	}

	public HandShakePacket(int version, String host, boolean FML, int port, int nextState, String name, UUID uuid) {
		this.protocolVersion = version;
		this.host = host;
		this.FML = FML;
		this.port = port;
		this.state = nextState;
		this.name = name;
		this.uuid = uuid;
	}

	public HandShakePacket(HandShakePacket c00) {
		this.protocolVersion = c00.protocolVersion;
		this.host = c00.host;
		this.port = c00.port;
		this.state = c00.state;
		this.name = c00.name;
		this.infoLength = c00.infoLength;
	}

	@Override
	public HandShakePacket read(ByteBuffer buffer) {
		try {
			this.length = BufferUtils.readVarInt(buffer);
			this.packetId = BufferUtils.readVarInt(buffer);
			this.protocolVersion = BufferUtils.readVarInt(buffer);
			this.host = BufferUtils.readString(buffer, 255);
			if (host.equals(""))
				return null;
			if (host.contains("FML"))
				this.FML = true;
			this.port = buffer.getShort();
			this.state = BufferUtils.readVarInt(buffer);
			this.infoLength = BufferUtils.readVarInt(buffer);
			if (!BufferUtils.hasRemaining(buffer)) {
				return this;
			} else {
				buffer.position(buffer.position() + 1);
				int nameLength = buffer.get();
				this.name = BufferUtils.readStringASCII(buffer, nameLength);
				if (BufferUtils.hasRemaining(buffer))
					this.uuid = BufferUtils.readUUID(buffer);
				return !BufferUtils.hasRemaining(buffer) ? this : null;
			}
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public ByteBuffer construct() throws IOException {
		if (this.FML && !this.host.contains("\u0000FML\u0000"))
			this.host += "\u0000FML\u0000";
		int len = 1 + 2 + host.getBytes().length + 2 + 1 + (this.protocolVersion > 110 ? 1 : 0);
		ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
		BufferUtils.write(buffer, len, 0x00, this.protocolVersion, this.host, (short) this.port, this.state);
		if (this.name != null) {
			buffer.put((byte) (2 + this.name.getBytes().length + (this.uuid != null ? 16 : 0)));
			buffer.put((byte) 0);
			BufferUtils.writeString(buffer, this.name);
			if (this.uuid != null)
				BufferUtils.writeUUID(buffer, uuid);
			buffer.flip();
		} else {
			buffer.put((byte) 1);
			buffer.flip();
			buffer.limit(buffer.limit() + 1);
		}
		return buffer;
	}

	@Override
	public String toString() {
		String pn = this.getClass().getName();
		String len = "Packet Length: " + this.length;
		String ver = "Protocol Version: " + this.protocolVersion;
		String host = "Host: " + this.host;
		String FML = "FML: " + this.FML;
		String port = "Port: " + this.port;
		String next = "Next: " + this.state;
		String idk = "Info Length: " + this.infoLength;
		String name = "Name: " + this.name;
		String uuid = "UUID: " + this.uuid;
		return pn + "\r\n" + len + "\r\n" + ver + "\r\n" + host + "\r\n" + port + "\r\n" + FML + "\r\n" + next + "\r\n"
				+ idk + "\r\n" + name + "\r\n" + uuid;
	}

	public String getName() {
		return name;
	}

	public String getHost() {
		return host;
	}

	public boolean isFML() {
		return FML;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getState() {
		return state;
	}

	public int getPort() {
		return port;
	}

	public int getProtocolVersion() {
		return protocolVersion;
	}
}
