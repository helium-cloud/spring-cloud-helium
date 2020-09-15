package org.helium.cloud.logger.config;

import org.helium.cloud.configcenter.autoconfig.ConfigCenterConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = LoggerConfig.PREFIX)
public class LoggerConfig {
	public static final String PREFIX = "helium.log";

	//empty or5 kafka
	private String writer = "empty";



	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}


}
