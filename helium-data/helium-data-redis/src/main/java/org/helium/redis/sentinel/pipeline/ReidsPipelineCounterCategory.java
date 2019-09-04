package org.helium.redis.sentinel.pipeline;


import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;

@PerformanceCounterCategory("redis-pipeline")
public class ReidsPipelineCounterCategory {
    @PerformanceCounter(name = "redisCmd", type = PerformanceCounterType.TRANSACTION)
    private SmartCounter redisCmdCounter;

    public SmartCounter getRedisCmdCounter() {
        return redisCmdCounter;
    }

    public void setRedisCmdCounter(SmartCounter redisCmdCounter) {
        this.redisCmdCounter = redisCmdCounter;
    }
}
