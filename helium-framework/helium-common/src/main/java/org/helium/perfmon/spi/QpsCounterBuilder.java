package org.helium.perfmon.spi;

import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.CounterBuilder;
import org.helium.perfmon.observation.ObserverReportColumn;

/**
 * Created by Coral on 10/22/15.
 */
public class QpsCounterBuilder extends CounterBuilder {
	public static final QpsCounterBuilder INSTANCE = new QpsCounterBuilder();

	private QpsCounterBuilder() {
	}

	@Override
	public AbstractCounterEntity createCounter() {
		return new QpsCounter();
	}

	@Override
	public ObserverReportColumn[] getColumns(String name) {
		return QpsCounterReportUnit.getColumns(name);
	}
}
