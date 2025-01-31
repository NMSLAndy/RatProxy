package Utils;

import java.util.Date;

public final class TimerUtil {

	private long lastMS;
	private long pauseMS = 0;
	private boolean paused = false;

	public TimerUtil() {
		this.reset();
	}

	public long getCurrentMS() {
		return System.nanoTime() / 1000000L;
	}

	public boolean hasReached(double milliseconds) {
		if (paused)
			return false;
		return (double) (this.getCurrentMS() - this.lastMS) >= milliseconds;
	}

	public TimerUtil reset() {
		this.lastMS = this.getCurrentMS();
		this.paused = false;
		this.pauseMS = 0;
		return this;
	}

	public boolean delay(float milliSec) {
		if (paused)
			return false;
		return (float) (this.getTime() - this.lastMS) >= milliSec;
	}

	public long getLastMS() {
		return lastMS;
	}

	public long getTime() {
		if (paused)
			return this.pauseMS - this.lastMS;
		return this.getCurrentMS() - this.lastMS;
	}

	public static long curTime() {
		return new Date().getTime();
	}

	public void pause() {
		if (!paused) {
			paused = true;
			pauseMS = this.getCurrentMS();
		}
	}

	public void resume() {
		if (paused) {
			paused = false;
			lastMS += this.getCurrentMS() - pauseMS;
		}
	}

	public boolean isPaused() {
		return paused;
	}
}
