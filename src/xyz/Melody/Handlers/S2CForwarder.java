package xyz.Melody.Handlers;

public class S2CForwarder extends Handler {

	// nothing complex here, quite simple huh
	public S2CForwarder(ClientHandler client) {
		super(client.clientChannel, client.serverChannel, client.connection);
	}

	@Override
	public void run() {
		try {
			while (this.serverChannel.read(buffer) != -1) {
				this.buffer.flip();
				this.clientChannel.write(buffer);
				this.buffer.clear();
			}
		} catch (Exception e) {
			this.buffer.clear();
		}
		super.run();
	}

}
