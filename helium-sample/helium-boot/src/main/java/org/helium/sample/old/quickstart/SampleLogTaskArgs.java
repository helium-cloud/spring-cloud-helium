package org.helium.sample.bootstrap.quickstart;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by Coral on 6/27/17.
 */
public class SampleLogTaskArgs extends SuperPojo {
	@Field(id = 1)
	private String clientIp;
	
	@Field(id = 2)
	private String action;
	
	@Field(id = 3)
	private SampleUser user;

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public SampleUser getUser() {
		return user;
	}

	public void setUser(SampleUser user) {
		this.user = user;
	}
}
