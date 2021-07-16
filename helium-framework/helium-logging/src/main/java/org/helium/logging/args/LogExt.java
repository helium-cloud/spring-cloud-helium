package org.helium.logging.args;


import org.helium.superpojo.SuperPojo;

public class LogExt extends SuperPojo {

    private String key = "key";

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
