package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class TransactionCounterSnapshot extends ObserverReportSnapshot {
	private long fired;
	private long successes;
	private long failed;
	private long totalCost;
	private String lastError;
	private long lastErrorNanos;

	public long getFired() {
		return fired;
	}

	public long getSuccesses() {
		return successes;
	}

	public long getFailed() {
		return failed;
	}

	public long getTotalCost() {
		return totalCost;
	}

	public String getLastError() {
		return lastError;
	}

	public long getLastErrorNanos() {
		return lastErrorNanos;
	}

	public TransactionCounterSnapshot(long fired, long successes, long failed, long totalCost, String lastError, long lastErrorNanos) {
		super();
		this.fired = fired;
		this.successes = successes;
		this.failed = failed;
		this.totalCost = totalCost;
		this.lastError = lastError;
		this.lastErrorNanos = lastErrorNanos;
	}

	@Override
	public ObserverReportUnit computeReport(ObserverReportSnapshot last) {
		TransactionCounterSnapshot rv = (TransactionCounterSnapshot) last;
		long totalFired = this.fired;
		long fired = this.fired - rv.fired;
		long totalFailed = this.failed;
		long concurrent = this.fired - this.successes - this.failed;
		long costNanos = this.totalCost - rv.totalCost;
		long nanos = this.getNanos() - rv.getNanos();

		String lastError = null;
		if (this.getLastErrorNanos() != rv.getLastErrorNanos()) {
			lastError = this.lastError;
		}

		return new TransactionCounterReportUnit(totalFired, fired, totalFailed, concurrent, costNanos, nanos, lastError);
	}
}
