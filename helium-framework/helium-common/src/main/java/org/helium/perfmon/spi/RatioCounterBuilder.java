package org.helium.perfmon.spi;

import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.CounterBuilder;
import org.helium.perfmon.observation.ObserverReportColumn;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class RatioCounterBuilder extends CounterBuilder {
	public static final RatioCounterBuilder INSTANCE = new RatioCounterBuilder();

	private RatioCounterBuilder() {
	}

	@Override
	public AbstractCounterEntity createCounter() {
		return new RatioCounter();
	}

	@Override
	public ObserverReportColumn[] getColumns(String name) {
		return RatioCounterReportUnit.getColumns(name);
	}
}
