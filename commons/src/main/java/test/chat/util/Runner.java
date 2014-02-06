package test.chat.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Runner implements Runnable {
	private static final Logger logger = LogManager.getLogger();

	private Updateable updateable;
	private long sleepTime;

	public Runner(Updateable updateable, long sleepTime) {
		this.updateable = updateable;
		this.sleepTime = sleepTime;
	}

	@Override
	public void run() {
		while (!Thread.interrupted()) {
			try {
				updateable.update();
			} catch (Exception e) {
				logger.catching(e);
				return;
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}
