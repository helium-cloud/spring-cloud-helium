package org.helium.data.spark.spi;

import org.helium.data.spark.SparkClient;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Serializable;

import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SparkClien管理
 */
public class SparkClientManager {

    /**
     * 配置
     */
    private ConfigProvider configProvider;

    /**
     * SparkClient缓存
     */
    private static final Map<String, SparkClient> sparkClientMap = new ConcurrentHashMap<>();

    /**
     * SparkClient配置路径
     */
    private static final String SPARK_CONFIG_PATH = "spark" + File.separator;


    public static final SparkClientManager INSTANCE = new SparkClientManager();
    private static final Logger LOGGER = LoggerFactory.getLogger(SparkClientManager.class);

    /**
     * 初始化对象
     */
    private SparkClientManager() {
        if (BeanContext.getContextService() != null) {
            configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
        }
    }

    /**
     * 第1步：创建Spark的配置对象SparkConf，设置Spark程序的运行时的配置信息，
     * 例如说通过setMaster来设置程序要链接的Spark集群的Master的URL,如果设置
     * 为local，则代表Spark程序在本地运行 
     * @param sparkConf
     * @return
     */
    public SparkClient getSparkClient(String sparkConf) {
        SparkClient client = sparkClientMap.get(sparkConf);
        if (client != null) {
            return client;
        }
        synchronized (sparkClientMap) {
            client = sparkClientMap.get(sparkConf);
            if (client != null) {
                return client;
            }
            // Load Config
            Properties prop = configProvider.loadProperties(SPARK_CONFIG_PATH + sparkConf + ".properties");
            if (prop == null) {
                throw new IllegalArgumentException("getSparkClient config not found. path : " + SPARK_CONFIG_PATH + sparkConf + ".properties");
            }
            try {
                client = createSparkClient(prop);
                sparkClientMap.put(sparkConf, client);
                return client;
            } catch (Exception e) {
                throw new RuntimeException("getSparkClient failed.", e);
            }
        }

    }

    public SparkClient getSparkClient(String sparkConf, Properties prop) {
        // Load Config
        SparkClient sparkClient = createSparkClient(prop);
        sparkClientMap.put(sparkConf, sparkClient);
        LOGGER.info("SparkClient cache hasn't, put SparkClient into cache, conf: {}", sparkConf + ".properties");
        return sparkClient;
    }

    private synchronized SparkClient createSparkClient(Properties prop) {
        LOGGER.info("createSparkClient, config :{}", prop.toString());
        return new SparkClientImpl(prop);
    }
}

