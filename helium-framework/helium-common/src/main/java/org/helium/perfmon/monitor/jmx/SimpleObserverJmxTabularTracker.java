package org.helium.perfmon.monitor.jmx;

import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObservableTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个简单的监控条目追踪器（{@link ObservableTracker}） ,可以自动为每一个监控条目（{@link Observable}） 维护注册一个相对应的JMX适配器（{@link ObserverJmxTabularAdapter}）
 * <p>
 * Created by Coral on 2015/8/13.
 */
public class SimpleObserverJmxTabularTracker implements ObservableTracker {

    private final static Logger logger = LoggerFactory.getLogger(SimpleObserverJmxTabularTracker.class);

    MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
    Map<String, ObserverJmxTabularAdapter> adapterMap = new HashMap<>();

    int inspectorInterval;
    int keepingAliveTime;

    boolean active = true;

    /**
     * @param inspectorInterval 数据采集周期，单位是秒
     * @param keepingAliveTime  在无数据请求的情况下持续采集的时间长度，单位是秒
     */
    public SimpleObserverJmxTabularTracker(int inspectorInterval, int keepingAliveTime) {
        this.inspectorInterval = inspectorInterval;
        this.keepingAliveTime = keepingAliveTime;
    }

    @Override
    public void action(Action action, Observable observable) {
        synchronized (this) {
            if (!active) {
                // 如果Tracker已经要被干掉了，就不再处理任何action了
                return;
            }
            String category = observable.getObserverName();
            ObserverJmxTabularAdapter adapter;
            switch (action) {
                case ADD:
                    adapter = ObserverJmxTabularAdapter.register(mBeanServer, category, inspectorInterval, keepingAliveTime);
                    adapterMap.put(category, adapter);
                    logger.info("Register ObserverJmxTabularAdapter. category={} inspectorInterval={} keepingAliveTime={}", category, inspectorInterval, keepingAliveTime);
                    break;
                case REMOVE:
                    adapter = adapterMap.get(category);
                    if (adapter != null) {
                        ObserverJmxTabularAdapter.unregister(mBeanServer, adapter);
                        logger.info("Unregister ObserverJmxTabularAdapter. category={}", category);
                    }
                    break;
            }
        }
    }

    /**
     * 干掉这个追踪器，注销它注册的所有JMX适配器（{@link ObserverJmxTabularAdapter}）
     */
    public void destroy() {
        synchronized (this) {
            active = false;
            for (Map.Entry<String, ObserverJmxTabularAdapter> entry : adapterMap.entrySet()) {
                try {
                    ObserverJmxTabularAdapter.unregister(mBeanServer, entry.getValue());
                    logger.info("Unregister ObserverJmxTabularAdapter. category={}", entry.getKey());
                } catch (Exception e) {
                    logger.error("Unregister ObserverJmxTabularAdapter error. category={}", entry.getKey(), e);
                }
            }
        }
    }
}
