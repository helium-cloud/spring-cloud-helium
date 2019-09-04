package zconfig.configuration.args;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

public class HAWorkerRegisterArgs extends SuperPojo
{
	@Field(id = 1)
	private String serviceName;

	@Field(id = 2)
	private String serverName;

	@Field(id = 3)
	private int workerPid;

	@Field(id = 4)
	private String servicePorts;

	@Field(id = 5)
	private int deploymentId;

	public String getServiceName()
	{
		return serviceName;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public int getWorkerPid()
	{
		return workerPid;
	}

	public void setWorkerPid(int workerPid)
	{
		this.workerPid = workerPid;
	}

	public String getServicePorts()
	{
		return servicePorts;
	}

	public void setServicePorts(String servicePorts)
	{
		this.servicePorts = servicePorts;
	}

	public int getDeploymentId()
	{
		return deploymentId;
	}

	public void setDeploymentId(int deploymentId)
	{
		this.deploymentId = deploymentId;
	}
}
