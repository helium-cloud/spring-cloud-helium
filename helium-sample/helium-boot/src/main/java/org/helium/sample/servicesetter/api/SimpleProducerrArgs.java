package org.helium.sample.servicesetter.api;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

public class SimpleProducerrArgs extends SuperPojo {
	@Field(id = 1)
	private String mobile;
	@Field(id = 2)
	private String type;
	@Field(id = 3)
	private int priority;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}
}
