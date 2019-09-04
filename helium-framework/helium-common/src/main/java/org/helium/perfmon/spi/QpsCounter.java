package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;
import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.Stopwatch;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Coral on 10/22/15.
 */
public class QpsCounter extends AbstractCounterEntity {
	private AtomicLong count = new AtomicLong();

	@Override
	public void reset() {
		count.set(0);
	}

	@Override
	public void increase() {
		count.incrementAndGet();
	}

	@Override
	public void decrease() {
		count.decrementAndGet();
	}

	@Override
	public void increaseBy(long value) {
		count.addAndGet(value);
	}

	@Override
	public void setRawValue(long value) {
		count.set(value);
	}

	@Override
	public void increaseRatio(boolean hitted) {
		throw new UnsupportedOperationException("Invailed CounterType!");
	}

	@Override
	public Stopwatch begin() {
		throw new UnsupportedOperationException("Invailed CounterType!");
	}

	@Override
	public ObserverReportUnit getEmptyReport() {
		return new QpsCounterReportUnit(0, 0.0);
	}

	@Override
	public ObserverReportSnapshot getObserverSnapshot() {
		return new QpsCounterSnapshot(count.longValue());
	}
}
