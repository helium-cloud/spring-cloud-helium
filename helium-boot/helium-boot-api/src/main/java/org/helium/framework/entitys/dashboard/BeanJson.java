package org.helium.framework.entitys.dashboard;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 7/28/15.
 */
public class BeanJson extends SuperPojo {
	@Field(id = 1, name = "bundle", type = NodeType.ATTR)
	private String bundle;

	@Field(id = 2, name = "id", type = NodeType.ATTR)
	private String id;

	@Field(id = 3, name = "type", type = NodeType.ATTR)
	private String type;

	@Field(id = 4, name = "state", type = NodeType.ATTR)
	private String state;

	@Field(id = 5, name = "serviceUrls", type = NodeType.ATTR)
	private String serviceUrls;

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

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

	public String getServiceUrls() {
		return serviceUrls;
	}

	public void setServiceUrls(String serviceUrls) {
		this.serviceUrls = serviceUrls;
	}
}
