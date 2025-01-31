package Connection;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable, AutoCloseable {

	public SocketChannel sourceChannel;
	public SocketChannel targetChannel;
	public ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);

	public Handler(SocketChannel sourceChannel, SocketChannel targetChannel) {
		this.sourceChannel = sourceChannel;
		this.targetChannel = targetChannel;
	}

	public void closeSocket() {
		try {
			if (sourceChannel != null && sourceChannel.isOpen())
				sourceChannel.close();
			if (targetChannel != null && targetChannel.isOpen())
				targetChannel.close();
			this.buffer.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public void close() throws Exception {
		this.closeSocket();
	}
}
