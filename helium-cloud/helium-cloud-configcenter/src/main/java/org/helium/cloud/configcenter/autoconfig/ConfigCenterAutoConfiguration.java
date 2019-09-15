package org.helium.cloud.configcenter.autoconfig;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.configcenter.ConfigCenterClient;
import org.helium.cloud.configcenter.ConfigNetHost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 */
@Configuration
@EnableConfigurationProperties(ConfigCenterConfig.class)
public class ConfigCenterAutoConfiguration {

    @Autowired
    private ConfigCenterConfig configCenterConfig;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;


    @Bean
    public ConfigCenterClient getConfiguration() {
        return new ConfigCenterClient(configCenterConfig, configurableEnvironment);
    }

    @Bean
    @ConditionalOnProperty(prefix = ConfigCenterConfig.PREFIX, value = "hosts")
    public ConfigNetHost getConfigNetHost() {
        ConfigNetHost configNetHost = new ConfigNetHost(configCenterConfig);
        configNetHost.refreshFile();
        return configNetHost;
    }

	@Bean
	public FieldSetterBeanPostProcessor cloudPropertySourceLocator(){
    	return new FieldSetterBeanPostProcessor();
	}

    @Bean
    public SpringContextUtil getSpringContextUtil(){
        return new SpringContextUtil();
    }

}
