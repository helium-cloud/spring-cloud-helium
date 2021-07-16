package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

/**
 * 对Key的依赖类型
 * Created by Coral on 7/4/15.
 */
public class ConfigImportNode extends SuperPojo {
	private String type;

	private String key;

	private String defaultValue;

	private String desc;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
}
