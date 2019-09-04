package org.helium.cloud.logger.annotation;


import org.helium.cloud.logger.model.LogLevel;

import java.lang.annotation.*;

/**
 * 自动写日志注解
 * 用法,标注到需要写日志的对应方法上
 * <p>
 *
 * @author : zhangruibj@feinno.com
 * @LogAppender 注解标识
 * public Message getMessage(String messageId){
 * doSomething();...
 * return message;
 * }
 * <p>
 * 写出日志的格式详情请看com.allstar.omc.log.LogMessage
 * </p>
 * @since : 19-6-11 下午4:21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SystemLog {


    /**
     * 日志级别
     *
     * @return 日志级别, 默认为INFO
     */
    LogLevel logLevel() default LogLevel.INFO;

    /**
     * 业务类型
     *
     * @return 业务类型
     */
    String businessType();

}
