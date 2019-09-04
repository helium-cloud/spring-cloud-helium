package org.helium.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Created by Coral on 8/6/15.
 */
public class MapComparator {
	public enum ModifyType {
		INSERT,
		UPDATE,
		DELETE,
	}

	public static class Modification<K, V> {
		private ModifyType modifyType;
		private K key;
		private V value;

		public ModifyType getModifyType() {
			return modifyType;
		}

		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public Modification(ModifyType modifyType, K key, V value) {
			this.modifyType = modifyType;
			this.key = key;
			this.value = value;
		}
	}

	/**
	 * 对比两个map
	 * @param lm
	 * @param rm
	 * @param <K>
	 * @param <V>
	 * @return
	 */
	public static <K, V> List<Modification<K, V>> compare(Map<K, V> lm, Map<K, V> rm) {
		List<Modification<K, V>> result = new ArrayList<>();
		//
		// 遍历新map, 对比原map
		for (Entry<K, V> re: rm.entrySet()) {
			V lv = lm.get(re.getKey());
			if (lv == null) {
				result.add(new Modification<K, V>(ModifyType.INSERT, re.getKey(), re.getValue()));
			} else {
				if (!lv.equals(re.getValue())) {
					result.add(new Modification<K, V>(ModifyType.UPDATE, re.getKey(), re.getValue()));
				}
			}
		}
		//
		// 遍历原map, 寻找新map中已删除的值
		for (Entry<K, V> le: lm.entrySet()) {
			if (rm.get(le.getKey()) == null) {
				result.add(new Modification<K, V>(ModifyType.DELETE, le.getKey(), le.getValue()));
			}
		}
		return result;
	}
}
