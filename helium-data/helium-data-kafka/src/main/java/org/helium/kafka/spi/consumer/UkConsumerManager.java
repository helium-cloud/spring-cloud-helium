package org.helium.kafka.spi.consumer;//package com.feinno.urcs.support.services.uek.kafka.spi.consumer;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.kafka.UkConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * kafka消费者管理
 */
public class UkConsumerManager {

	/**
	 * 配置
	 */
	private ConfigProvider configProvider;
	/**
	 * kafka配置路径
	 */
	private static final String KAFKA_CONFIG_PATH = "kafka" + File.separator;
	/**
	 * consumer配置
	 */
	private static final Map<String, UkConsumer> consumers = new ConcurrentHashMap<>();

	public static final UkConsumerManager INSTANCE = new UkConsumerManager();

	private static final Logger LOGGER = LoggerFactory.getLogger(UkConsumerManager.class);

	/**
	 * 初始化对象
	 */
	private UkConsumerManager() {
		if (BeanContext.getContextService() != null) {
			configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		}
	}
	/**
	 *  采用config-provider
	 *
	 * @param kafkaConf
	 * @return
	 */
	public UkConsumer getKafkaConsumer(String kafkaConf) {

		try {
            String content = configProvider.loadText(KAFKA_CONFIG_PATH + kafkaConf + ".properties");
			return getKafkaConsumer(kafkaConf, content);
		} catch (Exception e) {
			LOGGER.error("getKafkaConsumer Exception:{}", kafkaConf, e);
		}
		return null;

	}
	/**
	 * 获取指定kafka配置下的消费者配置
	 *
	 * @param kafkaConf
	 * @return
	 */
	public UkConsumer getKafkaConsumer(String kafkaConf, String content) {

		try {
			UkConsumer consumer = consumers.get(kafkaConf);
			if (consumer != null) {
				LOGGER.info("consumers cache has not found, conf: {}", kafkaConf);
				return consumer;
			}
			return getKafkaConsumer(kafkaConf, content);
		} catch (Exception e) {
			LOGGER.error("getKafkaConsumer Exception:{}", kafkaConf, e);
		}
		return null;

	}

    /**
     * 获取指定kafka配置下的消费者配置
     *
     * @param kafkaConf
     * @return
     */
    public UkConsumer getAndUpdateKafkaConsumer(String kafkaConf, String content) {

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(content.getBytes());
            Properties prop = new Properties();
            prop.load(inputStream);
            return getKafkaConsumer(kafkaConf, prop);
        } catch (Exception e) {
            LOGGER.error("getKafkaConsumer Exception:{}", kafkaConf, e);
        }
        return null;

    }

	/**
	 * 获取kafka消费者
	 * @param kafkaConf
	 * @param prop
	 * @return
	 */
    public UkConsumer getKafkaConsumer(String kafkaConf, Properties prop) {
		// 环境变量添加，需要输入配置文件的路径
		// 从相对路径拿取/kafka/kafka_client_jaas.conf
		if (prop.getProperty("java.security.auth.login.config") !=  null){
			System.setProperty("java.security.auth.login.config", prop.getProperty("java.security.auth.login.config"));
		}
		UkConsumer ukConsumer = new UkConsumerImpl(kafkaConf, prop);
		consumers.put(kafkaConf, ukConsumer);
		return ukConsumer;
	}

}

