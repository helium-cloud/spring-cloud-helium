package org.helium.logging;

/**
 * Created by Coral on 8/27/15.
 */
public interface LoggerManager {
	/**
	 * 应用配置
	 * @param configuration
	 */
	void applyConfiguration(LoggingConfiguration configuration);

	/**
	 * 应用子配置
	 * @param configuration
	 */
	void applyChildConfiguration(LoggingConfiguration configuration);
}
