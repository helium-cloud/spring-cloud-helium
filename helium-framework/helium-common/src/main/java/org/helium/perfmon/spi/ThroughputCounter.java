package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;
import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.Stopwatch;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 用于记录吞吐量的计数器
 * 
 * Created by Coral
 */
public class ThroughputCounter extends AbstractCounterEntity implements SmartCounter {
	private AtomicLong throughtput;
	private AtomicLong times;

	public ThroughputCounter() {
		throughtput = new AtomicLong();
		times = new AtomicLong();
	}

	@Override
	public void increase() {
		throughtput.incrementAndGet();
		times.incrementAndGet();
	}

	@Override
	public void increaseBy(long value) {
		throughtput.addAndGet(value);
		times.incrementAndGet();
	}

	@Override
	public void reset() {
		throw new UnsupportedOperationException("没实现呢");
	}

	@Override
	public void decrease() {
		throw new UnsupportedOperationException("没实现呢");
	}

	@Override
	public void setRawValue(long value) {
		throw new UnsupportedOperationException("没实现呢");
	}

	@Override
	public void increaseRatio(boolean hitted) {
		throw new UnsupportedOperationException("没实现呢");
	}

	@Override
	public Stopwatch begin() {
		throw new UnsupportedOperationException("没实现呢");
	}

	@Override
	public ObserverReportSnapshot getObserverSnapshot() {
		return new ThroughputCounterSnapshot(throughtput.longValue(), times.longValue());
	}

	@Override
	public ObserverReportUnit getEmptyReport() {
		return new ThroughputCounterReportUnit(0, 0, 1);
	}
}
