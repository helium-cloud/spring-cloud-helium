package test.org.helium.perfmon;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

@PerformanceCounterCategory("sample")
public class SampleCounter {
	@PerformanceCounter(name = "number", type = PerformanceCounterType.NUMBER)
	private SmartCounter number;

	@PerformanceCounter(name = "ratio", type = PerformanceCounterType.RATIO)
	private SmartCounter ratio;

	@PerformanceCounter(name = "throughput", type = PerformanceCounterType.THROUGHPUT)
	private SmartCounter throughput;

	@PerformanceCounter(name = "transaction", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter transaction;

	public SmartCounter getNumber() {
		return number;
	}

	public void setNumber(SmartCounter number) {
		this.number = number;
	}

	public SmartCounter getRatio() {
		return ratio;
	}

	public void setRatio(SmartCounter ratio) {
		this.ratio = ratio;
	}

	public SmartCounter getThroughput() {
		return throughput;
	}

	public void setThroughput(SmartCounter throughput) {
		this.throughput = throughput;
	}

	public SmartCounter getTransaction() {
		return transaction;
	}

	public void setTransaction(SmartCounter transaction) {
		this.transaction = transaction;
	}

}