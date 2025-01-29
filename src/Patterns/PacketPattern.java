package Patterns;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class PacketPattern<T> {

	public int packetId;

	public int length;

	public abstract T read(ByteBuffer buffer);

	public abstract ByteBuffer construct() throws IOException;

	@Override
	public String toString() {
		return super.toString();
	}

}
