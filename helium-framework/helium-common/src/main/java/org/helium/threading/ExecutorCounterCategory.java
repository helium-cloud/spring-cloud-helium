package org.helium.threading;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * 
 * <b>描述: </b>这是一个线程计数器
 * <p>
 * <b>功能: </b>线程计数器
 * <p>
 * <b>用法: </b>由内部逻辑调用，外部由diagnostic模块负责展现
 * <p>
 * 
 * Created by Coral
 * 
 */
@PerformanceCounterCategory("thread-pool")
public class ExecutorCounterCategory {
	@PerformanceCounter(name = "size", type = PerformanceCounterType.NUMBER)
	private SmartCounter sizeCounter;

	@PerformanceCounter(name = "worker", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter workerCounter;

	public SmartCounter getSizeCounter() {
		return sizeCounter;
	}

	public void setSizeCounter(SmartCounter sizeCounter) {
		this.sizeCounter = sizeCounter;
	}

	public SmartCounter getWorkerCounter() {
		return workerCounter;
	}

	public void setWorkerCounter(SmartCounter workerCounter) {
		this.workerCounter = workerCounter;
	}
}
