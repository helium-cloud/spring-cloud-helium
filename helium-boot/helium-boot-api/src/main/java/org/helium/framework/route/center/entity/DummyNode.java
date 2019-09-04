package org.helium.framework.route.center.entity;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by Coral on 8/5/15.
 */
public class DummyNode extends SuperPojo {
	@Field(id = 1)
	private String creator;

	public DummyNode() {
	}

	public DummyNode(String creator) {
		this.creator = creator;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}
}
