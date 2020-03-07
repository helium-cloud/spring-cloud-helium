package org.helium.plugin.mybatis.loader.impl;

import org.helium.plugin.mybatis.loader.ConfigLoader;
import org.helium.plugin.mybatis.model.ConfigModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractConfigLoader implements ConfigLoader {

    private static final Map<String, ConfigModel> CONFIG_CACHE = new ConcurrentHashMap<>();

    @Override
    public ConfigModel load(String key, String group) {
        final String configKey = key + group;
        ConfigModel config = CONFIG_CACHE.get(configKey);
        if (Objects.isNull(config)) {
            config = doLoad(key, group);
            if (needCache()){
                CONFIG_CACHE.put(getConfigKey(key, group), config);
            }
        }
        return config;
    }

    @Override
    public void remove(String key, String group) {
        CONFIG_CACHE.remove(getConfigKey(key, group));
    }

    @Override
    public void clear() {
        CONFIG_CACHE.clear();
    }

    @Override
    public void refresh(String key, String group, ConfigModel config) {
        CONFIG_CACHE.put(getConfigKey(key, group), config);
    }

    protected abstract ConfigModel doLoad(String key, String group);

    protected abstract boolean needCache();

    String getConfigKey(String key, String group) {
        return group + "." + key;
    }
}
