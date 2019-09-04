package org.helium.framework.configuration.legacy;

public class ConfigurationFailedException extends ConfigurationException
{
	private static final long serialVersionUID = 8636671796268376690L;

	public ConfigurationFailedException(ConfigType type, String path, Exception e)
	{
		super(String.format("ConfigFailed<%1$s>, %2$s", type, path), e);
	}
}
