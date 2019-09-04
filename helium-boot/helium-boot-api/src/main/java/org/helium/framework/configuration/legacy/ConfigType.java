package org.helium.framework.configuration.legacy;

import com.feinno.superpojo.type.EnumInteger;

/**
 * HA模式下支持的配置类型
 * 
 * @author lichunlei
 */
public enum ConfigType implements EnumInteger
{
	UNKOWN(0),
	
	/** 文本, 可以是xml或者其他文本  */
	TEXT(1),
	
	/** 表 */
	TABLE(2);

	private int value;
	
	ConfigType(int type)
	{
		value = type;
	}
	
	@Override
	public int intValue() {
		return value;
	}
}

