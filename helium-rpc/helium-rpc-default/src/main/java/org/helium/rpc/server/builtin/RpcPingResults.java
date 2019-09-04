/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Aug 4, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.server.builtin;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * {在这里补充类的功能说明}
 * <p>
 * Created by Coral
 */
public class RpcPingResults extends SuperPojo {
	@Field(id = 1)
	private String serverName;

	@Field(id = 2)
	public static String serviceName;

	@Field(id = 3)
	private String[] services;

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		RpcPingResults.serviceName = serviceName;
	}

	public String[] getServices() {
		return services;
	}

	public void setServices(String[] services) {
		this.services = services;
	}
}
