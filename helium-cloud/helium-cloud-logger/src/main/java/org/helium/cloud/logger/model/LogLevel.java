package org.helium.cloud.logger.model;

/**
 * 日志级别枚举
 *
 * @author : zhangruibj@feinno.com
 * @since : 19-6-11 下午7:43
 */
public enum LogLevel {

    DEBUG("debug", 0), INFO("info", 1), WARN("warn", 2), ERROR("error", 3);

    public String name;
    public int level;

    LogLevel(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public static LogLevel formLevel(String level) {
        level = level.toLowerCase();
        switch (level) {
            case "error":
                return LogLevel.ERROR;
            case "warn":
                return LogLevel.WARN;
            case "info":
                return LogLevel.INFO;
            case "debug":
                return LogLevel.DEBUG;
        }
        return LogLevel.ERROR;
    }
}
