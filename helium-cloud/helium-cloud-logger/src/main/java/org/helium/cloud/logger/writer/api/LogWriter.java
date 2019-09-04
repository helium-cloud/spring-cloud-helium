package org.helium.cloud.logger.writer.api;


import org.helium.cloud.logger.model.LogMessage;

/**
 * LogWriter 顶层接口
 *
 * @author : zhangruibj@feinno.com
 * @since : 19-6-11 下午4:52
 */
public interface LogWriter {

    void log(LogMessage logMessage);

}
