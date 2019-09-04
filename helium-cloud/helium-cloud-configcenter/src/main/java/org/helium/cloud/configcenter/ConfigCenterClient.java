package org.helium.cloud.configcenter;


import com.feinno.superpojo.util.FileUtil;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.configcenter.ConfigChangeEvent;
import org.apache.dubbo.configcenter.ConfigurationListener;
import org.apache.dubbo.configcenter.DynamicConfiguration;
import org.apache.dubbo.configcenter.DynamicConfigurationFactory;
import org.helium.cloud.common.api.CloudConstant;
import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.configcenter.cache.ConfigCenterLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * ConfigCenterClient 自定义客户端
 *
 */
public class ConfigCenterClient{

    //本地缓存配置
    private ConfigCenterLocal configCenterLocal = new ConfigCenterLocal();

    //启动配置
    private ConfigCenterProperties properties;

    //远程动态配置
    private DynamicConfiguration dynamicConfiguration;

    //springboot原生配置
    private ConfigurableEnvironment configurableEnvironment;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCenterClient.class);

    public ConfigCenterClient(ConfigCenterProperties configCenterProperties, ConfigurableEnvironment configurableEnvironment) {
        properties = configCenterProperties;
        //1.是否启用远程动态配置
        if (properties.isEnable()) {
            URL url = URL.valueOf(properties.getUrl());
            DynamicConfigurationFactory factories = ExtensionLoader
                    .getExtensionLoader(DynamicConfigurationFactory.class)
                    .getExtension(url.getProtocol());
            DynamicConfiguration configuration = factories.getDynamicConfiguration(url);
            Environment.getInstance().setDynamicConfiguration(configuration);
            dynamicConfiguration = configuration;
        }
        //2.自定义配置文件加载此处不启用
        configCenterLocal.loadConfig(properties.getFile());
        //3.定时缓存至本机local
        configCenterLocal.timerCheck(properties.getFile() + ".tmp");
        this.configurableEnvironment = configurableEnvironment;

    }

    public void addListener(String key, ConfigurationListener listener) {
        addListener(key, CloudConstant.GROUP, listener);
    }


    public void removeListener(String key, ConfigurationListener listener) {
        removeListener(key, CloudConstant.GROUP, listener);
    }


    public void addListener(String key, String group, ConfigurationListener listener) {
        if (dynamicConfiguration == null){
            return;
        }
        String indexKey = KeyUtils.getKey(key, group);
        dynamicConfiguration.addListener(key, group, new ConfigurationListener() {
            @Override
            public void process(ConfigChangeEvent event) {
                //内部缓存
                //通知业务侧
                if (listener != null){
                    listener.process(event);
                }
                switch (event.getChangeType()){
                    case ADDED:
                    case MODIFIED:
                        configCenterLocal.putConfig(indexKey, event.getValue());
                        break;
                    case DELETED:
                        configCenterLocal.deleteConfig(indexKey);
                        break;
                    default:
                        break;
                }

            }
        });
    }


    public void removeListener(String key, String group, ConfigurationListener listener) {
        dynamicConfiguration.removeListener(key, group, new ConfigurationListener() {
            @Override
            public void process(ConfigChangeEvent event) {
                //内部缓存
                String indexKey = KeyUtils.getKey(key, group);
                //通知业务侧
                listener.process(event);
                configCenterLocal.deleteConfig(indexKey);
            }
        });
    }


    public String get(String key) {
        return get(key, CloudConstant.GROUP);
    }

    public String get(String key, String group) {
        String indexKey = KeyUtils.getKey(key, group);
        try {
            //1.cincloud.properties加载配置
            //内部缓存
            String value = configCenterLocal.getConfig(indexKey);
            //2.spring-boot-application加载配置
            if (configurableEnvironment != null){
                String ceValue = configurableEnvironment.getProperty(indexKey);
                if (!StringUtils.isEmpty(ceValue)) {
                    value = ceValue;
                }
            }
            //3.远程加载配置
            if (StringUtils.isEmpty(value) && dynamicConfiguration != null){
                String  dyValue = dynamicConfiguration.getConfig(key, group,0L);
                if (!StringUtils.isEmpty(dyValue)) {
                    value = dyValue;
                    configCenterLocal.putConfig(indexKey, dyValue);
                }
                //设置为null 业务不需要处理，组件层拦截消息通知
                addListener(key, group, null);
            }
            //4. 加载本机local值获取
            if (!StringUtils.isEmpty(value) && value.startsWith(CloudConstant.LOCAL)){
                return readLocalValue(value);
            }
            return value;
        } catch (Exception e) {
            LOGGER.error("getConfig exception:{}", indexKey, e);
        } finally {

        }
        return null;
    }


    /**
     * 获取本地属性配置
     * @param keyValue
     * @return
     */
    private String readLocalValue(String keyValue) {
        try {
            String path = keyValue.replace(CloudConstant.LOCAL, "");
            DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
            File file = defaultResourceLoader.getResource(path).getFile();
            String str = FileUtil.read(file.getAbsolutePath());
            return str;
        } catch (IOException e) {
            LOGGER.error("key: getValue", keyValue,e);
        }
        return null;
    }


    public ConfigurableEnvironment getConfigurableEnvironment() {
        return configurableEnvironment;
    }

    public void setConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    public static ConfigCenterClient getInstance(){
        ConfigCenterClient configCenterClient =  SpringContextUtil.getBean(ConfigCenterClient.class);
        return configCenterClient;
    }
}
