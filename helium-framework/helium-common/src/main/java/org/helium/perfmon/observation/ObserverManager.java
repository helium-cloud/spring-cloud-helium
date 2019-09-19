package org.helium.perfmon.observation;

import com.feinno.superpojo.type.DateTime;
import com.feinno.superpojo.type.TimeSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <b>描述: </b>这是一个性能计数器等监视器{@link Observable}的管理者，它负责注册一个性能计数器或监视器
 * {@link Observable}到当前服务中，注册后的性能计数器或监视器{@link Observable}
 * 可以实现定时的数据采集，定时的数据采集使用回调方式进行，回调写在
 * {@link ObserverManager#addInspector(Observable, ObserverReportMode, TimeSpan, ObserverInspector.ReportCallback)}
 * 的ReportCallback中
 * <p>
 * <b>功能: </b>注册且启动对一个性能计数器等监视器{@link Observable}的数据采集工作
 * <p>
 * <b>用法: </b> 下面是一个详细的计数器例子，包括了创建一个计数器A，对A进行注册，每间隔5秒钟对A获取到的数据进行一次信息采集<br>
 * 1. 首先创建一个简单的性能计数器，名字为sample
 * <p>
 * <pre>
 * &#064;PerformanceCounterCategory(&quot;sample&quot;)
 * public class SampleCounter {
 * 	&#064;PerformanceCounter(name = &quot;number&quot;, type = PerformanceCounterType.NUMBER)
 * 	private SmartCounter number;
 *
 * 	&#064;PerformanceCounter(name = &quot;ratio&quot;, type = PerformanceCounterType.RATIO)
 * 	private SmartCounter ratio;
 *
 * 	&#064;PerformanceCounter(name = &quot;throughput&quot;, type = PerformanceCounterType.THROUGHPUT)
 * 	private SmartCounter throughput;
 *
 * 	&#064;PerformanceCounter(name = &quot;transaction&quot;, type = PerformanceCounterType.TRANSACTION)
 * 	private SmartCounter transaction;
 *
 * 	Getter And Setter...
 * }
 * </pre>
 * <p>
 * 2.简单的性能计数器创建完毕后，继续编写一个类，类中有一个线程，线程不停的对上面的计数器进行自增，再通过 {@link ObserverManager}
 * 将计数器添加到数据采集服务中，在数据采集结果的回调上不挺的输出计数器采集到的结果
 * <p>
 * <pre>
 * public class CounterTest {
 * 	&#064;Test
 * 	public void test() {
 * 		final SampleCounter counter = PerformanceCounterFactory.getCounters(SampleCounter.class, &quot;&quot;);
 * 		counter.getNumber().increase();
 * 		counter.getNumber().decrease();
 *
 * 		Thread tr = new Thread(new Runnable() { // 创建一个线程，不停的使用计数器,这样才可以在数据采集中看到计数器不同时刻的变化
 * 					&#064;Override
 * 					public void run() {
 * 						Random rand = new Random();
 * 						while (true) {
 * 							try {
 * 								Thread.sleep(0);
 * 								long l = 0;
 * 								for (int i = 0; i &lt; 1 * 1; i++) {
 * 									l = l | System.nanoTime();
 *                                }
 * 								counter.getThroughput().increaseBy(1000 + rand.nextInt(1000) &amp; l &amp; 0x0000ffff);
 * 								counter.getRatio().increaseRatio(l % 2 == 0);
 *                            } catch (InterruptedException e) {
 * 								e.printStackTrace();
 * 								return;
 *                            }
 *                        }
 *                    }
 *                });
 * 		tr.start();
 *
 * 		Observable ob = ObserverManager.getObserverItem(&quot;sample&quot;);
 * 		ObserverManager.addInspector(ob, ObserverReportMode.ALL, new TimeSpan(5000), new ReportCallback() {
 * 			&#064;Override
 * 			public boolean handle(ObserverReport report) {
 * 				System.out.println(report.encodeToJson());// 将采集到的结果以json的格式输出
 * 				return true;
 *            }
 *        });
 * 		try {
 * 			Thread.sleep(30 * 1000);
 *        } catch (InterruptedException e) {
 * 			e.printStackTrace();
 *        }
 *    }
 * }
 * </pre>
 * <p>
 *
 * Created by Coral
 */
public class ObserverManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObserverManager.class);

    private static Object syncRoot;
    private static Map<String, Observable> observers;

    private static Object syncInspectors;
    private static List<ObserverInspector> inspectors;

    private static Thread thread;

    private static List<ObservableTracker> trackers;

    static {
        syncRoot = new Object();
        observers = new HashMap<String, Observable>();

        syncInspectors = new Object();
        inspectors = new ArrayList<ObserverInspector>();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                inspectorProc();
            }
        });

        trackers = new ArrayList<>();

        thread.setDaemon(true);
        thread.start();
    }

    public static void addObservableTracker(ObservableTracker tracker) {
        synchronized (syncRoot) {
            trackers.add(tracker);
            for (Observable observable : observers.values()) {
                fireTrackerAction(tracker, ObservableTracker.Action.ADD, observable);
            }
        }
    }

    public static void removeObservableTracker(ObservableTracker tracker) {
        synchronized (syncRoot) {
            trackers.remove(tracker);
        }
    }

    private static void fireTrackerAction(ObservableTracker tracker, ObservableTracker.Action action, Observable observable) {
        try {
            tracker.action(action, observable);
        } catch (Exception e) {
            LOGGER.error("fire ObservableTracker fault. action={} observableName={}", action, observable.getObserverName(), e);
        }
    }

    private static void fireTrackerAction(ObservableTracker.Action action, Observable observable) {
        for (ObservableTracker tracker : trackers) {
            fireTrackerAction(tracker, action, observable);
        }
    }

    public static void register(Observable obj) {
        String name = obj.getObserverName();
        synchronized (syncRoot) {
            if (observers.containsKey(name)) {
                throw new IllegalArgumentException("duplicated observable)" + name);
            }
            observers.put(name, obj);
            fireTrackerAction(ObservableTracker.Action.ADD, obj);
        }
    }

    public static void unregister(String key) {
        synchronized (syncRoot) {
            Observable observable = observers.get(key);
            observers.remove(key);
            if (observable != null) {
                synchronized (syncInspectors) {
                    for (ObserverInspector inspector : inspectors) {
                        if (observable.equals(inspector.getObservable())) {
                            inspectors.remove(inspector);
                        }
                    }
                }
                fireTrackerAction(ObservableTracker.Action.REMOVE, observable);
            }
        }
    }

    public static List<Observable> getAllObserverItems() {
        List<Observable> ret = new ArrayList<Observable>();
        synchronized (syncRoot) {
            for (Observable obj : observers.values()) {
                ret.add(obj);
            }
        }
        return ret;
    }

    public static Observable getObserverItem(String key) {
        synchronized (syncRoot) {
            return observers.get(key);
        }
    }

    public static ObserverInspector addInspector(Observable observer, ObserverReportMode mode, TimeSpan span, ObserverInspector.ReportCallback callback) {
        ObserverInspector inspector = new ObserverInspector(observer, mode, span, callback);
        synchronized (syncInspectors) {
            inspectors.add(inspector);
        }
        return inspector;
    }

    private static void inspectorProc() {
        while (true) {
            try {
                Thread.sleep(10);
                DateTime now = DateTime.now();

                List<ObserverInspector> runs = new ArrayList<ObserverInspector>();
                synchronized (syncInspectors) {
                    for (ObserverInspector i : inspectors) {
                        if (i.onTime(now)) {
                            LOGGER.debug("begin to run: {}", i);
                            runs.add(i);
                        }
                    }
                }
                List<ObserverInspector> deletes = new ArrayList<ObserverInspector>();
                for (ObserverInspector i : runs) {
                    LOGGER.debug("running: {}", i);
                    if (!i.run(now)) {
                        deletes.add(i);
                        LOGGER.debug("delete for: {}", i);
                    }
                }
                //
                // 删除掉不再响应的Inspector
                synchronized (syncInspectors) {
                    for (ObserverInspector i : deletes) {
                        inspectors.remove(i);
                    }
                }
            } catch (InterruptedException ex) {

            } catch (Exception ex) {
                LOGGER.error("ThreadProc failed {}", ex);
            }
        }
    }
}
