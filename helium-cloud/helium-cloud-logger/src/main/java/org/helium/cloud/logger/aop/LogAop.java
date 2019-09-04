package org.helium.cloud.logger.aop;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.helium.cloud.logger.annotation.SystemLog;
import org.helium.cloud.logger.model.LogLevel;
import org.helium.cloud.logger.model.LogMessage;
import org.helium.cloud.logger.writer.api.LogWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author : zhangruibj@feinno.com
 * @since : 19-6-11 下午4:32
 */
@Aspect
public class LogAop {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogAop.class);

    @Resource
    private LogWriter logWriter;

    /**
     * 拦截点  -->  标注了LogAppender的方法
     */
    @Pointcut("@annotation(org.helium.cloud.logger.annotation.SystemLog)")
    public void logPoint() {

    }

    /**
     * 切面代码
     *
     * @param point 拦截点
     * @return 方法执行结果
     */
    @Around("logPoint()")
    public Object saveLog(ProceedingJoinPoint point) {
        // 创建日志消息类
        LogMessage logMessage = new LogMessage();
        // 准备日志数据是否成功标志
        boolean prepareLogSuccess = true;
        try {
            // 获取目标类的class
            Class<?> currentClass = point.getTarget().getClass();
            MethodSignature signature = (MethodSignature) point.getSignature();
            // 获取方法
            Method method = signature.getMethod();
            // 获取注解
            SystemLog systemLog = method.getAnnotation(SystemLog.class);
            // 获取注解上的日志级别与业务类型
            LogLevel logLevel = systemLog.logLevel();
            String businessType = systemLog.businessType();
            // 获取参数名称
            String[] parameterNames = signature.getParameterNames();
            // 获取参数值
            Object[] parameterValues = point.getArgs();
            // 参数封装
            logMessage.setLogLevel(logLevel);
            logMessage.setClassName(currentClass.getName());
            logMessage.setMethodName(method.getName());
            logMessage.setBusinessType(businessType);
            // 方法参数组装
            if (Objects.nonNull(parameterNames) && Objects.nonNull(parameterValues)) {
                int paramSum = Math.min(parameterNames.length, parameterValues.length);
                Map<String, Object> methodParams = new LinkedHashMap<>(8);
                for (int i = 0; i < paramSum; i++) {
                    methodParams.put(parameterNames[i], parameterValues[i]);
                }
                logMessage.setMethodParams(methodParams);
            }
            // 创建日志输出类
        } catch (Throwable e) {
            prepareLogSuccess = false;
            LOGGER.error(e.getMessage(), e);
        }
        // 方法执行结果
        Object result = null;
        // 切面方法是否执行成功标识
        boolean success = true;
        // 切面方法执行异常消息
        String message = "";
        try {
            // 执行切面方法
            result = point.proceed();
        } catch (Throwable e) {
            // 如果抛出异常,则设置
            success = false;
            message = e.getMessage();
        } finally {
            if (prepareLogSuccess) {
                logMessage.setSuccess(success);
                logMessage.setMethodResult(result);
                logMessage.setMessage(message);
                try {
                     logWriter.log(logMessage);
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }
}

