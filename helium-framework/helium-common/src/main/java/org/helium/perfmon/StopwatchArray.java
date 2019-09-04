package org.helium.perfmon;

/**
 * Created by Coral on 10/11/16.
 */
public class StopwatchArray extends Stopwatch {
	private Stopwatch[] watchs;

	public StopwatchArray(Stopwatch... watchs) {
		super(null);
		this.watchs = watchs;
	}

	@Override
	public void update() {
		for (Stopwatch watch: watchs) {
			watch.update();
		}
	}

	@Override
	public long getBeginNanos() {
		return watchs[0].getBeginNanos();
	}

	@Override
	public long getNanos() {
		return watchs[0].getNanos();
	}

	@Override
	public double getSeconds() {
		return watchs[0].getSeconds();
	}

	@Override
	public double getMillseconds() {
		return watchs[0].getMillseconds();
	}

	@Override
	public void end() {
		for (Stopwatch watch: watchs) {
			watch.end();
		}
	}

	@Override
	public void fail(String message) {
		for (Stopwatch watch: watchs) {
			watch.fail(message);
		}
	}

	@Override
	public void fail(Throwable error) {
		for (Stopwatch watch: watchs) {
			watch.fail(error);
		}
	}
}
