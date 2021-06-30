package org.helium.perfmon.simple;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.Stopwatch;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * wuhao
 */
@PerformanceCounterCategory("monitor")
public class PerfmonCounters {
	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter tx;

	@PerformanceCounter(name = "qps", type = PerformanceCounterType.QPS)
	private SmartCounter qps;

	public SmartCounter getQps() {
		return qps;
	}

	public SmartCounter getTx() {
		return tx;
	}

	public static PerfmonCounters getInstance(String name) {
		PerfmonCounters perfmonCounters =
				PerformanceCounterFactory.getCounters(PerfmonCounters.class, name);
		return perfmonCounters;
	}

	public void test(){
		PerfmonCounters perfmonCounters = PerfmonCounters.getInstance("test");
		Stopwatch stopwatch = perfmonCounters.getTx().begin();
		stopwatch.fail("xxx");
		stopwatch.end();
	}
}

