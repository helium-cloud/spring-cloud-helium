package org.helium.perfmon;

/**
 * 自动开始的计时秒表, 精度到纳秒1E-9<br>
 * 
 * Created by Coral
 */
public class Stopwatch {
	public interface Watchable {
		void end(long nanos);

		void fail(long nanos, String message);

		void fail(long nanos, Throwable error);
	}

	private long begin;
	private Watchable watchee;

	public Stopwatch(Watchable watchee) {
		this.watchee = watchee;
		begin = System.nanoTime();
	}

	public Stopwatch() {
		begin = System.nanoTime();
	}

	public void update() {
		begin = System.nanoTime();
	}

	public long getBeginNanos() {
		return begin;
	}

	public long getNanos() {
		return System.nanoTime() - begin;
	}

	public double getSeconds() {
		return (System.nanoTime() - begin) / 1E9;
	}

	public double getMillseconds() {
		return (double) (System.nanoTime() - begin) / 1E6;
	}

	public void end() {
		long nanos = System.nanoTime() - begin;
		if (watchee != null) {
			watchee.end(nanos);
		}
	}

	public void fail(String message) {
		long nanos = System.nanoTime() - begin;
		if (watchee != null) {
			watchee.fail(nanos, message);
		}
	}

	public void fail(Throwable error) {
		long nanos = System.nanoTime() - begin;
		if (watchee != null) {
			watchee.fail(nanos, error);
		}
	}
}
