package org.helium.perfmon;

import org.helium.perfmon.annotation.PerformanceCounter;
import org.helium.perfmon.annotation.PerformanceCounterCategory;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObservableUnit;
import org.helium.perfmon.observation.ObserverReportColumn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * <b>描述: </b>描述一个计数器分类的目录类别<br>
 * 该类实现了{@link Observable}接口，外部可以通过该类获取到计数器分类的目录
 * <p>
 * <b>功能: </b>描述一个计数器分类的目录，用于外部监控使用
 * <p>
 * <b>用法: </b>
 * 
 * {@link CounterCategory}的实例，由{@link PerformanceCounterFactory}负责创建，创建时
 * {@link PerformanceCounterFactory#getCounters(Class, String)}必须明确指定一个计数器
 * 首先假设拥有如下计数器(关于性能计数器的创建与使用，请参考{@link PerformanceCounterCategory})
 * 
 * <pre>
 * &#064;PerformanceCounterCategory(&quot;rpc-server&quot;)
 * public static class ServerCounter {
 * 	&#064;PerformanceCounter(name = &quot;tx&quot;, type = PerformanceCounterType.TRANSACTION)
 * 	private SmartCounter tx;
 * 
 * 	public SmartCounter getTx() {
 * 		return tx;
 * 	}
 * 
 * 	public void setTx(SmartCounter tx) {
 * 		this.tx = tx;
 * 	}
 * }
 * </pre>
 * 
 * 获得如上计数器的{@link CounterCategory}:
 * 
 * <pre>
 * CounterCategory counterCategory = PerformanceCounterFactory.getCounters(ServerCounter.class, &quot;ServerCounter&quot;);
 * </pre>
 * <p>
 * 
 * Created by Coral
 * @see PerformanceCounterFactory
 * @see PerformanceCounter
 * @see PerformanceCounterType
 */
public class CounterCategory implements Observable {
	private static final int MAX_INSTANCES = 1000;
	private static final String ELSE_INSTANCE_NAME = "else...";

	private String name;
	private Object syncRoot;
	private CounterCategorySchema schema;
	private Map<String, CounterCategoryInstance> instances;
	private CounterCategoryInstance defaultInstance;

	public String getName() {
		return name;
	}

	CounterCategory(String category, Class<?> categoryClazz) {
		name = category;
		syncRoot = new Object();
		schema = new CounterCategorySchema(category, categoryClazz);
		instances = new HashMap<String, CounterCategoryInstance>();
		defaultInstance = null;
	}

	public Object getInstanceRefer(String instance) {
		CounterCategoryInstance ret;
		synchronized (syncRoot) {
			ret = instances.get(instance);
			if (ret == null) {
				ret = schema.createCounterInstance(instance);
				instances.put(instance, ret);
				if (instances.size() > MAX_INSTANCES) {
					if (defaultInstance == null) {
						defaultInstance = schema.createCounterInstance(ELSE_INSTANCE_NAME);
					}
					ret = defaultInstance;
				}
			}
		}
		return ret.getReferredObject();
	}

	public List<CounterCategoryInstance> getAllInstances() {
		ArrayList<CounterCategoryInstance> ret = new ArrayList<CounterCategoryInstance>();
		synchronized (syncRoot) {
			for (CounterCategoryInstance i : instances.values()) {
				ret.add(i);
			}
			if (defaultInstance != null) {
				ret.add(defaultInstance);
			}
		}
		return ret;
	}

	public int getInstanceCount() {
		return instances.size();
	}

	public CounterCategorySchema getTemplate() {
		return schema;
	}

	@Override
	public String getObserverName() {
		return name;
	}

	@Override
	public List<ObserverReportColumn> getObserverColumns() {
		return schema.getObserverColumns();
	}

	@Override
	public List<ObservableUnit> getObserverUnits() {
		List<ObservableUnit> ret = new ArrayList<ObservableUnit>();
		synchronized (ret) {
			for (CounterCategoryInstance i : instances.values()) {
				ret.add(i);
			}
		}
		return ret;
	}
}
