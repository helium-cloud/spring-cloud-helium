/*
 * FAE, Feinno App Engine
 *  
 * Create by zhangyali 2011-02-25
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util.container;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 
 * <b>描述: </b>用户类型 核心类,提供了获取配置表信息方法
 * <p>
 * <b>功能: </b>
 * <p>
 * <b>用法: </b>
 * <p>
 *
 * @author zhangyali
 *
 * @param <K>
 * @param <V>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class SortedDictionary<K extends Comparable, V>
{
	private ConcurrentSkipListMap<K, Node> searchEntrys = new ConcurrentSkipListMap<K, Node>();

	public SortedDictionary()
	{
	}

	public void clear()
	{
		searchEntrys.clear();
	}

	public void add(K beginKey, K endKey, V value)
	{
		Node itemKey = searchEntrys.get(beginKey);
		if (itemKey != null) {
			throw new RuntimeException(String.format("key already exists! %s", beginKey));
		} else {
			searchEntrys.put(endKey, new Node(beginKey, endKey, value));
		}
	}

	/**
	 * 根据key值,检索相应的值对象,并返回其索引位置. 存放值对象所对应的key：seg; 存放索引所对应的key：index
	 * 
	 * @param key
	 *            key值
	 * @return 返回map对象,包括两个值:一个就是key对应的KeyValueObject对象,另一个就是
	 *         KeyValueObject对象的索引位置
	 */
	public V search(K key)
	{
		Entry<K, Node> e = searchEntrys.ceilingEntry(key);
		if (e != null) {
			Node node = e.getValue();
			if (key.compareTo(node.beginKey) >= 0 && key.compareTo(node.endKey) <= 0) {
				return node.value;
			} else {
				return e.getValue().value;
			}
		} else {
			return null;
		}
	}
	
	private class Node
	{
		private K beginKey;
		private K endKey;
		private V value;

		public Node(K begin, K end, V value)
		{
			beginKey = begin;
			endKey = end;
			this.value = value;
		}
	}
}
