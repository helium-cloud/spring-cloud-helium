/*
 * FAE, Feinno App Engine
 *  
 * Create by duyu 2010-11-25
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package com.feinno.superpojo.util;

import java.sql.Timestamp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/**
 * {在这里补充功能说明} 
 */
/**
 * 
 * <b>描述: </b>对日期进行操作的工具类，包含了字符串和日期之间转换的方法
 * <p>
 * <b>功能: </b>日期工具类，提供了字符串和日期之间转换的方法以及其他快捷方法
 * <p>
 * <b>用法: </b>静态方法集
 * <p>
 *
 * @author duyu
 *
 */
public class DateUtil {

    /** yyyy-MM-dd HH:mm:ss.SSS */
    public static final String DEFAULT_DATETIME_HYPHEN_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss.SSS";
    
	/** yyyy-MM-dd HH:mm:ss */
	public static final String DEFAULT_DATETIME_HYPHEN_FORMAT = "yyyy-MM-dd HH:mm:ss";

	/** 缺省日期时间格式设置 */
	public final static String DEFAULT_YEAR_MONTH_DAY_TIME = "yyyyMMddHHmmss";
	
	public final static String COMPLEX_GMT_FORMAT_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";

	public final static String GMT_FORMAT_PATTERN = "d MMM yyyy hh:mm:ss zzz";
	/** 缺省日期格式设置 */
	public final static String DEFAULT_DATE = "yyyyMMdd";

	/** 缺省年月格式设置 */
	public final static String DEFAULT_YEAR_MONTH = "yyyyMM";

	/** 缺省月日设置 */
	public final static String DEFAULT_MONTH_DAY = "MMdd";

	/** 缺省时间格式设置 */
	public final static String DEFAULT_TIME = "HHmmss";

	/** 缺省小时格式设置 */
	public final static String DEFAULT_HOUR = "H";

	public static final Date MIN_VALUE = createDate(0001, 1, 1);
	public static final Date MAX_VALUE = createDate(9999, 12, 31, 23, 59, 59);
	
	public static final Date SmallDateTime_MinValue = createDate(1900, 1, 1);
    public static final Date SmallDateTime_MaxValue = createDate(2079, 6, 6);

    public static final Date MysqlTimeStamp_Min = createDate(1970, 1, 1);
    public static final Date MysqlTimeStamp_Max = createDate(2038, 1, 1);

    public static final Date DateTime_MinValue = createDate(1753, 1, 1);
    public static final Date DateTime_MaxValue = createDate(9999, 12, 31);

	/**
	 * 获得缺省年月日时间格式
	 * 
	 * @return 缺省日期格式
	 */
	public static String getDefaultYearMonthDayTime() {
		return formatDate(DEFAULT_DATETIME_HYPHEN_FORMAT);
	}

	public static String getClientDateTime(Date d) {
		return String.format("%s GMT", formatDate(d, "E, dd MMM yyyy HH:mm:ss").toString());
	}

	/**
	 * 获得缺省年月日时间格式
	 * 
	 * @param d
	 *            要格式化的时间
	 * @return
	 */
	public static String getDefaultYearMonthDayTime(Date d) {
		return formatDate(d, DEFAULT_DATETIME_HYPHEN_FORMAT);
	}

	/**
	 * 获得缺省小时格式
	 * 
	 * @return 缺省小时格式
	 */
	public static int getDefaultHourTime() {
		return Integer.parseInt(formatDate(DEFAULT_HOUR));
	}

	/**
	 * 获得缺省日期格式
	 * 
	 * @return 缺省日期格式
	 */
	public static int getDefaultDate() {
		return Integer.parseInt(formatDate(DEFAULT_DATE));
	}

	/**
	 * 获得缺省年月格式
	 * 
	 * @return 缺省年月格式
	 */
	public static int getDefaultYearMonth() {
		return Integer.parseInt(formatDate(DEFAULT_YEAR_MONTH));
	}

	/**
	 * 获得缺省月日格式
	 * 
	 * @return 缺省月日格式
	 */
	public static int getDefaultMonthDay() {
		return Integer.parseInt(formatDate(DEFAULT_MONTH_DAY));
	}

