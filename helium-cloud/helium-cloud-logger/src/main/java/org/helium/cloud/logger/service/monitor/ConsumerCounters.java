package org.helium.cloud.logger.service.monitor;


import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

/**
 * 性能计数器
 * 监控Kafka消费者
 */
@PerformanceCounterCategory("data-consumer")
public class ConsumerCounters {
    @PerformanceCounter(name = "tx", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter tx;

    @PerformanceCounter(name = "size", type = PerformanceCounterType.NUMBER)
    private SmartCounter size;

    @PerformanceCounter(name = "qps", type = PerformanceCounterType.QPS)
    private SmartCounter qps;

    public SmartCounter getQps() {
        return qps;
    }

    public SmartCounter getSize() {
        return size;
    }

    public SmartCounter getTx() {
        return tx;
    }
}
