package org.helium.framework.spring.assembly;

import org.helium.framework.entitys.BootstrapConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = HeliumConfig.PREFIX)
public class HeliumConfig extends BootstrapConfiguration {
    public static final String PREFIX = "helium";
	private boolean xmlEnable = false;
	private String bootFile = "helium.xml";
	private String bootPath = "";


	public String getBootFile() {
		return bootFile;
	}

	public void setBootFile(String bootFile) {
		this.bootFile = bootFile;
	}

	public String getBootPath() {
		return bootPath;
	}

	public void setBootPath(String bootPath) {
		this.bootPath = bootPath;
	}

	public boolean isXmlEnable() {
		return xmlEnable;
	}

	public void setXmlEnable(boolean xmlEnable) {
		this.xmlEnable = xmlEnable;
	}
}
