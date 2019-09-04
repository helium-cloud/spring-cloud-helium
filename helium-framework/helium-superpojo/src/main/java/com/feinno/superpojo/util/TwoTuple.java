package com.feinno.superpojo.util;

/**
 * 
 * <b>描述: </b>一个二元组，用于存储两个数据，例如K和V
 * <p>
 * <b>功能: </b>一个二元组，用于存储两个数据，例如K和V
 * <p>
 * <b>用法: </b>正常的对象创建和使用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 * @param <K>
 * @param <V>
 */
public class TwoTuple<K, V> {
	K first;
	V second;

	public TwoTuple(K first, V second) {
		this.first = first;
		this.second = second;
	}

	public final K getFirst() {
		return first;
	}

	public final void setFirst(K first) {
		this.first = first;
	}

	public final V getSecond() {
		return second;
	}

	public final void setSecond(V second) {
		this.second = second;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof TwoTuple)) {
			return false;
		}
		@SuppressWarnings("rawtypes")
		TwoTuple twoTuple = (TwoTuple) obj;
		if (first == twoTuple.getFirst() && second == twoTuple.getSecond()) {
			return true;
		}
		if (first != null) {
			if (twoTuple.getFirst() == null || !first.equals(twoTuple.getFirst())) {
				return false;
			}
		} else {
			if (twoTuple.getFirst() != null) {
				return false;
			}
		}
		if (second != null) {
			if (twoTuple.getSecond() == null || !second.equals(twoTuple.getSecond())) {
				return false;
			}
		} else {
			if (twoTuple.getSecond() != null) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result += (first != null ? first.hashCode() * 17 : 0);
		result += (second != null ? second.hashCode() * 17 : 0);
		return result;
	}

	public String toString() {
		return "first:" + first + ", second:" + second;
	}
}
