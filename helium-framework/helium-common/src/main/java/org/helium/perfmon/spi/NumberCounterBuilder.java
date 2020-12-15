package org.helium.perfmon.spi;

import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.CounterBuilder;
import org.helium.perfmon.observation.ObserverReportColumn;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class NumberCounterBuilder extends CounterBuilder {
	public static final NumberCounterBuilder INSTANCE = new NumberCounterBuilder();

	private NumberCounterBuilder() {
	}

	@Override
	public AbstractCounterEntity createCounter() {
		return new NumberCounter();
	}

	@Override
	public ObserverReportColumn[] getColumns(String name) {
		return NumberCounterReportUnit.getColumns(name);
	}
}
