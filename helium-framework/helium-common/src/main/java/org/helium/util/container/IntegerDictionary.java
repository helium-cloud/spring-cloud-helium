/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Apr 7, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.util.container;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 满足如下需求的字典类<br>
 * 1. 新增一个对象, 并提供一个连续的整数号码, lock-free, O(1)<br>
 * 2. 通过对象本身查找对象的序号, lock, O(1)<br>
 * 3. 通过整数号码查找到对象, lock-free, O(1)<br>
 * 4. 删除一个对象, lock， O(1)<br>
 * int的key不重复使用
 *  
 * Created by Coral
 */
@SuppressWarnings("rawtypes")
public class IntegerDictionary<E>
{
	private static final int CELL_SIZE = 1024;
	private static final int MAX_CELL = 64;
	
	private static class Cell<E>
	{
		Object[] values = new Object[CELL_SIZE];
	}
	
	private Object sync;
	private int order;
	private Cell[] cells;
	private Map<E, Integer> index;
	
	public IntegerDictionary()
	{
		sync = new Object();
		cells = new Cell[MAX_CELL];
		order = 0;
		index = new HashMap<E, Integer>();		
	}
	
	public int addOrGet(E e)
	{
		//
		// 0被忽略掉了
		synchronized(sync) {
			Integer i = index.get(e);
			if (i != null) {
				return i.intValue();
			}
				
			if (order > CELL_SIZE * MAX_CELL) {
				throw new IllegalStateException("exceed max size");
			}
			order++;
			int n = order / CELL_SIZE;
			int r = order % CELL_SIZE;
			Cell cell = cells[n];
			if (cell == null) {
				cell = new Cell();
				cells[n] = cell;
			}
			cell.values[r] = e;
			index.put(e, order);
			return order;
		}
	}
	
	public void put(int k, E e)
	{
		synchronized (sync) {
			int n = k / CELL_SIZE;
			if (cells[n] == null) { 
				for (int i = order / CELL_SIZE; i <= n; i++) {
					if (cells[i] == null) {
						cells[i] = new Cell();
					}
				}
			}
			int r = k % CELL_SIZE;
			cells[n].values[r] = e;
			index.put(e, k);
			order = k;
		}
	}
	
	public int get(E e)
	{
		synchronized(sync) {
			Integer i = index.get(e);
			return i == null ? -1 : i.intValue();
		}
	}
	
	@SuppressWarnings("unchecked")
	public E get(int i)
	{
		int n = i / CELL_SIZE;
		if (n >= MAX_CELL && cells[n] == null) {
			return null;
		}
		int r = i % CELL_SIZE;
		return (E)cells[n].values[r];
	}
}
