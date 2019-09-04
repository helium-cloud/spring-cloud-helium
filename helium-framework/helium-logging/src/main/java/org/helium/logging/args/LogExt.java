package org.helium.logging.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

public class LogExt extends SuperPojo {
	@Field(id = 1, name = "key", type = NodeType.NODE)
    private String key = "key";

    @Field(id = 2, name = "value", type = NodeType.NODE)
    private String value = "value";

    public static LogExt create(String key, String value){
    	return new LogExt(key, value);
	}

	public LogExt() {
	}

	public LogExt(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
