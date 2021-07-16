package org.helium.logging;


import org.helium.superpojo.type.EnumInteger;

/**
 * 记录日志的级别
 *
 * Created by Coral
 */
public enum LogLevel implements EnumInteger {
	ALL(0, "[ ALL ]"),
	TRACE(10000, "[TRACE]"),
	DEBUG(20000, "[DEBUG]"),
	INFO(30000, "[INFO] "),
	WARN(50000, "[WARN] "),
	ERROR(80000, "[ERROR]"),
	OFF(Integer.MAX_VALUE,  "[ OFF ]")
	;

	private int value;
	private String format;

	private LogLevel(int value, String format) {
		this.value = value;
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	/**
	 *
	 * @param currentLevel 当前的配置等级
	 * @return
	 */
	public boolean canLog(LogLevel currentLevel) {
		return this.value >= currentLevel.value;
	}

	/**
	 * 为每一个枚举值设置对应的int值
	 */
	@Override
	public int intValue() {
		return value;
	}
}
