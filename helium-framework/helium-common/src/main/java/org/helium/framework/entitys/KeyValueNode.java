/*
* Created by Coral on 5/15/15.
 */
package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * for xml node
 *
 * <node key="" value=""/>
 */
public class KeyValueNode extends SuperPojo {
	@Field(id = 1, name = "key", type = NodeType.ATTR)
	private String key;

	@Field(id = 2, name = "value", type = NodeType.ATTR)
	private String value;

	@Field(id = 3, name = "desc", type = NodeType.ATTR)
	private String desc;

	@Field(id = 4, name = "import", type = NodeType.ATTR)
	private String imports;

	public KeyValueNode() {
	}

	public KeyValueNode(String key, String value) {
		this.key = key;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public String getImports() {
		return imports;
	}

	public void setImports(String imports) {
		this.imports = imports;
	}
}
