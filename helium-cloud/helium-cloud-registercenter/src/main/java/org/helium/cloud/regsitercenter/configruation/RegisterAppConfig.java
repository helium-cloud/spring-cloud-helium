package org.helium.cloud.regsitercenter.configruation;

import org.apache.dubbo.config.RegistryConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 类描述：RegisterAppConfig
 *
 * @author zkailiang
 * @date 2020/4/21
 */
@ConfigurationProperties(prefix = "helium.registry")
public class RegisterAppConfig extends RegistryConfig {
	private static final String DEFAULT_PROTOCOL = "direct";
	private boolean needRegistry = false;
	private boolean needSubscribe = false;

	public String getProtocol() {
		return super.getProtocol() == null ? DEFAULT_PROTOCOL :
				super.getProtocol();
	}

	public boolean isNeedRegistry() {
		return needRegistry;
	}

	public void setNeedRegistry(boolean needRegistry) {
		this.needRegistry = needRegistry;
	}

	public boolean isNeedSubscribe() {
		return needSubscribe;
	}

	public void setNeedSubscribe(boolean needSubscribe) {
		this.needSubscribe = needSubscribe;
	}
}
