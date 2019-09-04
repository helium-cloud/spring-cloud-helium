package org.helium.perfmon;

import org.helium.perfmon.observation.ObservableUnit;
import org.helium.perfmon.observation.ObserverReportUnit;

/**
 * Counter实现的实体类 提供getSnapshot接口用于记录值
 * 
 * Created by Coral
 */
public abstract class AbstractCounterEntity implements SmartCounter, ObservableUnit {
	@Override
	public String getInstanceName() {
		throw new UnsupportedOperationException("Abstract");
	}

	public AbstractCounterEntity() {
	}

	@Override
	public abstract void reset();

	public abstract ObserverReportUnit getEmptyReport();

	@Override
	public void increase() {
		throw new UnsupportedOperationException("NotSupportted");
	}

	@Override
	public void decrease() {
		throw new UnsupportedOperationException("NotSupportted");
	}

	@Override
	public void increaseBy(long value) {
		throw new UnsupportedOperationException("NotSupportted");
	}

	@Override
	public void setRawValue(long value) {
		throw new UnsupportedOperationException("NotSupportted");
	}

	@Override
	public void increaseRatio(boolean hitted) {
		throw new UnsupportedOperationException("NotSupportted");
	}

	@Override
	public Stopwatch begin() {
		throw new UnsupportedOperationException("NotSupportted");
	}
}
