package org.helium.cloud.regsitercenter.configruation.registry;

import org.apache.dubbo.config.AbstractConfig;

import java.util.Map;

/**
 * 类描述：TODO
 *
 * @author zkailiang
 * @date 2020/4/22
 */
public class ConfigUtil extends AbstractConfig {

	public static void appendParameters(Map<String, String> parameters, Object config) {
		AbstractConfig.appendParameters(parameters, config);
	}
}
