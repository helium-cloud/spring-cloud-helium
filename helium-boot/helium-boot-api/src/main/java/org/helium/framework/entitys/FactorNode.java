package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 8/4/15.
 */
public class FactorNode extends SuperPojo {
	@Field(id = 1, name = "key", type = NodeType.ATTR)
	private String key;

	@Field(id = 2, name = "operator", type = NodeType.ATTR)
	private String operator;

	@Field(id = 3, name = "value", type = NodeType.ATTR)
	private String value;

	@Field(id = 4, name = "value2", type = NodeType.ATTR)
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
