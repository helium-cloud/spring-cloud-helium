/************************************************************************************
 * <p>Copyright: Copyright (c) 2016</p>
 * <p> 公司：新媒传信</p>
 * <p> 部门：和飞信产品事业.平台技术团队.1X平台项目团队</p>
 *
 * @version V6
 ************************************************************************************/
package org.helium.http.logging;

/**
 * <p>Title: LogUtils</p>
 * <p>Description: </p>
 * @author : zhaodongyw email:zhaodongyw@feinno.com
 * @date : 2017/7/26
 * @version : 1.0
 */
public class LogUtils {

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
}
