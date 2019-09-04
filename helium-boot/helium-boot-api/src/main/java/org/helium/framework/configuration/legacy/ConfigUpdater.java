package org.helium.framework.configuration.legacy;

import java.util.Date;

public abstract class ConfigUpdater<E> {
	private String path;
	private String params;
	private ConfigType type;
	private Date version;

	public Date getVersion() {
		return version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}

	public String getPath() {
		return path;
	}

	public ConfigType getType() {
		return type;
	}

	public String getParams() {
		return params;
	}

	public ConfigUpdater(String path, ConfigType type) {
		this.path = path;
		this.type = type;
	}

	public ConfigUpdater(String path, ConfigType type, String params) {
		this.path = path;
		this.type = type;
		this.params = params;
	}

	@Override
	public String toString() {
		return String.format("ConfigUpdater<%1$s> %2$s.%3$s", type, path);
	}

	public abstract void update() throws Exception;
}
