package org.helium.test.perfmon.jmx;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;
import org.helium.perfmon.monitor.jmx.SimpleObserverJmxTabularTracker;
import org.helium.perfmon.observation.ObserverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by Coral on 2015/8/11.
 */
public class TestMain {

    private static final Logger logger = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) throws Exception {

        buildCounter(SampleCounter1.class, "instance01");

        Thread.sleep(10000);

        SimpleObserverJmxTabularTracker tracker = new SimpleObserverJmxTabularTracker(1, 10);
        ObserverManager.addObservableTracker(tracker);

        Thread.sleep(10000);

        buildCounter(SampleCounter2.class, "instance01");

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                ObserverManager.removeObservableTracker(tracker);
                tracker.destroy();
            }
        });

        logger.info("Service start success.");
        System.in.read();
    }

    private static void buildCounter(Class<? extends SampleCounter> clazz, String instanceName) {
        logger.info("Build counter. counterClass={} instanceName={}", clazz.getSimpleName(), instanceName);
        final SampleCounter counter = PerformanceCounterFactory.getCounters(clazz, instanceName);

        Thread tr = new Thread(new Runnable() { // 创建一个线程，不停的使用计数器,这样才可以在数据采集中看到计数器不同时刻的变化
            @Override
            public void run() {
                Random rand = new Random();
                while (true) {
                    try {
                        Thread.sleep(0);
                        long l = 0;
                        for (int i = 0; i < 1 * 1; i++) {
                            l = l | System.nanoTime();
                        }
                        counter.getThroughput().increaseBy(1000 + rand.nextInt(1000) & l & 0x0000ffff);
                        counter.getRatio().increaseRatio(l % 2 == 0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }
            }
        });
        tr.start();
    }


    static public class SampleCounter {
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

        @PerformanceCounter(name = "number", type = PerformanceCounterType.NUMBER)
        SmartCounter number;

        @PerformanceCounter(name = "ratio", type = PerformanceCounterType.RATIO)
        SmartCounter ratio;

        @PerformanceCounter(name = "throughput", type = PerformanceCounterType.THROUGHPUT)
        SmartCounter throughput;

        @PerformanceCounter(name = "transaction", type = PerformanceCounterType.TRANSACTION)
        SmartCounter transaction;

    }

    @PerformanceCounterCategory("sample1")
    static public class SampleCounter1 extends SampleCounter {

    }

    @PerformanceCounterCategory("sample2")
    static public class SampleCounter2 extends SampleCounter {

    }

}