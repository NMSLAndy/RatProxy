package xyz.Melody.Codec;

import java.nio.ByteBuffer;

import xyz.Melody.Main;
import xyz.Melody.Utils.BufferUtils;
import xyz.Melody.Utils.Text.Message;

public class Disconnect {

	public static ByteBuffer constS40(String json) {
		byte[] motdBytes = json.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
		BufferUtils.writeVarInt(buffer, motdBytes.length + 3);
		buffer.put((byte) 0);
		BufferUtils.writeString(buffer, json);
		buffer.flip();
		return buffer;
	}

	public static ByteBuffer playerLimit() {
		Message title = title();
		title.addExtra("服务器已经达到最大人数");
		title.addExtra("");
		title.addExtra("Server has reached the maximum player count.");
		return constS40(title.construct());
	}

	public static ByteBuffer whiteList() {
		Message title = title();
		title.addExtra("你不在白名单内");
		title.addExtra("");
		title.addExtra("You are not in the whitelist.");
		return constS40(title.construct());
	}

	public static ByteBuffer rateLimit() {
		Message title = title();
		title.addExtra("请过几秒再重新连接");
		title.addExtra("");
		title.addExtra("Please relog after a few seconds.");
		return constS40(title.construct());
	}

	public static ByteBuffer reqDoubleLogin() {
		Message title = title();
		title.addExtra("请重新连接至服务器");
		title.addExtra("");
		title.addExtra("Please relogin to join the server.");
		return constS40(title.construct());
	}

	public static ByteBuffer uuidIssues() {
		Message title = title();
		title.addExtra("无法获取您的uuid, 请稍后重试");
		title.addExtra("");
		title.addExtra("Unable to fetch your uuid, please try again later.");
		return constS40(title.construct());
	}

	public static ByteBuffer custom(String... contents) {
		Message title = title();
		for (String content : contents)
			title.addExtra(content);
		return constS40(title.construct());
	}

	private static Message title() {
		Message msg = new Message();
		Message title = msg.addExtra("§7▁ ▂ ▃ ▅ ▆ - " + Main.RAT_PROXY + " §7- ▆ ▅ ▃ ▂ ▁");
		title.addExtra("");
		return title;
	}
}
