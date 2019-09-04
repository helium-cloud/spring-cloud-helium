package org.helium.cloud.logger.writer.impl;

import com.alibaba.fastjson.JSON;

import org.helium.cloud.logger.model.LogMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志输出到文件
 *
 * @author : zhangruibj@feinno.com
 * @since : 19-6-12 上午10:39
 */
public class FileLogWriter extends LogWriterAdapter {

    private Logger logger = LoggerFactory.getLogger(FileLogWriter.class);

    @Override
    protected void doLog(LogMessage logMessage) {
        // 如果执行失败,则直接无视是什么级别,直接输出
        if (!logMessage.isSuccess()) {
            logger.error(JSON.toJSONString(logMessage));
            return;
        }
        // 日志打印输出
        switch (logMessage.getLogLevel()) {
            case DEBUG:
                logger.debug(JSON.toJSONString(logMessage));
                break;
            case INFO:
                logger.info(JSON.toJSONString(logMessage));
                break;
            case WARN:
                logger.warn(JSON.toJSONString(logMessage));
                break;
            case ERROR:
                logger.error(JSON.toJSONString(logMessage));
                break;
        }
    }
}
