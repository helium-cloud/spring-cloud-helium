/*
* Created by Coral on 5/15/15.
 */
package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

/**
 * for xml node
 *
 * <node key="" value=""/>
 */
public class KeyValueNode extends SuperPojo {

	private String key;


	private String value;


	private String desc;


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
