package org.helium.cloud.logger.model;

import java.util.Date;
import java.util.Map;

/**
 * 日志消息类
 *
 * @author : zhangruibj@feinno.com
 * @since : 19-6-11 下午4:38
 */
public class LogMessage {

    /**
     * 主键ID
     */
    private long id;

    /**
     * 成功标识.如果为false,则一定会打印日志
     */
    private boolean success;

    /**
     * 日志级别
     */
    private LogLevel logLevel;

    /**
     * 执行失败之后的错误消息,执行成功为空
     */
    private String message;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 日志目标class名称,例如在LogController类的某个方法上使用了打印日志的注解,则该名称为LogController的全类名
     */
    private String className;

    /**
     * 方法名,打印日志注解所标识的方法
     */
    private String methodName;

    /**
     * 方法参数,key为参数名称,值为参数值
     */
    private Map<String, Object> methodParams;

    /**
     * 执行结果,如果执行失败,则为空
     */
    private Object methodResult;

    /**
     * 创建时间
     */
    private Date createTime;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Map<String, Object> getMethodParams() {
        return methodParams;
    }

    public void setMethodParams(Map<String, Object> methodParams) {
        this.methodParams = methodParams;
    }

    public Object getMethodResult() {
        return methodResult;
    }

    public void setMethodResult(Object methodResult) {
        this.methodResult = methodResult;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
