package org.helium.plugin.mybatis.loader;


import org.helium.plugin.mybatis.model.ConfigModel;

public interface ConfigLoader {

    ConfigModel load(String key, String group);

    void remove(String key, String group);

    void clear();

    void refresh(String key, String group, ConfigModel config);
}
