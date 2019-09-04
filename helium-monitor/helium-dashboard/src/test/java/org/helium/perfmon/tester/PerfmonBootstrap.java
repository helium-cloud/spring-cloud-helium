package org.helium.perfmon.tester;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.PerformanceCounterType;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;
import org.helium.framework.spi.Bootstrap;

import java.util.Random;

/**
 * Created by Coral on 2015/8/17.
 */
public class PerfmonBootstrap {

    public static void main(String[] args) throws Exception {
        try {
            Bootstrap.INSTANCE.addPath("helium-dashboard-servlets/src/test/resources/test");
            Bootstrap.INSTANCE.addPath("helium-dashboard-servlets/src/main/resources/META-INF");
            Bootstrap.INSTANCE.initialize("bootstrap.xml", true, false);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }

        final SampleCounter counter = PerformanceCounterFactory.getCounters(SampleCounter.class, "");
        SampleService service = Bootstrap.INSTANCE.getService(SampleService.class);
        Thread tr = new Thread(new Runnable() { // 创建一个线程，不停的使用计数器,这样才可以在数据采集中看到计数器不同时刻的变化
            @Override
            public void run() {
                Random rand = new Random();
                while (true) {
                    try {
                        Thread.sleep(10);
                        long l = 0;
                        for (int i = 0; i < 1 * 1; i++) {
                            l = l | System.nanoTime();
                        }
                        service.testDedicatedTask((int)l);
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

        while (true) {
            Thread.sleep(10);
        }
    }


    @PerformanceCounterCategory("sample")
    public static class SampleCounter {
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
        private SmartCounter number;

        @PerformanceCounter(name = "ratio", type = PerformanceCounterType.RATIO)
        private SmartCounter ratio;

        @PerformanceCounter(name = "throughput", type = PerformanceCounterType.THROUGHPUT)
        private SmartCounter throughput;

        @PerformanceCounter(name = "transaction", type = PerformanceCounterType.TRANSACTION)
        private SmartCounter transaction;

    }
}
