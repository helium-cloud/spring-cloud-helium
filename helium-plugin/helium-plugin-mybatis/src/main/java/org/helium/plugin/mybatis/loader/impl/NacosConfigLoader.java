package org.helium.plugin.mybatis.loader.impl;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.configcenter.DynamicConfiguration;
import org.apache.dubbo.configcenter.DynamicConfigurationFactory;
import org.helium.plugin.mybatis.model.ConfigModel;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Component
@ConditionalOnExpression("'${spring.profiles.active}'.equals('prod')")
public class NacosConfigLoader extends AbstractConfigLoader implements InitializingBean {

    private DynamicConfiguration configuration;

    @Value("${cincloud.configcenter.url}")
    private String position;

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    protected ConfigModel doLoad(String key, String group) {
        Properties data = new Properties();
        String config = configuration.getProperties(key, group);
        try {
            data.load(new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ConfigModel(active, position, data, key, group);
    }

    @Override
    protected boolean needCache() {
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        URL url = URL.valueOf(position);
        DynamicConfigurationFactory factories = ExtensionLoader.getExtensionLoader(DynamicConfigurationFactory.class).getExtension(url.getProtocol());
        configuration = factories.getDynamicConfiguration(url);
        Environment.getInstance().setDynamicConfiguration(configuration);
    }
}
