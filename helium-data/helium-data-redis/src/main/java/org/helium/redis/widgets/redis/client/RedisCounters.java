package org.helium.redis.widgets.redis.client;

import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * Created by yibo on 2017-6-9.
 */
@PerformanceCounterCategory("redis-counter2")
public class RedisCounters {

    @PerformanceCounter(name = "request.", type = PerformanceCounterType.QPS)
    private SmartCounter qps;

    @PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter tx;

    public SmartCounter getTx() {
        return tx;
    }

    public void setTx(SmartCounter tx) {
        this.tx = tx;
    }

    public SmartCounter getQps() {
        return qps;
    }

    public void setQps(SmartCounter qps) {
        this.qps = qps;
    }
}
