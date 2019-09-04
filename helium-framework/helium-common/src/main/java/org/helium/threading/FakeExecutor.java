/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Aug 4, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

import java.util.concurrent.Executor;

/**
 * 
 * 假冒的线程池, 没有线程池的作用, 用于某些希望直接穿越线程池的场合
 * 
 * Created by Coral
 */
public class FakeExecutor implements Executor
{
	public static final Executor INSTANCE = new FakeExecutor();
	
	@Override
	public void execute(Runnable command)
	{
		command.run();
	}
}
