package Connection.Handlers.Processors;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import Connection.Handler;

public abstract class Processor<T> {
	public ByteBuffer buffer;
	public SocketChannel sourceChannel;
	public SocketChannel targetChannel;

	public Processor(Handler handler) {
		this.buffer = handler.buffer;
		this.sourceChannel = handler.sourceChannel;
		this.targetChannel = handler.targetChannel;
	}

	public abstract T processUWU(Handler handler) throws Exception;
}
