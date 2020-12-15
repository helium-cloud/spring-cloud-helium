package org.helium.perfmon.spi;

import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.CounterBuilder;
import org.helium.perfmon.observation.ObserverReportColumn;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class TransactionCounterBuilder extends CounterBuilder {
	public static final TransactionCounterBuilder INSTANCE = new TransactionCounterBuilder();

	private TransactionCounterBuilder() {
	}

	@Override
	public AbstractCounterEntity createCounter() {
		return new TransactionCounter();
	}

	@Override
	public ObserverReportColumn[] getColumns(String name) {
		return TransactionCounterReportUnit.getColumns(name);
	}
}
