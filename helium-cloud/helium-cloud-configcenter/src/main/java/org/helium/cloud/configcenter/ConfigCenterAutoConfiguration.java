package org.helium.cloud.configcenter;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @Auther: Mr.Jing
 * @Date: 2019-01-25 11:21
 * @Description:
 */
@Configuration
@EnableConfigurationProperties(ConfigCenterProperties.class)
public class ConfigCenterAutoConfiguration {

    @Autowired
    private ConfigCenterProperties cinMessageProperties;

    @Autowired
    private ConfigurableEnvironment configurableEnvironment;


    @Bean
    public ConfigCenterClient getConfiguration() {
        return new ConfigCenterClient(cinMessageProperties, configurableEnvironment);
    }

    @Bean
    @ConditionalOnProperty(prefix = "cincloud.configcenter", value = "hosts")
    public ConfigNetHost getConfigNetHost() {
        ConfigNetHost configNetHost = new ConfigNetHost(cinMessageProperties);
        configNetHost.refreshFile();
        return configNetHost;
    }
    @Bean
    public SpringContextUtil getSpringContextUtil(){
        return new SpringContextUtil();
    }

}
