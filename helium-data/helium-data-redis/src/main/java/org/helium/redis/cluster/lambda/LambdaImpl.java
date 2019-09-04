package org.helium.redis.cluster.lambda;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA. User: lihongbo Date: 13-5-16 Time: 下午4:35 To
 * change this template use File | Settings | File Templates.
 */
public class LambdaImpl {

	public static <E> int sum(Collection<E> list, LambdaActionInt<E> action) {
		int result = 0;
		if (list != null)
			for (E f : list)
				result += action.run(f);
		return result;
	}

	public static <E, N> E max(Collection<E> list, LambdaActionObject<E, N> action) {
		Map<N, ArrayList<E>> tmpMap = new HashMap<N, ArrayList<E>>();
		N max = null;
		for (E f : list) {
			ArrayList<E> tmp = tmpMap.get(action.run(f));
			if (tmp == null)
				tmp = new ArrayList<E>();
			tmp.add(f);
			tmpMap.put(action.run(f), tmp);
			max = action.run(f);
		}

		for (N n : tmpMap.keySet()) {
			int i = compare(max, n);
			if (i < 0)
				max = n;
		}
		return tmpMap.get(max).get(0);
	}

	public static <E> List<E> where(Collection<E> list, LambdaActionBool<E> action) {
		List<E> result = new ArrayList<E>();
		if (list != null)
			for (E f : list) {
				if (action.run(f))
					result.add(f);
			}
		return result;
	}

	public static <E, N> List<E> orderBy(Collection<E> list, LambdaActionObject<E, N> action) {
		Map<N, ArrayList<E>> tmpMap = new TreeMap<N, ArrayList<E>>();
		for (E f : list) {
			ArrayList<E> tmp = tmpMap.get(action.run(f));
			if (tmp == null)
				tmp = new ArrayList<E>();
			tmp.add(f);
			tmpMap.put(action.run(f), tmp);
		}

		// N[] keys = (N[]) tmpMap.keySet().toArray();
		// Arrays.sort(keys);
		List<E> result = new ArrayList<E>();
		for (N key : tmpMap.keySet()) {
			result.addAll(tmpMap.get(key));
		}
		return result;
	}

	public static <E, N> List<List<E>> groupBy(Collection<E> list, LambdaActionObject<E, N> action) {
		Map<N, ArrayList<E>> tmpMap = new TreeMap<N, ArrayList<E>>();
		for (E f : list) {
			ArrayList<E> tmp = tmpMap.get(action.run(f));
			if (tmp == null)
				tmp = new ArrayList<E>();
			tmp.add(f);
			tmpMap.put(action.run(f), tmp);
		}
		// N[] keys = (N[]) tmpMap.keySet().toArray();
		// Arrays.sort(keys);
		List<List<E>> result = new ArrayList<List<E>>();
		for (N key : tmpMap.keySet()) {
			result.add(tmpMap.get(key));
		}
		return result;
	}

	private static <N> int compare(N n1, N n2) {
		if (n1 instanceof Number) {

			if (n1 instanceof AtomicInteger) {
				AtomicInteger tmpn1 = (AtomicInteger) n1;
				AtomicInteger tmpn2 = (AtomicInteger) n2;

				return tmpn1.get() - tmpn2.get();
			} else if (n1 instanceof AtomicLong) {
				AtomicLong tmpn1 = (AtomicLong) n1;
				AtomicLong tmpn2 = (AtomicLong) n2;
				int result = 0;
				if (tmpn1.get() > tmpn2.get()) {
					result = 1;
				} else if (tmpn1.get() < tmpn2.get()) {
					result = -1;
				}
				return result;
			} else if (n1 instanceof BigDecimal) {
				BigDecimal tmpn1 = (BigDecimal) n1;
				BigDecimal tmpn2 = (BigDecimal) n2;

				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof BigInteger) {
				BigInteger tmpn1 = (BigInteger) n1;
				BigInteger tmpn2 = (BigInteger) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Byte) {
				Byte tmpn1 = (Byte) n1;
				Byte tmpn2 = (Byte) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Double) {
				Double tmpn1 = (Double) n1;
				Double tmpn2 = (Double) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Double) {
				Double tmpn1 = (Double) n1;
				Double tmpn2 = (Double) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Float) {
				Float tmpn1 = (Float) n1;
				Float tmpn2 = (Float) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Float) {
				Float tmpn1 = (Float) n1;
				Float tmpn2 = (Float) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Integer) {
				Integer tmpn1 = (Integer) n1;
				Integer tmpn2 = (Integer) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Short) {
				Short tmpn1 = (Short) n1;
				Short tmpn2 = (Short) n2;
				return tmpn1.compareTo(tmpn2);
			} else if (n1 instanceof Short) {
				Short tmpn1 = (Short) n1;
				Short tmpn2 = (Short) n2;
				return tmpn1.compareTo(tmpn2);
			} else {
				throw new RuntimeException(String.format("can not compare %s  type is  %s ", n1, n1.getClass()));
			}
		} else {
			throw new RuntimeException(String.format("can not compare %s not a Number type is  %s ", n1, n1.getClass()));
		}

	}

}