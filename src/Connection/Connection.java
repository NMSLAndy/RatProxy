package Connection;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Phaser;

import Connection.Handlers.C2SHandler;
import Connection.Handlers.S2CHandler;
import Utils.File.Log.Log;

public class Connection implements Runnable {

	private final String dstIP;
	private final int dstPort;
	private final SocketChannel sourceChannel;

	public Connection(String dstIP, int dstPort, SocketChannel sourceChannel) {
		this.dstIP = dstIP;
		this.dstPort = dstPort;
		this.sourceChannel = sourceChannel;
	}

	@Override
	public void run() {
		try (SocketChannel destinationChannel = SocketChannel.open(new InetSocketAddress(dstIP, dstPort))) {
			Phaser phaser = new Phaser(2);
			C2SHandler c2s = new C2SHandler(sourceChannel, destinationChannel, phaser);
			S2CHandler s2c = c2s.createS2C();

			Thread.ofVirtual().start(c2s);
			Thread.ofVirtual().start(s2c);
//			while (phaser.getUnarrivedParties() > 0) {
//
//			}
			phaser.awaitAdvance(0);
		} catch (Exception e) {
			Log.saveException(e);
		} finally {
			closeChannel(sourceChannel);
		}
	}

	private void closeChannel(SocketChannel channel) {
		if (channel != null && channel.isOpen()) {
			try {
				channel.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
