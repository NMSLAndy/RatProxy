package Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import Exceptions.DecoderException;

public class BufferUtils {

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

	public static int getVarIntSize(int value) {
		int size = 0;
		do {
			value >>>= 7;
			size++;
		} while (value != 0);
		return size;
	}

	public static void write(ByteBuffer buffer, Object... items) {
		for (Object item : items) {
			if (item instanceof Boolean) {
				buffer.put((byte) ((Boolean) item ? 1 : 0));
			} else if (item instanceof byte[]) {
				byte[] byteArray = (byte[]) item;
				writeVarInt(buffer, byteArray.length);
				buffer.put(byteArray);
			} else if (item instanceof String) {
				writeString(buffer, (String) item);
			} else if (item instanceof Byte) {
				buffer.put((Byte) item);
			} else if (item instanceof Short) {
				buffer.putShort((Short) item);
			} else if (item instanceof Integer) {
				writeVarInt(buffer, (int) item);
			} else if (item instanceof Long) {
				buffer.putLong((Long) item);
			}
		}
	}

	public static void writeString(ByteBuffer buffer, String str) {
		byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
		writeVarInt(buffer, strBytes.length);
		buffer.put(strBytes);
	}

	public static void writeVarInt(ByteBuffer buffer, int value) {
		while ((value & 0xFFFFFF80) != 0) {
			buffer.put((byte) ((value & 0x7F) | 0x80));
			value >>>= 7;
		}
		buffer.put((byte) value);
	}

	public static String readString(ByteBuffer buffer) {
		int startPos = buffer.position();
		while (buffer.hasRemaining()) {
			byte b = buffer.get();
			if (b == 0) {
				int endPos = buffer.position() - 1;
				buffer.position(startPos);
				byte[] bytes = new byte[endPos - startPos];
				buffer.get(bytes);
				buffer.get();
				return new String(bytes, StandardCharsets.UTF_8);
			}
		}
		buffer.position(startPos);
		byte[] remainingBytes = new byte[buffer.remaining()];
		buffer.get(remainingBytes);
		return new String(remainingBytes, StandardCharsets.UTF_8);
	}

	public static void printBuffer(ByteBuffer buffer) {
		int limit = buffer.limit();
		byte[] arr = readBytes(buffer, 16 * 1024).array();
		for (byte b : arr)
			System.out.print(b + " ");
		System.out.println();
		buffer.position(0);
		buffer.limit(limit);
	}

	public static String readString(ByteBuffer buffer, int maxLength) throws DecoderException {
		int length = readVarInt(buffer);
		if (length > maxLength * 4)
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed ("
					+ length + " > " + maxLength * 4 + ")");
		else if (length < 0)
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");

		ByteBuffer stringBuffer = readBytes(buffer, length);
		String result = StandardCharsets.UTF_8.decode(stringBuffer).toString();

		if (result.length() > maxLength)
			throw new DecoderException("The received string length is longer than maximum allowed (" + result.length()
					+ " > " + maxLength + ")");
		return result;
	}

	public static String readStringASCII(ByteBuffer buffer, int length) throws DecoderException {
		if (length < 0)
			throw new DecoderException("大胆! length竟然小于0?");
		if (length > buffer.remaining())
			throw new DecoderException("Buffer剩余位数小于length");
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		return new String(bytes, StandardCharsets.US_ASCII);
	}

	public static UUID readUUID(ByteBuffer buffer) {
		if (buffer.remaining() < 16) {
			throw new IllegalArgumentException("ByteBuffer剩余至少为16位");
		}

		long mostSigBits = buffer.getLong();
		long leastSigBits = buffer.getLong();
		return new UUID(mostSigBits, leastSigBits);
	}

	public static void writeUUID(ByteBuffer buffer, UUID uuid) {
		if (buffer.remaining() < 16) {
			throw new IllegalArgumentException("ByteBuffer剩余空间必须大于16位");
		}
		buffer.putLong(uuid.getMostSignificantBits());
		buffer.putLong(uuid.getLeastSignificantBits());
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

	public static boolean hasRemaining(ByteBuffer buffer) {
		if (!buffer.hasRemaining())
			return false;

		int pos = buffer.position();
		int i = 0;
		int sum = 0;
		while (buffer.hasRemaining()) {
			sum += buffer.get();
			i++;
			if (i >= 5)
				break;
		}
		buffer.position(pos);
		return sum != 0;
	}
}
