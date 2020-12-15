package org.helium.http.client;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * Created by jingmiao on 2016/11/21.
 */
@PerformanceCounterCategory("http-client")
public class HttpClientCounters {
	@PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter tx;
    public SmartCounter getTx() {
        return tx;
    }
    public void setTx(SmartCounter tx) {
        this.tx = tx;
    }
}