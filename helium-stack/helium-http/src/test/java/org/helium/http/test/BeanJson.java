package org.helium.http.test;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 7/28/15.
 */
public class BeanJson extends SuperPojo {
	@Field(id = 1, name = "id", type = NodeType.ATTR)
	private String id;

	@Field(id = 2, name = "type", type = NodeType.ATTR)
	private String type;

	@Field(id = 3, name = "state", type = NodeType.ATTR)
	private String state;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}
