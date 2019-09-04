package org.helium.logging.spi;

import org.helium.util.StringUtils;
import org.helium.logging.LogLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日志工具类,提供普通工具类方法
 * 
 * @author zhangyali
 */
public class LogUtils {

	public static LogLevel parseLogLevel(String levelName) {
		if (StringUtils.isNullOrEmpty(levelName)) {
			throw new IllegalArgumentException("level attr can't be null");
		}
		try {
			return LogLevel.valueOf(levelName);
		} catch (Exception ex) {
			throw new IllegalArgumentException("bad level attr:" + levelName);
		}
	}


	/**
	 * 将日期转换为指定格式的字符串
	 * 
	 * @param date
	 *            日期
	 * @return 返回字符串
	 */
	public static String formatDate(Date date) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return format.format(date);
	}

	/**
	 * 将明天日期转换为指定格式的字符串
	 * 
	 * @param strFormat
	 *            字符串
	 * @return 返回字符串
	 */
	public static String convertNextDate(String strFormat) {
		Calendar calTime = Calendar.getInstance();
		calTime.add(Calendar.DATE, 1);
		DateFormat format = new SimpleDateFormat(strFormat);
		return format.format(calTime.getTime());
	}

	/**
	 * 将当前日期转换为指定格式的字符串
	 * 
	 * @param strFormat
	 *            字符串
	 * @return 返回字符串
	 */
	public static String convertDate(String strFormat) {
		DateFormat format = new SimpleDateFormat(strFormat);
		return format.format(new Date());
	}

	/**
	 * 将日期转换为指定格式的字符串,作为日志文件的名称
	 * 
	 * @param millSec
	 *            时间毫秒数
	 * @return 返回字符串
	 */
	public static String formatDate(String path) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(path);
		// 如果path路径不是以 "/"结尾 ,则加上"/"
		if (!path.endsWith("/"))
			fileName.append("/");
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HH");
		return fileName.append("LOG_").append(format.format(new Date())).append(".log").toString();
	}

	/**
	 * 格式化具体错误信息
	 * 
	 * @param error
	 *            错误
	 * @return 返回具体错误信息
	 */
	public static String formatError(Throwable error) {
		StringBuilder message = new StringBuilder();
		Throwable curError = error;
		while (curError != null) {
			if (curError != error) {
				message.append("\n");
			}
			message.append(curError.toString());
			for (StackTraceElement stack : curError.getStackTrace()) {
				message.append("\n\tat ").append(stack);
			}
			curError = curError.getCause();
		}
		return message.toString();
	}

	/**
	 * 格式化具体错误信息
	 * 
	 * @param error
	 *            错误
	 * @return 返回具体错误信息
	 */
	public static String formatErrorNotLn(Throwable error) {
		if (error == null) {
			return "";
		}
		StringBuilder message = new StringBuilder(error.toString()).append(" ");
		for (StackTraceElement stack : error.getStackTrace())
			message.append(stack).append(" ");
		return message.toString();
	}

}
