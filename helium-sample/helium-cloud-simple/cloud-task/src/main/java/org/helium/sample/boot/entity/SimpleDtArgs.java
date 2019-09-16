package org.helium.sample.boot.entity;

import org.helium.cloud.task.api.DedicatedTaskArgs;

public class SimpleDtArgs implements DedicatedTaskArgs {

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
}