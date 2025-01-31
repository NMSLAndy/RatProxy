package Connection;

public class ConnectionState {

	private States state = States.NONE;

	public static enum States {
		NONE, HandShake, Ping, Pong, Play, Disconnect;
	}

	public States getState() {
		return state;
	}

	public boolean is(States state) {
		return this.state == state;
	}

	public void set(States state) {
		this.state = state;
	}
}