	/**
	 * 获得缺省时间格式
	 * 
	 * @return 缺省时间格式
	 */
	public static int getDefaultTime() {
		return Integer.parseInt(formatDate(DEFAULT_TIME));
	}

	public static int getDefaultTime(String pattern) {
		return Integer.parseInt(formatDate(pattern));
	}

	/**
	 * 把Date 转变为 java.sql.Date
	 * 
	 * @param date
	 * @return java.sql.Date
	 */
	public static java.sql.Date getSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}

	public static Date getUtilDate(Object obj) throws Exception {
		if (obj instanceof java.sql.Date) {
			return new Date(((java.sql.Date) obj).getTime());
		} else {
			throw new Exception("Class is not java.sql.Date." + obj.getClass());
		}

	}

	/**
	 * 把string 转变为 Date
	 * 
	 * @param date
	 * @return Date
	 * @throws ParseException
	 */
	public static Date getDefaultDate(String dateString) throws ParseException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = format.parse(dateString);
		return date;
	}

	/**
	 * 把string 转变为 Date
	 * 
	 * @param date
	 * @return Date
	 * @throws ParseException
	 */
	public static Date getDefaultDate(String dateString, String pattern)
			throws ParseException {
		DateFormat format = new SimpleDateFormat(pattern);
		Date date = format.parse(dateString);
		return date;
	}
	

	/**
	 * 在原日期的基础上增加
	 * 
	 * @param date
	 *            Date
	 * @param filed
	 *            int
	 * @param count
	 *            int
	 * @return Date
	 */
	public static Date addDatefield(Date date, int filed, int count) {
		if (filed != Calendar.YEAR && filed != Calendar.MONTH
				&& filed != Calendar.DATE && filed != Calendar.HOUR
				&& filed != Calendar.MINUTE && filed != Calendar.SECOND) {
			throw new IllegalStateException("不能处理的时间类型");
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(filed, count);
		return c.getTime();
	}

	/**
	 * 在原日期的增加天
	 * 
	 * @param date
	 *            Date
	 * @param count
	 *            int
	 * @return Date
	 */
	public static Date addDay(Date date, int count) {
		return addDatefield(date, Calendar.DAY_OF_MONTH, count);
	}

	/**
	 * 在原日期的增加月
	 * 
	 * @param date
	 *            Date
	 * @param count
	 *            int
	 * @return Date
	 */
	public static Date addMonth(Date date, int count) {
		return addDatefield(date, Calendar.MONTH, count);
	}

	/**
	 * 得到系统当前日期
	 * 
	 * @param pattern
	 *            日期格式
	 * @return 系统当前日期
	 */
	public static String getSystemCurrentDate(String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date date = new Date();
		String currentDate = format.format(date);
		return currentDate;
	}

	/**
	 * 格式化日期
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @return Date
	 */
	public static String month(String year, String month) {
		StringBuffer sb = new StringBuffer();
		sb.append(year);
		sb.append("-");

		if (month != null && month.length() == 1) {
			month = "0" + month;
		}
		sb.append(month);
		sb.append("-01");
		return sb.toString();
	}

	/**
	 * 格式化日期的基础上加一个月
	 * 
	 * @param year
	 *            年份
	 * @param month
	 *            月份
	 * @return Date
	 */
	public static String monthAdd(String year, String month) {
		int addMonth = Integer.parseInt(month);
		String strAddMonth = Integer.toString(addMonth + 1);
		StringBuffer sb = new StringBuffer();
		if (strAddMonth.equals("13")) {
			sb.append(Integer.parseInt(year) + 1);
			sb.append("-");
			sb.append("01");
			sb.append("-01");
		} else {
			sb.append(year);
			sb.append("-");
			sb.append(strAddMonth);
			sb.append("-01");
		}

		return sb.toString();
	}

	/**
	 * 得到系统当前日期
	 * 
	 * @return 系统当前日期
	 */
	public static Timestamp getSystemCurrentTime() {
		Date currentDate = new Date();
		Timestamp tmspt = new Timestamp(currentDate.getTime());
		return tmspt;
	}

	/**
	 * 将指定日期转化为timstamp类型
	 * 
	 * @param date
	 *            日期
	 * @return Timestamp形式的日期
	 */
	public static Timestamp getTimestamp(Date date) {
		Timestamp tmspt = new Timestamp(date.getTime());
		return tmspt;
	}

	/**
	 * 将指定字符串转化为timstamp类型
	 * 
	 * @param dateString
	 *            日期
	 * @return Timestamp形式的日期
	 * @throws ParseException
	 */
	public static Timestamp getTimestamp(String dateString)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = format.parse(dateString);
		Timestamp tmspt = new Timestamp(date.getTime());
		return tmspt;
	}

	public static String getTimestampString(Object timestamp) {
		Timestamp tsmp = (Timestamp) timestamp;
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		return format.format(new Date(tsmp.getTime()));
	}

	/**
	 * 取两日期的天差
	 * 
	 * @param from
	 * @param to
	 * @return int
	 */
	public static int getDateDiffDay(Date from, Date to) {
		from = truncDate(from);
		to = truncDate(to);

		return getDateFiled(from, to, Calendar.DATE);
	}

	/**
	 * 取两个日期的差 [amount = Calendar.YEAR 年 ] [amount = Calendar.MONTH 月] [amount
	 * =Calendar.DATE 日] [amount = Calendar.HOUR 小时] [amount =
	 * Calendar.MINUTE分钟] [amount = Calendar.SECOND 秒]
	 * 
	 * @param from
	 * @param to
	 * @param field
	 * @return int
	 */
	public static int getDateFiled(Date from, Date to, int field) {
		if (from == null) {
			return 0;
		}
		if (to == null) {
			return 0;
		}
		Date date1 = from;
		Date date2 = to;
		if (!to.after(from)) {
			date1 = to;
			date2 = from;
		}

		if (field != Calendar.YEAR && field != Calendar.MONTH
				&& field != Calendar.DATE && field != Calendar.HOUR
				&& field != Calendar.MINUTE && field != Calendar.SECOND) {
			throw new IllegalStateException("不能处理的时间类型");
		}
		int auditCycle = 0;
		Date tempDate = null;
		Calendar c = Calendar.getInstance();
		c.setTime(date1);
		while (true) {
			c.add(field, 1);
			tempDate = c.getTime();
			if (tempDate.after(date2)) {
				break;
			}
			auditCycle++;
		}

		if (!to.after(from)) {
			return 0 - auditCycle;
		}
		return auditCycle;
	}

	/**
	 * 去掉时分秒
	 * 
	 * @param date
	 * @return Date
	 */
	public static Date truncDate(Date date) {
		if (date == null) {
			return null;
		}
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int y = c.get(Calendar.YEAR);
			int m = c.get(Calendar.MONTH);
			int d = c.get(Calendar.DATE);
			c = Calendar.getInstance();
			c.set(y, m, d);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获得缺省日期
	 * 
	 * @param pattern
	 *            日期格式
	 * @return 缺省日期
	 */
	private static String formatDate(String pattern) {

		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat format = new SimpleDateFormat(pattern);

		String nowDate = format.format(date);

		return nowDate;

	}

	/**
	 * 得到格式化后的字符串
	 * 
	 * @param d
	 *            日期
	 * @param pattern
	 *            格式化字符串
	 * @return
	 */
	public static String formatDate(Date d, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern,Locale.US);
		return format.format(d);

	}

	/**
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int compareDate(Date date1, Date date2) {
		return date1.compareTo(date2);
	}

	public static String formatDateStr(Calendar cal, String format) {
		SimpleDateFormat formator = new SimpleDateFormat(format);

		return formator.format(cal.getTime());

	}

	public static String formatDateStr(Date date, String format) {
		SimpleDateFormat formator = new SimpleDateFormat(format);

		return formator.format(date);

	}

	/**
	 * 获得上一星期日期范围
	 * 
	 * @return
	 */
	public static String[] getPreviousWeek() {
		String[] ret = new String[2];
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.WEEK_OF_YEAR, -1);
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		ret[0] = formatDateStr(calendar, "yyyy-MM-dd");
		calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY + 1);
		ret[1] = formatDateStr(calendar, "yyyy-MM-dd");

		return ret;
	}

	/**
	 * 获得上一月日期范围
	 * 
	 * @return
	 */
	public static String[] getPreviousMonth() {
		String[] ret = new String[2];
		Calendar calendar = Calendar.getInstance();

		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, calendar
				.getActualMinimum(Calendar.DAY_OF_MONTH));
		ret[0] = formatDateStr(calendar, "yyyy-MM-dd");
		calendar.set(Calendar.DAY_OF_MONTH, calendar
				.getActualMaximum(Calendar.DAY_OF_MONTH));
		ret[1] = formatDateStr(calendar, "yyyy-MM-dd");

		return ret;
	}

	/**
	 * 检查日期格式是否正确(yyyy-MM-dd)
	 * 
	 * @param dateString
	 *            String 要检查的日期字符串
	 * @return boolean true:格式正确 false:格式错误
	 */
	public static boolean isRightDateFormat(String dateString) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			format.parse(dateString);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获得附件保存日期格式(yyyyMMddHHmmssSSS_)
	 * 
	 * @param date
	 *            附件创建日期
	 * @return 附件保存日期格式
	 */
	public static String getAttachmentTime(Timestamp date) {
		Date createDate = new Date(date.getTime());
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS_");
		return simpleDateFormat.format(createDate);
	}

	/**
	 * 将时间字符串格式成指定的时间格式
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDateString(String date, String pattern) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date d = null;
		try {
			d = simpleDateFormat.parse(date);

		} catch (ParseException e) {
			d = new Date();
		}
		return simpleDateFormat.format(d);
	}
	
	/**
     * 转换含有星期的日期字符串<br>
     * (EEE, dd MMM yyyy HH:mm:ss zzz)
     * 
     * @param date
     * @param pattern
     * @return
     */
    public static Date parseComplexGMTStringDate(String date) {
        Date d = null;
        try {
            d = getComplexGMTFormater().parse(date);
        } catch (ParseException e) {
            d = null;
        }
        return d;
    }

	/**
	 * 取日期的相应部分
	 * 
	 * @param date
	 * @param field
	 * @return
	 */
	public static int getDateField(Date date, int field) {
		try {
			if (field != Calendar.YEAR && field != Calendar.MONTH
					&& field != Calendar.DATE && field != Calendar.HOUR
					&& field != Calendar.MINUTE && field != Calendar.SECOND
					&& field != Calendar.DAY_OF_WEEK && field != Calendar.HOUR_OF_DAY
					&& field !=Calendar.DAY_OF_MONTH) {
				throw new IllegalStateException("不能处理的时间类型");
			}
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			return c.get(field);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 取得日期的星期<br>
	 * SUNDAY = 1 MONDAY = 2 TUESDAY = 3 WEDNESDAY = 4 THURSDAY = 5 FRIDAY = 6
	 * SATURDAY = 7
	 * 
	 * @param date
	 * @return
	 */
	public static int getDayOfWeak(Date date) {
		return getDateField(date, Calendar.DAY_OF_WEEK);
	}
	
	/**
	 * 取得指定日期所在当前月的位置<br>
	 * 一个月中第一天的值为 1
	 * @param date
	 * @return
	 */
	public static int getDayOfMonth(Date date){
		return getDateField(date, Calendar.DAY_OF_MONTH);
	}

	/**
	 * 取该日期所在月份的开始日
	 * 
	 * @param date
	 * @return Date
	 */
	public static Date getMonthFirst(Date date) {
		try {
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int yr = c.get(Calendar.YEAR);
			int mon = c.get(Calendar.MONTH);
			c.set(yr, mon, 1);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 取该日期所在月份的结束日
	 * 
	 * @param date
	 * @return
	 */
	public static Date getMonthEnd(Date date) {
		try {
			// 当月第一天
			Date ret = getMonthFirst(date);
			// 下月第一天
			ret = addMonth(ret, 1);
			Calendar c = Calendar.getInstance();
			c.setTime(ret);
			// 下月第一天的前一天即为上月最后一天
			c.add(Calendar.DATE, -1);
			return c.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 
	 * 转换为UTC时间
	 * 
	 * @param dt
	 * @return
	 */
	public static Date getUTCDate(Date dt) {
		Calendar cal = GregorianCalendar.getInstance();
		// 2、取得时间偏移量：
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);

		// 3、取得夏令时差：
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);

		// 之后调用cal.get(int x)或cal.getTimeInMillis()方法所取得的时间即是UTC标准时间。
		return new Date(dt.getTime()-(zoneOffset + dstOffset));
	}

	public static Date getUTCNow() {
		return getUTCDate(new Date());
	}

	private static Date createDate(int argYear, int argMonth, int argDate) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(argYear,
				argMonth - 1, argDate);
		gregorianCalendar.setLenient(false);
		return gregorianCalendar.getTime();
	}

	private static Date createDate(int argYear, int argMonth, int argDate,
			int hour, int minute, int second) {
		GregorianCalendar gregorianCalendar = new GregorianCalendar(argYear,
				argMonth - 1, argDate, hour, minute, second);
		gregorianCalendar.setLenient(false);
		return gregorianCalendar.getTime();
	}
	
	public static boolean verfiySmallDateTime(Date date)
    {
		if((date.after(DateUtil.SmallDateTime_MinValue)) && (date.before(DateUtil.SmallDateTime_MaxValue)))
            return true;
        else
            return false;
    }

	public static Date getMysqlTimeStamp(Date date)
    {
        if (date.after(MysqlTimeStamp_Max))
            return MysqlTimeStamp_Max;
        if (date.before(MysqlTimeStamp_Min))
            return MysqlTimeStamp_Min;
        return date;
    }
	
	/**
	 * 生日为2月29日的用户,日期减去一天
	 * 
	 */
	public static Date getBirthDateFor229(Date birthDate){
        Calendar c = Calendar.getInstance();
        c.setTime(birthDate);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);
        if (m == 2 && d == 29)
        {
            birthDate = addDay(birthDate , -1);
        }
        return birthDate;
	}
	
	public static Date getGMTDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//2、取得时间偏移量：   
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);  
		//3、取得夏令时差：   
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);  
		//4、从本地时间里扣除这些差量，即可以取得GMT时间：   
		cal.add(java.util.Calendar.MILLISECOND, +(zoneOffset + dstOffset));  
		return new Date(cal.getTimeInMillis());  
	}

	
	public static java.sql.Date getGMTDate(java.sql.Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		//2、取得时间偏移量：   
		int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET);  
		//3、取得夏令时差：   
		int dstOffset = cal.get(java.util.Calendar.DST_OFFSET);  
		//4、从本地时间里扣除这些差量，即可以取得GMT时间：   
		cal.add(java.util.Calendar.MILLISECOND, +(zoneOffset + dstOffset));  
		return new java.sql.Date(cal.getTimeInMillis());  
	}
	/**
	 * 
	 * @return 返回Timezone为GMT,Locale为UK的日历
	 */
	public static Calendar getGMTCalendar() {
	    return Calendar.getInstance(TimeZone.getTimeZone("GMT") , Locale.UK);
	}
	public static SimpleDateFormat getGMTFormater() {
	    SimpleDateFormat format = new SimpleDateFormat("d MMM yyyy hh:mm:ss zzz",Locale.UK);
	    format.setCalendar(getGMTCalendar());
	    return format;
	}
	public static SimpleDateFormat getComplexGMTFormater() {
	    SimpleDateFormat format = new SimpleDateFormat(COMPLEX_GMT_FORMAT_PATTERN,Locale.UK);
	    format.setCalendar(getGMTCalendar());

	    return format;
	}
	
	
	public static String toGMTString(Date date) {
	    return getGMTFormater().format(date);
	}
    
    public static String toComplexGMTString(Date date) {
        return getComplexGMTFormater().format(date);
    }
	
	public static String getTodayDate(String pattern) {
		return formatDate(pattern);
	}
}
