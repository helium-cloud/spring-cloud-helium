package org.helium.kafka.logging;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.logging.LogAppender;
import org.helium.logging.LogLevel;
import org.helium.logging.spi.LogEvent;
import org.helium.logging.spi.LogUtils;
import org.helium.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
  *
  * 基于Kafka的日志输出工具
  *
  * Created by chenxuwu on 2017/8/31.
 */
public class KafkaAppender implements LogAppender {


	/** 记录日志等级 */
	private String level = "INFO";

	/**
	 * 配置
	 */
	private ConfigProvider configProvider;

	/**
	 * kafka配置路径
	 */
	private static final String KAFKA_CONFIG_PATH = "kafka" + File.separator+"KAFKA_LOG.properties";

	private String serverList;
	private String topicName;

	private static KafkaLogManager kafkaLogManager ;

	public KafkaAppender() {
		if (BeanContext.getContextService() != null) {
			configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		}
	}

	private Properties getConfigParams() {
		Properties pro = configProvider.loadProperties(KAFKA_CONFIG_PATH);
		topicName = pro.getProperty("topic");
		serverList = pro.getProperty("bootstrap.servers");
		return pro;
	}

	@Override
	public void open() {
		kafkaLogManager = new KafkaLogManager(getConfigParams(),serverList);
	}

	@Override
	public void close() {
	}

	@Override
	public boolean needQueue() {
		return true;
	}

	@Override
	public void writeLog(LogEvent event) throws IOException {
		if (StringUtils.isNullOrEmpty(topicName)) {
			throw new IllegalArgumentException("config properties not find topic, please config it");
		}

		LogLevel currentLevel = LogUtils.parseLogLevel(level.toUpperCase());
		if(!event.getLevel().canLog(currentLevel)){ return ;}
		try {
			kafkaLogManager.send(topicName, event);
		} catch (Exception e) {
		}
	}


	public String getServerList() {
		return serverList;
	}

	public void setServerList(String serverList) {
		this.serverList = serverList;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
}
