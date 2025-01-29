package Handlers.Agents.Processors;

import Handlers.Handler;

public class PPing extends Processor<Long> {

	public PPing(Handler handler) {
		super(handler);
	}

	@Override
	public Long processUWU(Handler handler) throws Exception {
		if (sourceChannel.read(buffer) != -1) {
			buffer.flip();
			int lim = buffer.limit();
			buffer.position(2);
			long ping = buffer.getLong();
			buffer.position(0);
			buffer.limit(lim);
			return ping;
		}
		return (long) -1;
	}
}
