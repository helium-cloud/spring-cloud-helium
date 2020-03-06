package org.helium.framework.utils;

import org.helium.framework.entitys.BootstrapConfiguration;
import org.helium.framework.entitys.KeyValueNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class EnvUtils {
	public static String REG_IP = "REG_IP";
	private static HashMap<String, KeyValueNode> hashMap = null;
	private static Object object = new Object();

	private static final Logger LOGGER = LoggerFactory.getLogger(EnvUtils.class);
	public static String getEnv(BootstrapConfiguration configuration, String name) {
		String value = null;
		try {
			if (hashMap == null){
				synchronized (object){
					hashMap = new HashMap<>();
					for (KeyValueNode keyValueNode: configuration.getEnvironments()){
						hashMap.put(keyValueNode.getKey(), keyValueNode);
					}
				}
			}
			KeyValueNode keyValueNode = hashMap.get(name);
			if (keyValueNode != null){
				value = keyValueNode.getValue();
			} else {
				value = System.getenv(name);
			}

		} catch (Exception e){
			LOGGER.error("getEnv:{}", name, e);
		}
		LOGGER.info("EnvUtils:getEnv:{}-{}", name, value);
		return value;
	}
}
