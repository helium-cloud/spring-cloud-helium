/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-2-14
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

/**
 * 
 * <b>描述: </b>当线程池被占满时，再试图添加任务，则抛出此异常，此异常用于标识线程池忙
 * <p>
 * <b>功能: </b>用于标识线程池忙的异常
 * <p>
 * <b>用法: </b>由线程池抛出，外部负责捕获和处理
 * <p>
 * 
 * Created by Coral
 * 
 */
public class ExecutorBusyException extends RuntimeException
{
	public ExecutorBusyException(String message, String executorName)
	{
		super("Executor[" + executorName + "] busy!:" + message);
	}
	
	private static final long serialVersionUID = 4477350638750128917L;	
}
