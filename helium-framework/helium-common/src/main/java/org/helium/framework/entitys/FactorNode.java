package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

/**
 * Created by Coral on 8/4/15.
 */
public class FactorNode extends SuperPojo {

	private String key;


	private String operator;


	private String value;

	private String value2;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue2() {
		return value2;
	}

	public void setValue2(String value2) {
		this.value2 = value2;
	}
}
