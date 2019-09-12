package org.helium.cloud.task.autoconfigure;

import org.helium.framework.entitys.BootstrapConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = HeliumConfig.PREFIX)
public class HeliumConfig extends BootstrapConfiguration {
    public static final String PREFIX = "helium.beans";
	private String task = "";
	private String service = "";

	public static String getPREFIX() {
		return PREFIX;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}
}
