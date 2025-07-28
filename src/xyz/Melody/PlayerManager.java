package xyz.Melody;

import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager {

	private static AtomicInteger playerCount = new AtomicInteger(0);

	public static void join() {
		playerCount.incrementAndGet();
	}

	public static void disconnect() {
		playerCount.decrementAndGet();
	}

	public static int getCount() {
		return playerCount.get();
	}
}
