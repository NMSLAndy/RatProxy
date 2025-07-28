package xyz.Melody.Handlers;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class Connection implements Runnable {

	private String destHost;
	private int dstPort;
	private SocketChannel clientChannel;
	private SocketChannel serverChannel;

	public Connection(String destHost, int dstPort, SocketChannel clientChannel) {
		this.destHost = destHost;
		this.dstPort = dstPort;
		this.clientChannel = clientChannel;
	}

	@Override
	public void run() {
		try (SocketChannel serverChannel = SocketChannel.open(new InetSocketAddress(destHost, dstPort))) {
			this.serverChannel = serverChannel;
			ClientHandler clientHandler = new ClientHandler(clientChannel, serverChannel, this);
			Thread client = Thread.startVirtualThread(clientHandler);
			client.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeConnection();
		}
	}

	private void closeConnection() {
		try {
			if (clientChannel != null && clientChannel.isOpen())
				clientChannel.close();
			if (serverChannel != null && serverChannel.isOpen())
				serverChannel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
