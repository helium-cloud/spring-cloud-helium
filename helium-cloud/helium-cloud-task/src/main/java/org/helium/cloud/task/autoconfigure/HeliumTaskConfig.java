package org.helium.cloud.task.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = HeliumTaskConfig.PREFIX)
public class HeliumTaskConfig {
    public static final String PREFIX = "helium.task";

	private String bean = "";

	public static String getPREFIX() {
		return PREFIX;
	}


	public String getBean() {
		return bean;
	}

	public void setBean(String bean) {
		this.bean = bean;
	}
}
