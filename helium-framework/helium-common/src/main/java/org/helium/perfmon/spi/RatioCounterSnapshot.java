package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class RatioCounterSnapshot extends ObserverReportSnapshot {
	private long hits;
	private long missed;

	public long getHits() {
		return hits;
	}

	public long getMissed() {
		return missed;
	}

	public RatioCounterSnapshot(long hits, long missed) {
		this.hits = hits;
		this.missed = missed;
	}

	@Override
	public ObserverReportUnit computeReport(ObserverReportSnapshot obj) {
		RatioCounterSnapshot rv = (RatioCounterSnapshot) obj;
		long nanos = this.getNanos() - rv.getNanos();
		long hits = this.getHits() - rv.getHits();
		long missed = this.getMissed() - rv.getMissed();
		return new RatioCounterReportUnit(hits, hits + missed, nanos);
	}
}
