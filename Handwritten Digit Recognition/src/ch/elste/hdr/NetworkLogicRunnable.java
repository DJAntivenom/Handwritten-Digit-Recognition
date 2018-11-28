package ch.elste.hdr;

import static ch.elste.hdr.util.NetworkUtils.*;

import ch.elste.hdr.util.NetworkUtils;

public class NetworkLogicRunnable implements Runnable {
	private volatile boolean running = true;

	@Override
	public void run() {
		while (running) {
			if (ANIMATED || ANIMATED_ONCE) {
				Main.updateCurrentLabel();

				Main.setGuess(getGuessedValue(Main.CURRENT_NETWORK.backpropagate(to1DArray(Main.getCurrentImage()),
						toSolutionArray(Main.getCurrentLabel()))));

				NetworkUtils.CHANGED = true;

				ANIMATED_ONCE = false;
			}

			if (Thread.interrupted())
				running = false;
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				running = false;
			}
		}
	}

	/**
	 * Terminate this runnable object.
	 */
	public void terminate() {
		running = false;
	}

	/**
	 * The current thread waits until the condition is false.
	 * 
	 * @param condition
	 *            thread stops waiting if false
	 * @throws InterruptedException
	 */
	public static void waitUntil(boolean condition) throws InterruptedException {
		while (condition) {
			Thread.currentThread().wait();
		}
	}
}
