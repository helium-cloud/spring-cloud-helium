package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

/**
 * Created by Coral on 10/22/15.
 */
public class QpsCounterSnapshot extends ObserverReportSnapshot {
	private long total;

	public QpsCounterSnapshot(long total) {
		super();
		this.total = total;
	}

	public long getTotal() {
		return total;
	}

	@Override
	public ObserverReportUnit computeReport(ObserverReportSnapshot last) {
		QpsCounterSnapshot rv = (QpsCounterSnapshot) last;

		long dCount = this.total - rv.total;
		long dNanos = this.getNanos() - rv.getNanos();
		double qps = (double)dCount * 1E9 / dNanos;

		return new QpsCounterReportUnit(total, qps);
	}
}
