package org.helium.util;

import java.lang.reflect.Array;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 针对Map，List等常用容器帮助类
 * 不保证线程安全，请自行加锁
 * Created by Coral on 7/20/15.
 */
public class CollectionUtils {
	/**
	 * 将Map的值clone为一个列表
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> List<V> cloneValues(Map<K, V> map) {
		List<V> list = new ArrayList<>();
		for (V v: map.values()) {
			list.add(v);
		}
		return list;
	}

	public static <K, V> List<K> cloneKeys(Map<K, V> map) {
		List<K> list = new ArrayList<>();
		for (Entry<K, V> e: map.entrySet()) {
			list.add(e.getKey());
		}
		return list;
	}

	/**
	 * 将Map的值clone为一个列表，可使用filter过滤
	 * @param map
	 * @param filter
	 * @param <K>
	 * @param <V>
	 * @param <V2>
	 * @return
	 */
	public static <K, V, V2> List<V2> cloneValues(Map<K, V> map, Function<V, V2> filter) {
		List<V2> list = new ArrayList<>();
		for (V v: map.values()) {
			V2 v2 = filter.apply(v);
			if (v2 != null) {
				list.add(v2);
			}
		}
		return list;
	}

	/**
	 * 将Map的Entry值clone为一个列表
	 * @param map
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> List<Entry<K, V>> cloneEntrys(Map<K, V> map) {
		List<Entry<K, V>> list = new ArrayList<>();
		for (Entry<K, V> e: map.entrySet()) {
			list.add(e);
		}
		return list;
	}

	public static <V> List<V> cloneList(Iterable<V> from) {
		List<V> list = new ArrayList<>();
		Iterator<V> i = from.iterator();
		while (i.hasNext()) {
			list.add(i.next());
		}
		return list;
	}

	/**
	 * 从list生成Map
	 * @param list
	 * @param getKey
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> Map<K, V> generateHashMap(List<V> list, Function<V, K> getKey) {
		Map<K, V> map = new HashMap<>();
		for (V v: list) {
			K key = getKey.apply(v);
			map.put(key, v);
		}
		return map;
	}

	/**
	 * 合并两个Map
	 * @param mergeTo
	 * @param mergeBy
	 * @param replaceSameKey 是否替换相同的Key
	 * @param <K>
	 * @param <V>
	 */
	public static <K, V> void mergeMap(Map<K, V> mergeTo, Map<K, V> mergeBy, boolean replaceSameKey) {
		for (Entry<K, V> e: mergeBy.entrySet()) {
			if (replaceSameKey) {
				mergeTo.put(e.getKey(), e.getValue());
			} else {
				if (mergeTo.get(e.getKey()) == null) {
					mergeTo.put(e.getKey(), e.getValue());
				}
			}
		}
	}

	/**
	 * 过滤Iterable为List，可附带类型转换
	 * @param iterable
	 * @param func
	 * @param <V1>
	 * @param <V2>
	 * @return
	 */
	public static <V1, V2> List<V2> filter(Iterable<V1> iterable, Function<V1, V2> func) {
		List<V2> l2 = new ArrayList<>();
		Iterator<V1> i = iterable.iterator();
		while (i.hasNext()) {
			V2 v2 = func.apply(i.next());
			if (v2 != null) {
				l2.add(v2);
			}
		}
		return l2;
	}

	/**
	 * 在一个列表中寻找第一个满足条件的值
	 * @param iterable
	 * @param predicate
	 * @param <V>
	 * @return
	 */
	public static <V> V findFirst(Iterable<V> iterable, Predicate<V> predicate) {
		Iterator<V> i = iterable.iterator();
		while (i.hasNext()) {
			V v = i.next();
			if (predicate.test(v)) {
				return v;
			}
		}
		return null;
	}

	/**
	 * 将from有条件的copy到to中
	 * @param from
	 * @param to
	 * @param func
	 * @param <V>
	 * @param <V2>
	 */
	public static <V, V2> void copyTo(Iterable<V> from, List<V2> to, Function<V, V2> func) {
		Iterator<V> i = from.iterator();
		while (i.hasNext()) {
			V2 v2 = func.apply(i.next());
			if (v2 != null) {
				to.add(v2);
			}
		}
	}

	/**
	 * 在数组增加字段，生成新的数组
	 * @param la
	 * @param adds
	 * @param <E>
	 * @return
	 */
	public static <E> E[] appendArray(E[] la, E... adds) {
		int ll = (la == null) ? 0 : la.length;
		int size = ll + adds.length;

		Class<?> elementClazz;
		if (la != null) {
			elementClazz = la.getClass().getComponentType();
		} else {
			elementClazz = adds.getClass().getComponentType();
		}

		E[] r = (E[])Array.newInstance(elementClazz, size);

		for (int i = 0; i < size; i++) {
			if (i < ll) {
				r[i] = la[i];
			} else {
				r[i] = adds[i - ll];
			}
		}
		return r;
	}

	/**
	 * 移除数组中的符合条件的对象
	 * @param la
	 * @param predicate
	 * @param <E>
	 * @return
	 */
	public static <E> E[] removeArrayIf(E[] la, Predicate<E> predicate) {
		if (la.length == 0) {
			return la;
		}
		Class<?> elementClazz = la.getClass().getComponentType();

		List<E> tempList = new ArrayList<>();
		for (int i = 0; i < la.length; i++) {
			if (!predicate.test(la[i])) {
				tempList.add(la[i]);
			}
		}
		return toArray(tempList, elementClazz);
	}

	/**
	 * 将List转为数组
	 * @param list
	 * @param clazz
	 * @param <E>
	 * @return
	 */
	public static <E> E[] toArray(List<E> list, Class<?> clazz) {
		E[] r = (E[])Array.newInstance(clazz, list.size());
		for (int i = 0; i < list.size(); i++) {
			r[i] = list.get(i);
		}
		return r;
	}
}
