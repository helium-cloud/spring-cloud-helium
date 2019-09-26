package org.helium.cloud.task.rpc;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * Created by Coral on 10/22/15.
 */
@PerformanceCounterCategory("task-rpc")
public class TaskRpcCounter {
	@PerformanceCounter(name = "produce", type = PerformanceCounterType.QPS)
	private SmartCounter produce;

	@PerformanceCounter(name = "consume", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter consume;

	public SmartCounter getProduce() {
		return produce;
	}

	public void setProduce(SmartCounter produce) {
		this.produce = produce;
	}

	public SmartCounter getConsume() {
		return consume;
	}

	public void setConsume(SmartCounter consume) {
		this.consume = consume;
	}
}
