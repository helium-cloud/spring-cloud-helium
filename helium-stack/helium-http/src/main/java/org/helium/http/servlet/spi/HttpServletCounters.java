package org.helium.http.servlet.spi;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * @author coral
 * @version 创建时间：2014年10月22日
 * 类说明
 */
@PerformanceCounterCategory("http-servlet")
public class HttpServletCounters {

//	@PerformanceCounter(name = "request", type = PerformanceCounterType.QPS)
//	private SmartCounter request;

	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
	private SmartCounter tx;

//	@PerformanceCounter(name = "module", type = PerformanceCounterType.TRANSACTION)
//	private SmartCounter module;

	@PerformanceCounter(name = "throughput", type = PerformanceCounterType.THROUGHPUT)
	private SmartCounter throughput;

//	public SmartCounter getRequest() {
//		return request;
//	}
//
//	public void setRequest(SmartCounter request) {
//		this.request = request;
//	}

	public SmartCounter getTx() {
		return tx;
	}

	public HttpServletCounters setTx(SmartCounter tx) {
		this.tx = tx;
		return this;
	}

	
	public SmartCounter getThroughput() {
		return throughput;
	}

	public HttpServletCounters setThroughput(SmartCounter throughput) {
		this.throughput = throughput;
		return this;
	}

//	public SmartCounter getModule() {
//		return module;
//	}
//
//	public void setModule(SmartCounter module) {
//		this.module = module;
//	}
}
