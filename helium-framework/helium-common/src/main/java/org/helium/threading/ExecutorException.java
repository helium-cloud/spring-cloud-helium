/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Nov 15, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.threading;

import java.util.concurrent.Executor;

/**
 * 
 * 线程池异常, 当执行Executor.execute方法时抛出
 * 
 * Created by Coral
 */
public class ExecutorException extends RuntimeException
{
	public static final String OVER_MAX_CONCURRENT = "OverMaxConcurrent";
	public static final String OVER_MAX_SESSION = "OverMaxSession";
	public static final String SESSION_TIMEOUT = "SessionTimeout";
		
	private String reason;
	
	public ExecutorException(String reason, Executor executor)
	{
		super("ExecutorException<" + reason + ">:" + executor.toString());
		this.reason = reason;
	}
	
	public String getReason()
	{
		return reason;
	}

	private static final long serialVersionUID = -1543024127623814457L;
}
