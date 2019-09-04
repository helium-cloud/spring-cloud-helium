/*
 * FAE, Feinno App Engine
 *  
 * Create by lichunlei 2010-11-26
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package zconfig.configuration.args;

/**
 * 未找到配置的异常
 * 
 * @author lichunlei
 */
public class ConfigurationNotFoundException extends ConfigurationException
{
	static final long serialVersionUID = 1L;

	private ConfigType type = ConfigType.UNKOWN;
	private String path;
	private String key;

	public ConfigurationNotFoundException(String path)
	{
		super(String.format("ConfigurationNotFound %1$s", path), null);
		this.type = ConfigType.TEXT;
		this.path = path;
	}

	public ConfigurationNotFoundException(ConfigType type, String path)
	{
		super(String.format("ConfigurationNotFound<%1$s>: %2$s", type, path), null);
		this.type = type;
		this.path = path;
	}

	public ConfigurationNotFoundException(ConfigType type, String path, String key)
	{
		super(String.format("ConfigurationNotFound<%1$s>: %2$s.%3$s", type, path, key), null);
		this.type = type;
		this.path = path;
		this.key = key;
	}
	
	public ConfigType getConfigType()
	{
		return type;
	}
	
	public String getPath()
	{
		return path;
	}
	
	public String getKey()
	{
		return key;
	}
}
