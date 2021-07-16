/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-10-8
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.superpojo.type;

import java.util.Date;

/**
 * 
 * <b>描述: </b>类.Net的DateTime的实现类
 * <p>
 * <b>功能: </b>类.Net的DateTime的实现类
 * <p>
 * <b>用法: </b>正常的方法调用，无特殊性
 * <p>
 *
 * @author 高磊 gaolei@feinno.com
 *
 */
public class DateTime implements Comparable
{
//	public static final DateTime MIN = new DateTime();
//	public static final DateTime MAX = new DateTime();
	
	private Date t;	
	public DateTime()
	{
		t = new Date();
	}

	public DateTime(Date t) {
		this.t = t;
	}

	public DateTime(long mills)
	{
		t = new Date(mills);
	}
	
//	public DateTime(int year, int month, int day)
//	{
//		t = Calendar.set(year + 1900, month, day).
//		date = new Date(year, month, day);
//	}

	public static DateTime now()
	{
		return new DateTime();
	}
	
//	public static DateTime utcNow()
//	{
//		return new DateTime();
//	}
	
//	public static DateTime parse(String format)
//	{
//		return new DateTime();
//	}
	
//	public Date toDate()
//	{
//		return calender.getTime();
//	}
//	
//	public int getYear()
//	{
//		return calender.get(Calendar.YEAR);
//	}
//	
//	public int getMonth()
//	{
//		return calender.get(Calendar.MONTH);
//	}
//	
//	public int getDayOfMonth()
//	{
//		return calender.get(Calendar.DAY_OF_MONTH);
//	}
//	
//	public int getHour()
//	{
//		return calender.get(Calendar.HOUR_OF_DAY);
//	}
//	
//	public int getMinute()
//	{
//		return calender.get(Calendar.MINUTE);
//	}
//	
//	public int getSecond()
//	{
//		return calender.get(Calendar.SECOND);
//	}
//	
//	public int getMillisecond()
//	{
//		return calender.get(Calendar.MILLISECOND);
//	}
	
	public Date getDate() {
		return t;
	}

	@SuppressWarnings("deprecation")
	public TimeSpan getTimeOfDay()
	{
		Date d = new Date(t.getYear(), t.getMonth(), t.getDate());
		long span = t.getTime() - d.getTime();
		return new TimeSpan(span);
	}
	
	public long getTime()
	{
		return t.getTime();
	}
	
	public TimeSpan substract(DateTime rval)
	{
		return new TimeSpan(t.getTime() - rval.t.getTime());
	}

	public DateTime add(TimeSpan span)
	{
		return new DateTime(t.getTime() + span.getTotalMillseconds());
	}

	@Override
	public int compareTo(Object o)
	{
		if (DateTime.class.equals(o.getClass())) {
			DateTime rval = (DateTime)o;
			return (int)(t.getTime() - rval.t.getTime());
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public String toString()
	{
		return "DateTime [t=" + t + "]";
	}
}
