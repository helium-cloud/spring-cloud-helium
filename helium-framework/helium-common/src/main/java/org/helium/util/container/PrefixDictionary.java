/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-9-13
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * <b>描述: </b>这是一个前缀字典类(Map)，通常的Map是一个Key和Value的键值对，用于通过一个明确的Key来寻找到与之对应的Value,
 * 但当Key被干扰过时 ，Map就无法找到对应的Value
 * ，为了对付这个问题，该类应运而生，该类使用二分搜索，通过Key的前缀匹配(类似String的startsWith)
 * 来进行匹配，寻找到与之对应的Value，因此，该类的Key默认为String类型，Value可以通过泛型自定义，其余则与Map相似,例如Key值的唯一 <br>
 * <p>
 * <b>功能: </b>前缀字典类(Map)，用于以Key的前缀匹配方式获得Value的值
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * PrefixDictionary&lt;String&gt; prefix = new PrefixDictionary&lt;String&gt;();
 * prefix.put("go101.do/foo1", "key1");
 * prefix.put("go102.do/foo1", "key2");
 * prefix.put("go103.do/foo1", "key3");
 * String value = prefix.get("go102.do/foo123456789");
 * assertEquals("key2", value);
 * </pre>
 * <p>
 * 
 * Created by Coral
 * 
 * @param <E>
 */
@Deprecated
public class PrefixDictionary<E> {
	/*
	 * 返回最大匹配的前缀 要求最大复杂度是O(logn)
	 */
	Map<String, E> parameters = new TreeMap<String, E>();

	public E get(String target) {
		/*
		 * O(n)
		 */
		List<String> list = null;
		String hit = null;
		if (parameters != null) {
			list = new ArrayList<String>();
			for (String key : parameters.keySet()) {
				list.add(key);
			}
		}

		// // 最笨方法查找
		// int len = Integer.MAX_VALUE;
		// for (String str : list) {
		// if (target.startsWith(str)) {
		// if (str.length() < len ){
		// hit = str;
		// }
		// }
		// }
		// 折半查找
		hit = binarySearch(0, list.size() - 1, target, list);
		if (hit == null) {
			return null;
		}
		if (parameters != null) {
			return parameters.get(hit);
		} else {
			return null;
		}
	}

	public void put(String prefix, E obj) {
		parameters.put(prefix, obj);
	}

	public void remove(String prefix) {
		if (parameters != null) {
			parameters.remove(prefix);
		}
	}

	/**
	 * 折半查找
	 * 
	 * @param low
	 * @param high
	 * @param key
	 * @param list
	 * @return
	 */
	private static String binarySearch(int low, int high, String key, List<String> list) {

		int mid;
		int result;

		if (low <= high) {
			mid = (low + high) / 2;
			result = key.compareTo(list.get(mid));
			if (result < 0) {
				if (key.startsWith(list.get(mid))) {
					key = list.get(mid);
					return binarySearch(low, mid, key, list);
				}
				return binarySearch(low, mid - 1, key, list);
			} else if (result > 0) {
				if (key.startsWith(list.get(mid))) {
					key = list.get(mid);
					return binarySearch(mid, high, key, list);
				}
				return binarySearch(mid + 1, high, key, list);
			} else if (result == 0) {
				if (key.startsWith(list.get(mid))) {
					key = list.get(mid);
				}
			}
			return list.get(mid);
		}
		return null;
	}
}
