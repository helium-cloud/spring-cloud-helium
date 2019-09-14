package org.helium.cloud.configcenter.autoconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Auther: coral
 * @Date: 2019-05-31
 * @Description: 配置中心
 */
@Component
@ConfigurationProperties(prefix = "cincloud.configcenter", ignoreUnknownFields = false)
public class ConfigCenterProperties {

    private String url = "zookeeper://127.0.0.1:7998";

    private String file = "cincloud.properties";

    private boolean enable = false;

    private String hosts = "";

    private String encs = "123456";


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public String getEncs() {
        return encs;
    }
}
