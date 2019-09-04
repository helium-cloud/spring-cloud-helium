package org.helium.threading;

import org.helium.util.Runnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 延迟运行工具
 * Created by Coral on 11/17/15.
 */
public class DelayRunner {
	private static Logger LOGGER = LoggerFactory.getLogger(DelayRunner.class);

	public static void run(int delaySeconds, Runnable run) {
		DelayRunner runner = new DelayRunner(delaySeconds, run);
	}

	private Thread thread;
	public DelayRunner(int delaySeconds, Runnable run) {
		thread = new Thread(() -> {
			try {
				LOGGER.info("runTask after {} seconds", delaySeconds);
				Thread.sleep(delaySeconds * 1000);
				LOGGER.info("Task on time, running...", delaySeconds);
				run.run();
				LOGGER.info("Task done.", delaySeconds);
			} catch (Exception ex) {
				LOGGER.error("runTask failed", ex);
			}
		});
		thread.start();
	}
}
