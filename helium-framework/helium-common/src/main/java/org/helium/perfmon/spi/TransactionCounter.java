package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;
import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.Stopwatch;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * 一个用于追踪Transaction的计数器
 * 
 * Created by Coral
 */
public class TransactionCounter extends AbstractCounterEntity implements SmartCounter, Stopwatch.Watchable {
	private AtomicLong fired;
	private AtomicLong successed;
	private AtomicLong failed;
	private AtomicLong totalCost;
	private String lastError;
	private long lastErrorNanos;

	public TransactionCounter() {
		fired = new AtomicLong();
		successed = new AtomicLong();
		failed = new AtomicLong();
		totalCost = new AtomicLong();
		lastError = null;
	}

	public int getConcurrent() {
		return (int) (fired.get() - successed.get() - failed.get());
	}

	@Override
	public void reset() {
		fired.set(0);
		successed.set(0);
		failed.set(0);
		totalCost.set(0);
		lastError = null;
	}

	@Override
	public Stopwatch begin() {
		fired.incrementAndGet();
		return new Stopwatch(this);
	}

	@Override
	public void end(long nanos) {
		successed.incrementAndGet();
		totalCost.addAndGet(nanos);
	}

	@Override
	public void fail(long nanos, String message) {
		failed.incrementAndGet();
		totalCost.addAndGet(nanos);
		if (message != null) {
			lastError = message;
			lastErrorNanos = System.nanoTime();
		}
	}

	@Override
	public void fail(long nanos, Throwable error) {
		failed.incrementAndGet();
		totalCost.addAndGet(nanos);
		if (error != null) {
			lastError = error.getMessage();
			lastErrorNanos = System.nanoTime();
		}
	}

	@Override
	public ObserverReportSnapshot getObserverSnapshot() {
		return new TransactionCounterSnapshot(fired.longValue(), successed.longValue(), failed.longValue(), totalCost.longValue(), lastError, lastErrorNanos);
	}

	@Override
	public ObserverReportUnit getEmptyReport() {
		return new TransactionCounterReportUnit(0, 0, 0, 0, 0, 0, null);
	}

	@Override
	public String toString() {
		return String.format("fired=%s,successed=%s,failed=%s,totalCost=%s,lastError=%s", fired != null ? fired.get() : "", successed != null ? successed.get()
				: "", failed != null ? failed.get() : "", totalCost != null ? totalCost.get() : "", lastError != null ? lastError : "");
	}
}
