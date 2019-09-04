package org.helium.logging.spi;

import org.helium.logging.LogLevel;
import org.slf4j.Marker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志事件的实体类, 记录日志的具体信息
 * 暂时没有通过Rpc传输的需求，如果有，会考虑换个类的，所以这里就做成一个普普通通的类就好了
 * 
 * Created by Coral
 */
public class LogEvent /* extends SuperPojo implements Serializable */ {
	Date time;
	String timeString;
	String loggerName;
	LogLevel level;
	String message;
	Throwable error;
	Marker marker;
	long threadId;
	String threadName;
	LogOutput output;

	public LogEvent(String loggerName, LogLevel level, String message, Throwable error, Marker marker, LogOutput output) {
		this.loggerName = loggerName;
		this.level = level;
		this.message = message;
		this.error = error;
		this.marker = marker;
		this.output = output;

		this.time = new Date();
		this.timeString = SIMPLE_TIME_FORMAT.format(this.time);

		Thread currentThread = Thread.currentThread();
		this.threadName = currentThread.getName();
		this.threadId = currentThread.getId();
	}

	public Date getTime() {
		return time;
	}

	public String getTimeString() {
		return timeString;
	}

	public String getLoggerName() {
		return loggerName;
	}

	public LogLevel getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public Throwable getError() {
		return error;
	}

	public Marker getMarker() {
		return marker;
	}

	public long getThreadId() {
		return threadId;
	}

	public String getThreadName() {
		return threadName;
	}

	private static final DateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private static final DateFormat SIMPLE_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS");
}