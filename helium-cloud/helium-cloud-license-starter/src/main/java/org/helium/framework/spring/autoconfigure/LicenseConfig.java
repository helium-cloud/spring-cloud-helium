package org.helium.framework.spring.autoconfigure;

import org.helium.framework.entitys.BootstrapConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = LicenseConfig.PREFIX)
public class LicenseConfig extends BootstrapConfiguration {
	public static final String PREFIX = "license";
	private long expire = 60000;
	private String key = "djadiKJdj49dFJLd";

	public long getExpire() {
		return expire;
	}

	public void setExpire(long expire) {
		this.expire = expire;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
}
