package xyz.Melody.Handlers;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import xyz.Melody.Main;
import xyz.Melody.Main.HostContext;
import xyz.Melody.PlayerManager;
import xyz.Melody.Codec.Disconnect;
import xyz.Melody.Codec.Handshake;
import xyz.Melody.Codec.Handshake.HandshakeResult;
import xyz.Melody.Codec.ServerStatus;
import xyz.Melody.Utils.BufferUtils;

public class ClientHandler extends Handler {

	private String name;
	private Thread s2cThread;

	public ClientHandler(SocketChannel clientChannel, SocketChannel serverChannel, Connection connection) {
		super(clientChannel, serverChannel, connection);
	}

	@Override
	public void run() {
		try {
			HandshakeResult result = null;
			if (clientChannel.read(buffer) != -1) {
				buffer.flip();
				result = Handshake.tryDecode(buffer);
				if (result == null || !result.isComplete())
					return;
				if (result.nextState() == 2) { // check if its a login connection
					this.name = result.loginInfo().name();
					if (PlayerManager.getCount() >= Main.getMaxPlayers()) { // check player count
						System.out.println("Denied " + this.name + ", reason: reached max player counts.");
						this.clientChannel.write(Disconnect.playerLimit());
						this.name = null;
						return;
					}
					this.s2cThread = Thread.startVirtualThread(new S2CForwarder(this));
					// if all good, start server to client forwarding
					System.out.println(this.name + " joined.");
				}
				HostContext hostCtx = Main.getHostContext(); // includes the rewritten host and port
				if (hostCtx != null) {
					ByteBuffer buf = Handshake.encode(result.protocol(), hostCtx.maskHost(), result.isFML(),
							hostCtx.maskPort(), result.nextState(), result.loginInfo());
					serverChannel.write(buf);
				} else
					serverChannel.write(buffer); // if host context is null, that means the host rewriting is disabled
				buffer.clear();
			}

			if (result.nextState() == 1) { // Handle ping
				int rcvedLen = 0;
				int totalLen = -1;
				while (serverChannel.read(buffer) != -1) { // Ignored server motd then send custom motd
					int position = buffer.position();
					buffer.flip();
					if (totalLen == -1)
						totalLen = BufferUtils.readVarInt(buffer) + 2; // +2 includes the packet_length and packet_id
					rcvedLen += position;
					buffer.clear();
					if (rcvedLen >= totalLen)
						break;
				}
				this.clientChannel.write(ServerStatus.constS00(result.protocol())); // send custom motd
				if (clientChannel.read(buffer) != -1) { // get and send client ping to server
					buffer.flip();
					serverChannel.write(buffer);
					buffer.clear();
				}
				if (serverChannel.read(buffer) != -1) { // return the server pong to client
					buffer.flip();
					clientChannel.write(buffer);
					buffer.clear();
				}
			} else if (result.nextState() == 2) { // once player joined the server, this will handle the play life cycle
				while (clientChannel.read(buffer) != -1) {
					buffer.flip();
					serverChannel.write(buffer);
					buffer.clear();
				}
			}
		} catch (Exception e) {
			buffer.clear();
			e.printStackTrace();
		} finally {
			if (this.name != null)
				System.out.println(this.name + " disconnected.");
			try {
				if (this.s2cThread != null) {
					this.s2cThread.interrupt();
					this.s2cThread.join();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		super.run();
	}
}
