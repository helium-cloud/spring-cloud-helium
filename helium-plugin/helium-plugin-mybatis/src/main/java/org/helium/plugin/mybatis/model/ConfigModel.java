package org.helium.plugin.mybatis.model;

import java.util.Properties;

public class ConfigModel {

    /**
     * 当前配置的环境
     */
    private final String active;

    /**
     * 加载配置的地址
     */
    private final String position;

    /**
     * 具体配置
     */
    private final Properties data;

    /**
     * 配置类型
     */
    private final String key;

    /**
     * 配置类型
     */
    private final String group;


    public ConfigModel(String active, String position, Properties data, String key, String group) {
        this.active = active;
        this.position = position;
        this.data = data;
        this.key = key;
        this.group = group;
    }

    public String getActive() {
        return active;
    }

    public String getPosition() {
        return position;
    }

    public Properties getData() {
        return data;
    }

    public String getKey() {
        return key;
    }

    public String getGroup() {
        return group;
    }
}
