package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * 对Key的依赖类型
 * Created by Coral on 7/4/15.
 */
public class ConfigImportNode extends SuperPojo {
	@Field(id = 1, name = "type", type = NodeType.ATTR)
	private String type;

	@Field(id = 2, name = "key", type = NodeType.ATTR)
	private String key;

	@Field(id = 3, name = "default", type = NodeType.ATTR)
	private String defaultValue;

	@Field(id = 4, name = "desc", type = NodeType.ATTR)
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
