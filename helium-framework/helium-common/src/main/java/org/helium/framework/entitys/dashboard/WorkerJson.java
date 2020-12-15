package org.helium.framework.entitys.dashboard;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 7/28/15.
 */
public class WorkerJson extends SuperPojo {
	@Field(id = 1, name = "bundleName", type = NodeType.NODE)
	private String bundleName;

	@Field(id = 2, name = "serverEndpoints", type = NodeType.NODE)
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
