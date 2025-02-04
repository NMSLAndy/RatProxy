package Connection.Handlers;

import java.nio.channels.SocketChannel;
import Connection.Handler;
import Connection.ConnectionState.States;
import Patterns.ServerStatus;
import Utils.BufferUtils;
import Utils.File.Log.Log;

public class S2CHandler extends Handler {

	private C2SHandler c2s;

	public S2CHandler(SocketChannel sourceChannel, SocketChannel targetChannel, C2SHandler c2s) {
		super(sourceChannel, targetChannel);
		this.c2s = c2s;
	}

	@Override
	public void run() {
		try {
			long time = System.currentTimeMillis();
			synchronized (this) {
				while (c2s.state.is(States.NONE) || c2s.state.is(States.HandShake)) {
					if (System.currentTimeMillis() - time > 3000)
						break;
					this.wait(5);
				}
			}
			if (c2s.state.is(States.Play)) {
				while (sourceChannel.read(buffer) != -1) {
					buffer.flip();
					targetChannel.write(buffer);
					buffer.clear();
				}
			} else {
				int totalLen = -1;
				int length = 0;
				while (sourceChannel.read(buffer) != -1) {
					buffer.flip();
					if (c2s.state.is(States.Ping)) {
						if (totalLen == -1)
							totalLen = BufferUtils.readVarInt(buffer);
						length += buffer.remaining();
						if (length >= totalLen)
							targetChannel.write(ServerStatus.constS00(c2s.result.version()));
						buffer.clear();
						continue;
					} else if (c2s.state.is(States.Pong)) {
						if (buffer.get(2) != 0) {
							buffer.clear();
							continue;
						}
						targetChannel.write(buffer);
						buffer.clear();
						c2s.state.set(States.Disconnect);
						continue;
					}
				}
			}
		} catch (Exception e) {
			Log.saveException(e);
			buffer.clear();
		} finally {
			this.c2s.phaser.arrive();
		}
		super.run();
	}
}
