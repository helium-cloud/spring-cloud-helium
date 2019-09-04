package org.helium.logging;


import org.helium.logging.spi.LogEvent;

import java.io.IOException;

/**
 * Log的通用接口
 * 
 * @author coral
 * @version 创建时间：2014年9月17日
 */
public interface LogAppender {
	/**
	 * 启动
	 */
	void open();

	/**
	 * 关闭
	 */
	void close();

	/**
	 * 支持同步输出
	 * @return
	 */
	boolean needQueue();

	/**
	 * 写入
	 * @param event
	 */
	void writeLog(LogEvent event) throws IOException;
}
