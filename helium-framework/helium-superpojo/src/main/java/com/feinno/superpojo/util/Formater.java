package com.feinno.superpojo.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <b>描述: </b>格式化工具类，目前仅提供了出错异常的格式化，待扩展
 * <p>
 * <b>功能: </b>格式化工具类，目前仅提供了出错异常的格式化
 * <p>
 * <b>用法: </b>静态方法，正常调用即可
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class Formater {

	public static final DateFormat DATA_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static DateFormat createDateFormat() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 格式化具体错误信息
	 * 
	 * @param error
	 *            错误
	 * @return 返回具体错误信息
	 */
	public static String formaError(Throwable error) {
		StringBuilder message = new StringBuilder();
		Throwable curError = error;
		while (curError != null) {
			if (curError != error) {
				message.append("\n");
			}
			message.append(curError.toString());
			for (StackTraceElement stack : curError.getStackTrace()) {
				message.append("\n\t").append(stack);
			}
			curError = curError.getCause();
		}
		return message.toString();
	}
}
