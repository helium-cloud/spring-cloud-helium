package org.helium.cloud.logger.writer.impl;


import org.helium.cloud.logger.model.LogLevel;
import org.helium.cloud.logger.model.LogMessage;
import org.helium.cloud.logger.writer.api.LogWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 日志输出基本类,支持扩展
 *
 * @author : zhangruibj@feinno.com
 * @since : 19-6-11 下午6:08
 */
@Component
public abstract class LogWriterAdapter implements LogWriter, InitializingBean {

    /**
     * 环境里配置的日志级别
     */
    @Value("${spring.common.log-level:error}")
    private String envLevel;

    private LogLevel envLogLevel;


    @Override
    public void afterPropertiesSet() {
        this.envLogLevel = LogLevel.formLevel(envLevel);
    }

    @Override
    public void log(LogMessage logMessage) {
        // 如果执行失败了,则无论标识的是什么级别,输出日志
        if (!logMessage.isSuccess()) {
            doLog(logMessage);
            return;
        }
        LogLevel logLevel = logMessage.getLogLevel();
        // 如果日志级别为空,则return
        if (Objects.isNull(logLevel)) {
            return;
        }
        // 如果配置环境里面的日志级别低于当前方法标注的级别,则进行打印
        // 原因是:
        // 1.开发环境日志级别通常设置为debug,某个方法上标注的级别为debug,会打印日志,方便调试
        // 2.线上环境日志级别通常设置为error,如果方法上标记的debug,则不打印,目的是为了提高性能
        if (envLogLevel.level <= logMessage.getLogLevel().level) {
            doLog(logMessage);
        }
    }

    /**
     * 子类实现,具体保存到哪
     *
     * @param logMessage 消息
     */
    protected abstract void doLog(LogMessage logMessage);

}
