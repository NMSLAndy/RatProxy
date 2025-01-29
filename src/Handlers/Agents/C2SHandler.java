package Handlers.Agents;

import java.nio.channels.SocketChannel;
import java.util.concurrent.Phaser;

import Handlers.ConnectionState;
import Handlers.Handler;
import Handlers.Agents.Processors.PHandShake;
import Handlers.Agents.Processors.PPing;
import Handlers.ConnectionState.States;
import Utils.HandShakeResult;
import Utils.File.Log.Log;
import Utils.Player.PlayerManager;

public class C2SHandler extends Handler {

	public final ConnectionState state = new ConnectionState();
	private final S2CHandler s2c;
	protected HandShakeResult result;
	protected Phaser phaser;

	public C2SHandler(SocketChannel sourceChannel, SocketChannel targetChannel, Phaser phaser) {
		super(sourceChannel, targetChannel);
		this.phaser = phaser;
		this.s2c = new S2CHandler(targetChannel, sourceChannel, this);
	}

	@Override
	public void run() {
		try {
			state.set(States.HandShake);
			result = new PHandShake(this).processUWU(this);
			if (result == null)
				return;
			if (result.state() == 1) {
				long ping = new PPing(this).processUWU(this);
				if (ping <= 0)
					return;
				state.set(States.Pong);
				targetChannel.write(buffer);
				buffer.clear();
				return;
			}
			while (sourceChannel.read(buffer) != -1) {
				buffer.flip();
				targetChannel.write(buffer);
				buffer.clear();
			}
		} catch (Exception e) {
			Log.saveException(e);
			buffer.clear();
		} finally {
			if (result != null && result.name() != null) {
				Log.saveQuit(result.name());
				PlayerManager.quit(result.name(), result.uuid(), result.version());
			}
			phaser.arrive();
		}
		super.run();
	}

	public S2CHandler createS2C() {
		return this.s2c;
	}

}
