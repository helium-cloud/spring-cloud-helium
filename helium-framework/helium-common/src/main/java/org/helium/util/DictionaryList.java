/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-5-16
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

import java.util.*;

/**
 * 
 * <b>描述: </b>一个类似于Map<K, List<V>>的数据结构<br>
 * <p>
 * <b>功能: </b>一个类似于Map<K, List<V>>的数据结构<br>
 * <p>
 * <b>用法: </b>
 * <p>
 *
 * Created by Coral
 *
 * @param <K>
 * @param <V>
 */
public class DictionaryList<K, V>
{
	public static enum UpdateMode
	{
		INSERT,
		UPDATE,
		DELETE,
	};
	
	private Object syncRoot = new Object();
	private Hashtable<K, List<V>> table;
	private List<V> empty = new ArrayList<V>();

	public DictionaryList()
	{
		table = new Hashtable<K, List<V>>();
	}

	public void fillWith(List<V> list, Func<V, K> getKeyFunc)
	{
		for (V v : list) {
			K key = getKeyFunc.exec(v);
			put(key, v);
		}
	}
	
	/**
	 * 
	 * {在这里补充功能说明}
	 * @param list
	 * @param getKeysFunc
	 */
	public void fillWithKeys(List<V> list, Func<V, List<K>> getKeysFunc)
	{
		for (V v: list) {
			List<K> keys = getKeysFunc.exec(v);
			for (K k: keys) {
				put(k, v);
			}
		}
	}

	public Collection<K> keys()
	{
		ArrayList<K> keys = new ArrayList<K>();

		for (Enumeration<K> e = table.keys(); e.hasMoreElements();) {
			keys.add(e.nextElement());
		}
		return keys;
	}

	public List<V> get(K key)
	{
		List<V> res = table.get(key);
		if (res == null) {
			return empty;
		} else {
			return res;
		}
	}

	public void put(K key, V value)
	{
		List<V> list = table.get(key);
		if (list == null) {
			synchronized (syncRoot) {
				list = table.get(key);
				if (list == null) {
					list = new ArrayList<V>();
					table.put(key, list);
				}
			}
		}

		synchronized (syncRoot) {
			list.add(value);
		}
	}

	/**
	 * 
	 * 检测在table中存在的app，但是在rval中检测到改变的值 以及在rval中新增的值
	 * 
	 * @param rval
	 * @param unmatchedCallback
	 */
	public void compareAll(DictionaryList<K, V> rval, Action3<K, UpdateMode, List<V>> unmatchedCallback)
	{
		//
		// 检测rval中的新值
		for (K key: rval.keys()) {
			List<V> llist = table.get(key);
			List<V> rlist = rval.get(key);
			if (llist == null) {
				unmatchedCallback.run(key, UpdateMode.INSERT, rlist);
			}
		}
		
		//
		// 检测this.table中存在的值的变化
		for (Enumeration<K> e = table.keys(); e.hasMoreElements();) {
			K key = e.nextElement();
			List<V> llist = table.get(key);
			List<V> rlist = rval.table.get(key);
			
			if (rlist == null) {
				unmatchedCallback.run(key, UpdateMode.DELETE, null);
			} else if (!listEqual(llist, rlist)) {
				unmatchedCallback.run(key, UpdateMode.UPDATE, rlist);
			}
		}
	}

	/**
	 * 
	 * 这是一个o(n*n)的算法，但是输入数据量很小，所以可以接受
	 * 
	 * @param llist
	 * @param rlist
	 * @return
	 */
	public static <V> boolean listEqual(List<V> llist, List<V> rlist)
	{
		if (llist.size() != rlist.size())
			return false;

		int matchs = 0;
		boolean[] matching = new boolean[llist.size()];
		for (int i = 0; i < llist.size(); i++) {
			V lval = llist.get(i);
			for (int j = 0; j < rlist.size(); j++) {
				V rval = rlist.get(j);
				if ((lval == null && rval == null) || (lval != null && lval.equals(rval))) {
					//
					// 必须兼容在一个list中存在相同值的情况
					// 一个右值只能match一次
					if (!matching[j]) {
						matching[j] = true;
						matchs++;
						break;
					}
				}
			}
		}
		return matchs == llist.size();
	}
}
