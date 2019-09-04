/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Jun 8, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 
 * 具备计数器的CachedObserverExecutor
 * 
 * Created by Coral
 */
public class CachedObservableExecutor extends ObservableExecutor
{ 
	private ThreadPoolExecutor innerExecutor;
	
	public CachedObservableExecutor(String name, Executor executor)
	{
		super(name, executor);
		this.innerExecutor = (ThreadPoolExecutor)executor;
	}
	
	@Override
	public void execute(Runnable task)
	{
		this.getSizeCounter().setRawValue(innerExecutor.getActiveCount());
		super.execute(task);
	}
}
