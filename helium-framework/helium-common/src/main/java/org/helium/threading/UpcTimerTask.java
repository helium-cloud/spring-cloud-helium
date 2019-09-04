package org.helium.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 扩展自Netty的Timer
 * 
 * @author Lv.Mingwei
 * 
 */
public class UpcTimerTask implements TimerTask {

	/** Timer listener */
	private TimeoutListener listener;

	/** Timeout value */
	private long time;

	/** Start time */
	private long start_time;

	/** Timer label (optional) */
	private String label;

	private Timeout timeout;

	/** Whether the Timer is (still) active */
	private boolean active;

	/** Timeout thread pool */
	private static Executor executor;

	private static HashedWheelTimer[] innerTimer;

	private static final AtomicInteger counter = new AtomicInteger();

	static {
		int cupNumber = Runtime.getRuntime().availableProcessors();
		int timerSize = (cupNumber + 1) / 2;
		innerTimer = new HashedWheelTimer[timerSize];
		for (int i = 0; i < timerSize; i++) {
			innerTimer[i] = new HashedWheelTimer(50 + new Random().nextInt(timerSize * 3), TimeUnit.MILLISECONDS, 7200);
		}
		executor = ExecutorFactory.newFixedExecutor("UpcTimerTask", cupNumber * 2, cupNumber * 2 * 10);
	}

	private static Logger logger = LoggerFactory.getLogger(UpcTimerTask.class);

	public void setLabel(String label) {
		this.label = label;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Creates a new Timer of <i>t_msec</i> milliseconds with TimerListener
	 * <i>listener</i> The Timer is not started. You need to fire the start()
	 * method.
	 */
	public UpcTimerTask(long time, TimeoutListener listener) {
		this(time, null, listener);
	}

	/**
	 * Creates a new Timer of <i>t_msec</i> milliseconds, with a label
	 * <i>t_event</i>, and with TimerListener <i>listener</i> The Timer is not
	 * started. You need to fire the start() method.
	 */
	public UpcTimerTask(long time, String label, TimeoutListener listener) {
		this.listener = listener;
		this.time = time;
		this.label = label;
		this.active = false;
		this.timeout = null;
		this.start_time = 0;
	}

	/** Gets the Timer label. */
	public String getLabel() {
		return label;
	}

	/** Gets the initial time (in milliseconds). */
	public long getTime() {
		return time;
	}

	public UpcTimerTask updateTime(long time) {
		this.time = time;
		this.cancel();
		this.start();
		return this;
	}

	/** Gets the remaining time (in milliseconds). */
	public long getExpirationTime() {
		if (active) {
			long expire = start_time + time - System.currentTimeMillis();
			return (expire > 0) ? expire : 0;
		} else
			return 0;
	}

	/** Stops the Timer. The onTimeout() method will not be fired. */
	public void cancel() {
		active = false;
		// (CHANGE-040421) now it can free the link to Timer listeners
		// listener_list=null;
		if (timeout != null) {
			timeout.cancel();
			timeout = null;
		}
	}

	/** Starts the timer. */
	public UpcTimerTask start() {
		if (time < 0)
			return null;
		start_time = System.currentTimeMillis();
		active = true;
		if (time > 0) {
			int index = Math.abs(counter.incrementAndGet() % innerTimer.length);
			timeout = innerTimer[index].newTimeout(this, time, TimeUnit.MILLISECONDS);
		} else {
			// fire now!
			runTimeout(null);
		}
		return this;
	}

	/** Whether the timer is running. */
	public boolean isRunning() {
		return active;
	}

	@Override
	public void run(final Timeout timeout) {
		if (UpcTimerTask.executor != null) {
			UpcTimerTask.executor.execute(new Runnable() {

				@Override
				public void run() {
					runTimeout(timeout);
				}
			});
		} else {
			runTimeout(timeout);
		}
	}

	private void runTimeout(Timeout timeout) {
		try {
			if (active && listener != null) {
				listener.onTimeout(timeout);
			}
		} catch (Exception e) {
			logger.error("timeout exception", e);
		}
	}

	public void setNeverStart() {
		time = -1;
	}

	public void setNoWaitStart() {
		time = 0;
	}

	public void setListener(TimeoutListener listener) {
		this.listener = listener;
	}

	public static void setExecutor(Executor executor) {
		UpcTimerTask.executor = executor;
	}

}
