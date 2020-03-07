package org.helium.plugin.mybatis.loader.impl;


import org.helium.plugin.mybatis.BusinessException;
import org.helium.plugin.mybatis.model.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

@Component
@ConditionalOnExpression("'${spring.profiles.active}'.equals('local') || '${spring.profiles.active}'.equals('dev')")
public class LocalConfigLoader extends AbstractConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfigLoader.class);

    @Resource
    private Environment environment;

    @Override
    protected ConfigModel doLoad(String key, String group) {
        String configKey = getConfigKey(key, group);
        String value = environment.getProperty(configKey, String.class);
        String active = environment.getProperty("spring.profiles.active", String.class);
        if (StringUtils.isEmpty(value)) {
            //throw new RuntimeException()Exc("当前配置环境为[{}], 配置[{}]不存在!", active, configKey);
        }
        Properties config = new Properties();
        ClassPathResource resource = new ClassPathResource(value);
        InputStream inputStream = null;
        try {
            LOGGER.info("当前配置环境为[{}]，开始加载[{}]的配置[{}]...", active, group, key);
            inputStream = resource.getInputStream();
            config.load(inputStream);
            LOGGER.info("[{}]的配置[{}]加载完成！", group, key);
            return new ConfigModel(active, "local", config, key, group);
        } catch (Exception e) {
            throw new BusinessException("当前配置环境为[{}], 加载的配置为本地, 配置文件[{}]不存在! 请检查配置[{}]是否正确!", active, value, configKey);
        } finally {
            if (Objects.nonNull(inputStream)) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    protected boolean needCache() {
        return false;
    }
}
