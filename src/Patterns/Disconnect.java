package Patterns;

import java.io.IOException;
import java.nio.ByteBuffer;

import Utils.BufferUtils;
import Utils.Text.Message;

public class Disconnect {

	public static ByteBuffer constS40(String json) throws IOException {
		byte[] motdBytes = json.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
		BufferUtils.write(buffer, motdBytes.length + 3, 0x00, json);
		buffer.flip();
		return buffer;
	}

	public static String playerLimit() {
		Message msg = new Message();// ━ ▎ ┗ ┛ ┏ ┓
		Message qwe = msg.addExtra("§b§l━━━━§3§l[ §f§lDisconnected §3§l]§b§l━━━━");
		qwe.addExtra("Server has reached the maximum player count.");
		return msg.construct();
	}

	public static String whiteList() {
		Message msg = new Message();
		Message qwe = msg.addExtra("§b§l━━━━§3§l[ §f§lDisconnected §3§l]§b§l━━━━");
		qwe.addExtra("You are not in the whitelist.");
		return msg.construct();
	}
}
