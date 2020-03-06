package org.helium.framework.spring.autoconfigure;

import org.helium.framework.spring.annotation.EnableHeliumConfiguration;
import org.helium.framework.spring.annotation.processor.ServiceSetterBeanPostProcessor;
import org.helium.framework.spring.assembly.HeliumAssembly;
import org.helium.framework.spring.assembly.HeliumConfig;
import org.helium.framework.spring.health.HeliumHealthIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(annotation = EnableHeliumConfiguration.class)
//开启属性注入,通过@autowired注入
@EnableConfigurationProperties(HeliumConfig.class)
public class HeliumAutoConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumAutoConfiguration.class);

	@Autowired
	private HeliumConfig heliumConfig;

	@Bean
	public HeliumHealthIndicator heliumHealthIndicator() {
		assemblyHelium();
		return new HeliumHealthIndicator();
	}

	@Bean
	public ServiceSetterBeanPostProcessor serviceSetterBeanPostProcessor() {
		return new ServiceSetterBeanPostProcessor();
	}


	@Bean
	public HeliumAutoWired heliumAutoWired() {
		return new HeliumAutoWired();
	}

	private void assemblyHelium() {
		try {
			synchronized (this) {
				LOGGER.info("prepare init helium:{}", heliumConfig.getBootFile());
				if (HeliumAssembly.INSTANCE.isStarted()) {
					LOGGER.info("helium already started:{}", heliumConfig.getBootFile());
					return;
				}
				if (heliumConfig.isXmlEnable()) {
					String pathArray[] = heliumConfig.getBootFile().split(";");
					for (String path : pathArray) {
						HeliumAssembly.INSTANCE.addPath(path);
					}
					HeliumAssembly.INSTANCE.run(heliumConfig.getBootFile(), false);
				} else {
					HeliumAssembly.INSTANCE.run(heliumConfig, false);
				}


			}

		} catch (Exception e) {
			LOGGER.error(" init helium exception:", e);
		}

	}

}
