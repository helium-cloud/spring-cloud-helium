package org.helium.framework.entitys.dashboard;


import org.helium.superpojo.SuperPojo;

/**
 * Created by Coral on 7/28/15.
 */
public class WorkerJson extends SuperPojo {
	private String bundleName;

	private String serverEndpoints;

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getServerEndpoints() {
		return serverEndpoints;
	}

	public void setServerEndpoints(String serverEndpoints) {
		this.serverEndpoints = serverEndpoints;
	}
}
