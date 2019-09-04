package test.org.helium.perfmon;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

@PerformanceCounterCategory("apps")
public class BeanPerformanceCounters {
	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter tx;

	public void setTx(SmartCounter tx) {
		this.tx = tx;
	}

	public SmartCounter getTx() {
		return tx;
	}
}