package org.helium.framework.entitys.dashboard;


import org.helium.superpojo.SuperPojo;

/**
 * Created by Coral on 7/28/15.
 */
public class BeanJson extends SuperPojo {
	private String bundle;

	private String id;

	private String type;

	private String state;
	
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
