package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.CounterBuilder;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class ThroughputCounterBuilder extends CounterBuilder {
	public static final ThroughputCounterBuilder INSTANCE = new ThroughputCounterBuilder();

	private ThroughputCounterBuilder() {
	}

	@Override
	public AbstractCounterEntity createCounter() {
		return new ThroughputCounter();
	}

	@Override
	public ObserverReportColumn[] getColumns(String name) {
		return ThroughputCounterReportUnit.getColumns(name);
	}
}
