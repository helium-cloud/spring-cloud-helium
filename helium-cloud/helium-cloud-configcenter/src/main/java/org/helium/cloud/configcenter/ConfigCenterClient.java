package org.helium.cloud.configcenter;


import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.config.Environment;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.configcenter.ConfigChangeEvent;
import org.apache.dubbo.configcenter.ConfigurationListener;
import org.apache.dubbo.configcenter.DynamicConfiguration;
import org.apache.dubbo.configcenter.DynamicConfigurationFactory;
import org.helium.cloud.common.api.CloudConstant;
import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.configcenter.autoconfig.ConfigCenterConfig;
import org.helium.cloud.configcenter.cache.ConfigCenterLocal;
import org.helium.cloud.configcenter.utils.KeyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * ConfigCenterClient 自定义客户端
 */
public class ConfigCenterClient {
    private static final Logger logger = LoggerFactory.getLogger(ConfigCenterClient.class);
    /**
     * Default placeholder prefix: {@value}.
     */
    public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

    /**
     * Default placeholder suffix: {@value}.
     */
    public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

    /**
     * Default value separator: {@value}.
     */
    public static final String DEFAULT_VALUE_SEPARATOR = ":";


    /**
     * Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX}.
     */
    protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

    /**
     * Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX}.
     */
    protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;
    //本地缓存配置
    private ConfigCenterLocal configCenterLocal = new ConfigCenterLocal();

    //启动配置
    private ConfigCenterConfig configCenterConfig;

    //远程动态配置
    private DynamicConfiguration dynamicConfiguration;

    //springboot原生配置
    private ConfigurableEnvironment configurableEnvironment;

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigCenterClient.class);

    public ConfigCenterClient(ConfigCenterConfig configCenterProperties, ConfigurableEnvironment configurableEnvironment) {
        configCenterConfig = configCenterProperties;
        //1.是否启用远程动态配置
        if (configCenterConfig.isEnable()) {
            URL url = URL.valueOf(configCenterConfig.getUrl());
            DynamicConfigurationFactory factories = ExtensionLoader
                    .getExtensionLoader(DynamicConfigurationFactory.class)
                    .getExtension(url.getProtocol());
            DynamicConfiguration configuration = factories.getDynamicConfiguration(url);
            Environment.getInstance().setDynamicConfiguration(configuration);
            dynamicConfiguration = configuration;
        }
        //2.自定义配置文件加载此处不启用
        configCenterLocal.loadConfig(configCenterConfig.getFile());
        //3.定时缓存至本机local
        configCenterLocal.timerCheck(configCenterConfig.getFile() + ".tmp");
        this.configurableEnvironment = configurableEnvironment;

    }

    public void addListener(String key, ConfigurationListener listener) {
        addListener(key, CloudConstant.GROUP, listener);
    }


    public void removeListener(String key, ConfigurationListener listener) {
        removeListener(key, CloudConstant.GROUP, listener);
    }


    public void addListener(String key, String group, ConfigurationListener listener) {
        if (dynamicConfiguration == null) {
            return;
        }
        String indexKey = KeyUtils.getKey(key, group);
        dynamicConfiguration.addListener(key, group, new ConfigurationListener() {
            @Override
            public void process(ConfigChangeEvent event) {
                //内部缓存
                //通知业务侧
                if (listener != null) {
                    listener.process(event);
                }
                switch (event.getChangeType()) {
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
            if (configurableEnvironment != null) {
                String ceValue = configurableEnvironment.getProperty(indexKey);
                if (!StringUtils.isEmpty(ceValue)) {
                    value = ceValue;
                }
            }
            //3.远程加载配置
            if (StringUtils.isEmpty(value) && dynamicConfiguration != null) {
                String dyValue = dynamicConfiguration.getProperties(key, group, 0L);
                if (!StringUtils.isEmpty(dyValue)) {
                    value = dyValue;
                    configCenterLocal.putConfig(indexKey, dyValue);
                }
                //设置为null 业务不需要处理，组件层拦截消息通知
                addListener(key, group, null);
            }
            //4. 加载本机local值获取
            if (!StringUtils.isEmpty(value) && value.startsWith(CloudConstant.LOCAL)) {
                String readLocalValue = readLocalValue(value);
                //5. 替换二级变量${}
                if (Objects.nonNull(readLocalValue) && readLocalValue.contains(placeholderPrefix)) {
                    readLocalValue = parseStringValue(readLocalValue);
                }
                return readLocalValue;
            }
            return value;
        } catch (Exception e) {
            LOGGER.error("getConfig exception:{}", indexKey, e);
        } finally {

        }
        return null;
    }

    private String parseStringValue(String strVal) {
        StringBuffer buf = new StringBuffer(strVal);
        //提取出${}中间的字符串，
        int startIndex = strVal.indexOf(placeholderPrefix);
        while (startIndex != -1) {
            int endIndex = buf.toString().indexOf(this.placeholderSuffix, startIndex + this.placeholderPrefix.length());
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                //用System.getEnv和外部的properties文件替代了${}中间的值
                String propVal = readLocalValue(Objects.requireNonNull(configurableEnvironment.getProperty(placeholder)));
                buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal == null ? "null" : propVal);
                if (logger.isDebugEnabled()) {
                    logger.debug("Resolved placeholder '" + placeholder + "' to value [" + propVal + "]");
                }
                startIndex = buf.toString().indexOf(this.placeholderPrefix, startIndex + propVal.length());
            } else {
                startIndex = -1;
            }
        }
        return buf.toString();
    }

    /**
     * 获取本地属性配置
     *
     * @param keyValue
     * @return
     */
    private String readLocalValue(String keyValue) {
        try {
            String path = keyValue.replace(CloudConstant.LOCAL, "");
            DefaultResourceLoader defaultResourceLoader = new DefaultResourceLoader();
            Resource resource = defaultResourceLoader.getResource(path);
            //换成文件流读取,resource.getFile() is not working in docker. Need to use getInputStream()
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String str = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            return str;
        } catch (IOException e) {
            LOGGER.error("key: {} getValue: {}", keyValue, e);
        }
        return null;
    }


    public ConfigurableEnvironment getConfigurableEnvironment() {
        return configurableEnvironment;
    }

    public void setConfigurableEnvironment(ConfigurableEnvironment configurableEnvironment) {
        this.configurableEnvironment = configurableEnvironment;
    }

    public static ConfigCenterClient getInstance() {
        ConfigCenterClient configCenterClient = SpringContextUtil.getBean(ConfigCenterClient.class);
        return configCenterClient;
    }
}
