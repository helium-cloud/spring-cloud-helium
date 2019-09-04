package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class NumberCounterSnapshot extends ObserverReportSnapshot {
	private long count;

	public long getCount() {
		return count;
	}

	public NumberCounterSnapshot(long count) {
		this.count = count;
	}

	@Override
	public ObserverReportUnit computeReport(ObserverReportSnapshot last) {
		return new NumberCounterReportUnit(count);
	}
}
