package org.helium.perfmon;

/**
 * 空计数器类
 * 
 * Created by Coral
 */
public class DummyCounter implements SmartCounter, Stopwatch.Watchable {
	@Override
	public void reset() {
	}

	@Override
	public void increase() {
	}

	@Override
	public void decrease() {
	}

	@Override
	public void increaseBy(long value) {
	}

	@Override
	public void setRawValue(long value) {
	}

	@Override
	public void increaseRatio(boolean hitted) {
	}

	@Override
	public Stopwatch begin() {
		return new Stopwatch(this);
	}

	@Override
	public void end(long nanos) {
	}

	@Override
	public void fail(long nanos, String message) {
	}

	@Override
	public void fail(long nanos, Throwable error) {
	}
}
