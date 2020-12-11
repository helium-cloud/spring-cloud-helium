package org.helium.cloud.regsitercenter.configruation;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.common.utils.UrlUtils;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.registry.Registry;
import org.apache.dubbo.registry.RegistryFactory;
import org.helium.cloud.regsitercenter.configruation.registry.ConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.dubbo.common.constants.CommonConstants.*;

/**
 * 类描述：AutoRegisterCenterConfig
 *
 * @author zkailiang
 * @date 2020/4/21
 */
@Import({HeliumRegister.class})
@EnableConfigurationProperties(RegisterAppConfig.class)
@Configuration
public class AutoRegisterCenterConfig {

	@Autowired
	private RegisterAppConfig registerAppConfig;

	@Bean("heliumRegistryFactory")
	public RegistryFactory registryFactory() {
		return ExtensionLoader.getExtensionLoader(RegistryFactory.class).getExtension(registerAppConfig.getProtocol());
	}

	@Bean("heliumRegistry")
	public Registry register(@Autowired @Qualifier("heliumRegistryFactory") RegistryFactory registryFactory) {
		return registryFactory.getRegistry(register2Url(registerAppConfig));
	}

	public static URL register2Url(RegistryConfig config) {
		String address = config.getAddress();
		if (StringUtils.isEmpty(address)) {
			address = ANYHOST_VALUE;
		}
		Map<String, String> map = new HashMap();
		ConfigUtil.appendParameters(map, config);
		if (!map.containsKey(PROTOCOL_KEY)) {
			map.put(PROTOCOL_KEY, DUBBO_PROTOCOL);
		}

		List<URL> urls = UrlUtils.parseURLs(address, map);
		return urls != null ? urls.get(0) : null;
	}
}
