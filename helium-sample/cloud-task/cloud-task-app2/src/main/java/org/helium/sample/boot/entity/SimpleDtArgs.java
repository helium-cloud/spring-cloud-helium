package org.helium.sample.boot.entity;


import org.helium.framework.task.DedicatedTaskArgs;

import java.io.Serializable;

public class SimpleDtArgs implements DedicatedTaskArgs , Serializable {

	private String user;

	@Override
	public String getTag() {
		return user;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return user;
	}
}
