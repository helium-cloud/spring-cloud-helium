/*
 * FAE, Feinno App Engine
 *  
 * Create by Coral 2011-1-19
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程安全的循环计数器
 * 
 * <b>描述: </b>线程安全的循环计数器
 * <p>
 * <b>功能: </b>循环计数器
 * <p>
 * <b>用法: </b>
 * <p>
 *
 * @author Coral
 *
 */
public final class LoopCounter
{
	private int n;
	private int mask;
	private int i;
	private AtomicInteger atomic;

	public LoopCounter(int n)
	{
		this.n = n;
		this.mask = nextPower2(n * 40000) - 1;
		this.i = 0;
		atomic = new AtomicInteger(i);
	}

	public void reset()
	{
		i = 0;
	}

	public int next()
	{
		int result = atomic.incrementAndGet() & mask;
		return result % n;
	}
	
	private int nextPower2(int a) {
		int n = 1;
		for (int i = 0; i < 31; i++) {
			if (a <= n)
				return n;
			n = n << 1;
		}
		return -1;
	}
}
