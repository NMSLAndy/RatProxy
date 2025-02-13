package Connection.Handlers.Processors;

import Connection.Handler;
import Connection.ConnectionState.States;
import Connection.Handlers.C2SHandler;
import Patterns.Disconnect;
import Patterns.HandShakePacket;
import Utils.HandShakeResult;
import Utils.Access.AccessManager;
import Utils.Config.Config;
import Utils.File.Log.Log;
import Utils.Player.PlayerManager;

public class PHandShake extends Processor<HandShakeResult> {

	public PHandShake(Handler handler) {
		super(handler);
	}

	@Override
	public HandShakeResult processUWU(Handler handler) throws Exception {
		if (sourceChannel.read(buffer) != -1) {
			buffer.flip();
			int lim = buffer.limit();
			HandShakePacket c00 = new HandShakePacket().read(buffer);
			buffer.flip();
			buffer.limit(lim);
			if (c00 == null) {
				buffer.clear();
				return null;
			}
			String host = Config.rewriteHost ? Config.rewrittenHost : c00.getHost();
			int port = Config.rewriteHost ? Config.rewrittenPort : (short) c00.getPort();
			HandShakePacket newC00 = new HandShakePacket(c00.getProtocolVersion(), host, c00.isFML(), port,
					c00.getState(), c00.getName(), c00.getUuid());
			HandShakeResult result = new HandShakeResult(c00.getState(), c00.getName(), c00.getUuid(),
					c00.getProtocolVersion());
			if (c00.getState() == 1)
				((C2SHandler) handler).state.set(States.Ping);
			else if (c00.getState() == 2) {
				if (PlayerManager.getPlayerCount() >= Config.maxPlayers) {
					Log.saveDeny(c00.getName(), "达到了最大人数");
					sourceChannel.write(Disconnect.playerLimit());
					return result;
				} else if (!AccessManager.checkAccessibility(c00.getName(), c00.getUuid(), sourceChannel))
					return null;

				PlayerManager.join(c00.getName(), c00.getUuid(), c00.getProtocolVersion());
				((C2SHandler) handler).state.set(States.Play);
			}
			targetChannel.write(newC00.construct());
			buffer.clear();
			return result;
		}
		return null;
	}
}
