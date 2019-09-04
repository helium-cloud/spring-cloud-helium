package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class ThroughputCounterSnapshot extends ObserverReportSnapshot {
	private long throughput;
	private long times;

	public ThroughputCounterSnapshot(long throughput, long times) {
		super();
		this.throughput = throughput;
		this.times = times;
	}

	public long getThroughput() {
		return throughput;
	}

	public long getTimes() {
		return times;
	}

	@Override
	public ObserverReportUnit computeReport(ObserverReportSnapshot obj) {
		ThroughputCounterSnapshot rval = (ThroughputCounterSnapshot) obj;
		long nanos = this.getNanos() - rval.getNanos();
		long tr = throughput - rval.throughput;
		long ti = times - rval.times;

		return new ThroughputCounterReportUnit(tr, ti, nanos);
	}
}
