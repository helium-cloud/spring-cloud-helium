package org.helium.framework.configuration.legacy.intf;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

public class HAWorkerHeartbeatArgs extends SuperPojo
{
	@Field(id = 1)
	private String status;

	@Field(id = 2)
	private String statusEx;

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getStatusEx()
	{
		return statusEx;
	}

	public void setStatusEx(String statusEx)
	{
		this.statusEx = statusEx;
	}
}
