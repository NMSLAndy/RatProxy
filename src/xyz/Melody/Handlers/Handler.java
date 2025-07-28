package xyz.Melody.Handlers;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Handler implements Runnable, AutoCloseable {

	public SocketChannel clientChannel;
	public SocketChannel serverChannel;
	public ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
	public Connection connection;

	public Handler(SocketChannel clientChannel, SocketChannel serverChannel, Connection connection) {
		this.clientChannel = clientChannel;
		this.serverChannel = serverChannel;
		this.connection = connection;
	}

	public void closeSocket() {
		try {
			if (clientChannel != null && clientChannel.isOpen())
				clientChannel.close();
			if (serverChannel != null && serverChannel.isOpen())
				serverChannel.close();
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
