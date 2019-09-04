/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Sep 15, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 线程工厂, 目前仅为了能够修改线程的名称
 * 
 * Created by Coral
 */
public class ThreadFactorys
{
	private static final ThreadFactory DEFAULT = Executors.defaultThreadFactory();
	
	public static ThreadFactory forApp(String name)
	{
		return create("p", name);
	}
	
	public static ThreadFactory forIO(String name)
	{
		return create("io", name);
	}
	
	public static ThreadFactory create(final String prefix, final String name)
	{
		return new ThreadFactory() {
			private int i = 0;
			@Override
			public Thread newThread(Runnable r)
			{
				i++;
				Thread thread = DEFAULT.newThread(r);
				thread.setName(prefix + "-" + name + "-" + i);
				return thread;
			}
		};
	}
}
