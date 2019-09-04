package org.helium.database.spi;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * Created by Coral on 11/11/15.
 */
@PerformanceCounterCategory("database-transactions")
public class TransactionCounters {
	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter tx;

	public SmartCounter getTx() {
		return tx;
	}

	public void setTx(SmartCounter tx) {
		this.tx = tx;
	}
}
