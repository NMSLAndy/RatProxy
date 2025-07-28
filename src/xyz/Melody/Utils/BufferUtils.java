package xyz.Melody.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BufferUtils {

	public static void printBuffer(ByteBuffer buffer) {
		int limit = buffer.limit();
		byte[] arr = readBytes(buffer, 16 * 1024).array();
		for (byte b : arr)
			System.out.print(b + " ");
		System.out.println();
		buffer.position(0);
		buffer.limit(limit);
	}

	public static int getVarIntSize(int value) {
		int size = 0;
		do {
			value >>>= 7;
			size++;
		} while (value != 0);
		return size;
	}

	public static int readVarInt(ByteBuffer buffer) {
		int value = 0;
		int position = 0;
		while (true) {
			byte currentByte = buffer.get();
			value |= (currentByte & 0x7F) << (position * 7);
			if (position > 5)
				throw new RuntimeException("VarInt too big");
			if ((currentByte & 0x80) == 0)
				break;
			position++;
		}
		return value;
	}

	public static void writeString(ByteBuffer buffer, String str) {
		byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
		writeVarInt(buffer, strBytes.length); // 写入String长度
		buffer.put(strBytes); // 写String bytes
	}

	public static String readString(ByteBuffer buffer) {
		int strLen = readVarInt(buffer);
		ByteBuffer bytes = readBytes(buffer, strLen);
		return StandardCharsets.UTF_8.decode(bytes).toString();
	}

	public static void writeUUID(ByteBuffer buffer, UUID uuid) {
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
	}

	public static void writeVarInt(ByteBuffer buffer, int value) {
		while ((value & 0xFFFFFF80) != 0) {
			buffer.put((byte) ((value & 0x7F) | 0x80));
			value >>>= 7;
		}
		buffer.put((byte) value);
	}

	public static ByteBuffer readBytes(ByteBuffer buffer, int maxLen) {
		int length = Math.min(maxLen, buffer.remaining());
		ByteBuffer slice = ByteBuffer.allocate(length);

		byte[] temp = new byte[length];
		buffer.get(temp);
		slice.put(temp);

		slice.flip();
		return slice;
	}
}
