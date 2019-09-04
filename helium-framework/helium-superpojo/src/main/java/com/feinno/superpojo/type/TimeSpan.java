/*
 * FAE, Feinno App Engine
 *  
 * Create by Huangxianglong 2011-7-27
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package com.feinno.superpojo.type;

import java.sql.Date;

/**
 * 
 * <b>描述: </b>实现了.net的TimeSpan
 * <p>
 * <b>功能: </b>与.net的TimeSpan类
 * <p>
 * <b>用法: </b>正常工具类的使用方法
 * <p>
 * 
 * @author huangxianglong
 * 
 */
public class TimeSpan
{
	public final static long YEAR_MILLIS = 365 * 3600 * 24 * 1000;
	public final static long DAY_MILLIS = 3600 * 24 * 1000;
	public final static long HOUR_MILLIS = 3600 * 1000;
	public final static long MINUTE_MILLIS = 60 * 1000;
	public final static long SECOND_MILLIS = 1000;

	long millis = 0l;
	
	public TimeSpan(long millis)
	{
		this.millis = millis > 0l ? millis : 0;
	}

	public TimeSpan(Date begin, Date end)
	{
		millis = end.getTime() - begin.getTime();
		millis = millis > 0l ? millis : 0;
	}

	public int getDays()
	{
		return (int) (millis / DAY_MILLIS);
	}

	public int getHours()
	{
		return (int) (millis % DAY_MILLIS / HOUR_MILLIS);
	}

	public int getMinutes()
	{
		return (int) (millis % HOUR_MILLIS / MINUTE_MILLIS);
	}

	public int getSeconds()
	{
		return (int) (millis % MINUTE_MILLIS / SECOND_MILLIS);
	}

	public int getTotalHours()
	{
		return (int) (millis / HOUR_MILLIS);
	}

	public int getTotalMinutes()
	{
		return (int) (millis / MINUTE_MILLIS);
	}

	public int getTotalSeconds()
	{
		return (int) (millis / SECOND_MILLIS);
	}
	
	public long getTotalMillseconds()
	{
		return millis;
	}
}
