package org.helium.framework.route.center.entity;


import org.helium.superpojo.SuperPojo;

/**
 * Created by Coral on 8/5/15.
 */
public class DummyNode extends SuperPojo {
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
