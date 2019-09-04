/*
 * FAE, Feinno App Engine
 *  
 * Create by lichunlei 2010-11-26
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package zconfig.configuration.args;

/**
 * 配置异常公共类, 必须显示进行catch
 * 
 * @author lichunlei
 */
public class ConfigurationException extends Exception {
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	static final long serialVersionUID = 1L;

	public ConfigurationException() {
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(String message, Exception ex) {
		super(message, ex);
	}
}
