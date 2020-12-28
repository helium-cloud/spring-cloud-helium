package org.helium.framework.spring.autoconfigure;

import org.helium.framework.spring.annotation.EnableLicenseConfiguration;
import org.helium.framework.spring.controller.LicenseController;
import org.helium.framework.spring.service.LicService;
import org.helium.framework.spring.service.LicServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(annotation = EnableLicenseConfiguration.class)
@EnableConfigurationProperties(LicenseConfig.class)
public class LicenseAutoConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(LicenseAutoConfiguration.class);

	@Bean
	public LicService licService() {
		return new LicServiceImpl();
	}
	@Bean
	public LicenseConfig licenseConfig() {
		return new LicenseConfig();
	}
	@Bean
	public LicenseController licenseController() {
		return new LicenseController();
	}


}
