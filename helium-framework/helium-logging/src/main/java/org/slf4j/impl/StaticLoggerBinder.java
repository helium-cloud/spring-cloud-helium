/*
 * FAE, Feinno App Engine
 *  
 * Create by zhangyali 2011-01-05
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.slf4j.impl;

import org.helium.logging.spi.LoggerFactoryImpl;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.spi.LoggerFactoryBinder;


/**
 * The binding of {@link LoggerFactory} class with an actual instance of
 * {@link ILoggerFactory} is performed using information returned by this class.
 * 
 * @author zhangyali@feinno.com
 */
public class StaticLoggerBinder implements LoggerFactoryBinder
{
	static{
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
	/**
	 * The unique instance of this class.
	 */
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	/**
	 *  Logger version.
	 */
	public static String REQUESTED_API_VERSION = "1.0.0";
	
	
	private static final String loggerFactoryClassStr = LoggerFactoryImpl.class.getName();
	
	/**
	 * The ILoggerFactory instance returned by the {@link #getLoggerFactory}
	 * method should always be the same object
	 */
	private final ILoggerFactory loggerFactory;
	
	/**
	 * Return the singleton of this class.
	 * 
	 * @return the StaticLoggerBinder singleton
	 */
	public static final StaticLoggerBinder getSingleton()
	{
		return SINGLETON;
	}
	
	private StaticLoggerBinder()
	{
		this.loggerFactory = LoggerFactoryImpl.INSTANCE;
	}

	public ILoggerFactory getLoggerFactory()
	{
		return loggerFactory;
	}

	public String getLoggerFactoryClassStr()
	{
		return loggerFactoryClassStr;
	}

}
