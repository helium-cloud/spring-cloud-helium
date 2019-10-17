package org.helium.data.sharding.configuration;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 7/15/16.
 */
public class DataSourceNode extends SuperPojo {
	@Field(id = 1, name = "id", type = NodeType.ATTR)
	private int id;

	@Field(id = 2, name = "name", type = NodeType.ATTR)
	private String name;

	/**
	 * 根据key设置成真正想要的value
	 */
	@Field(id = 3, name = "value", type = NodeType.ATTR)
	private String value;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
