package org.helium.perfmon;

import org.helium.perfmon.annotation.PerformanceCounterCategory;
import org.helium.perfmon.observation.ObserverManager;
import org.helium.perfmon.spi.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 创建计数器的通用类型
 * 
 * TODO: 如何处理自己扩展类型的计数器比如Database
 * 
 * Created by Coral
 */
public final class PerformanceCounterFactory {
	private static Object syncRoot;
	private static Map<String, CounterCategory> categorys;
	private static Map<PerformanceCounterType, CounterBuilder> builders;

	static {
		syncRoot = new Object();
		categorys = new HashMap<String, CounterCategory>();

		builders = new HashMap<PerformanceCounterType, CounterBuilder>();
		builders.put(PerformanceCounterType.NUMBER, NumberCounterBuilder.INSTANCE);
		builders.put(PerformanceCounterType.QPS, QpsCounterBuilder.INSTANCE);
		builders.put(PerformanceCounterType.RATIO, RatioCounterBuilder.INSTANCE);
		builders.put(PerformanceCounterType.THROUGHPUT, ThroughputCounterBuilder.INSTANCE);
		builders.put(PerformanceCounterType.TRANSACTION, TransactionCounterBuilder.INSTANCE);
	}

	/**
	 * 
	 * 通过@PerformanceCounterCategory和@PerformanceCounter标注，获取或创建一个性能计数器
	 * 
	 * @param <E>
	 * @param categoryClazz
	 * @param instance
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getCounters(Class<E> categoryClazz, String instance) {
		PerformanceCounterCategory anno = categoryClazz.getAnnotation(PerformanceCounterCategory.class);
		if (anno == null) {
			throw new IllegalArgumentException("only support class annotated with @PerformanceCounterCategory");
		}

		String catName = anno.value();
		CounterCategory category;
		synchronized (syncRoot) {
			category = categorys.get(catName);
			if (category == null) {
				category = new CounterCategory(catName, categoryClazz);
				categorys.put(catName, category);
				ObserverManager.register(category);
			}
		}

		return (E) category.getInstanceRefer(instance);
	}

	public static CounterCategory getCategory(String name) {
		synchronized (syncRoot) {
			return categorys.get(name);
		}
	}

	public static List<CounterCategory> getAllCategorys() {
		List<CounterCategory> ret = new ArrayList<CounterCategory>();
		synchronized (syncRoot) {
			for (CounterCategory c : categorys.values()) {
				ret.add(c);
			}
		}
		return ret;
	}

	public static CounterBuilder getBuilder(PerformanceCounterType type) {
		return builders.get(type);
	}

}
