package org.helium.perfmon.spi;

import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于记录命中率的计数器
 * 
 * Created by Coral
 */
public class RatioCounter extends AbstractCounterEntity implements SmartCounter {
	private AtomicLong hits;
	private AtomicLong missed;

	public RatioCounter() {
		missed = new AtomicLong();
		hits = new AtomicLong();
	}

	@Override
	public void reset() {
		missed.set(0);
		hits.set(0);
	}

	@Override
	public void increase() {
		this.hits.incrementAndGet();
	}

	@Override
	public void increaseBy(long value) {
		this.hits.addAndGet(value);
	}

	@Override
	public void increaseRatio(boolean hit) {
		if (hit) {
			this.hits.incrementAndGet();
		} else {
			this.missed.incrementAndGet();
		}
	}

	@Override
	public ObserverReportSnapshot getObserverSnapshot() {
		return new RatioCounterSnapshot(hits.longValue(), missed.longValue());
	}

	@Override
	public ObserverReportUnit getEmptyReport() {
		return new RatioCounterReportUnit(0, 0, 0);
	}
}
