package org.helium.uek.es.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.uek.es.EsClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * es客户端管理
 */
public class EsClientManager {

	/**
	 * 配置
	 */
	private ConfigProvider configProvider;

	/**
	 * es客户端缓存
	 */
	private static final Map<String, EsClient> esClientMap = new ConcurrentHashMap<>();
	/**
	 * es配置路径
	 */
	private static final String ES_CONFIG_PATH = "es" + File.separator;

	public static final EsClientManager INSTANCE = new EsClientManager();
	private static final Logger LOGGER = LoggerFactory.getLogger(EsClientManager.class);

	/**
	 * 初始化对象
	 */
	private EsClientManager() {
		if (BeanContext.getContextService() != null) {
			configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		}
	}

	/**
	 * 获取指定es配置下的esclient生产代理
	 *
	 * @param esClientConf
	 * @return
	 */
	public EsClient getEsClient(String esClientConf) {
		EsClient esClient = esClientMap.get(esClientConf);
		if (esClient != null) {
			LOGGER.info("EsClient cache has, conf: {}", esClientConf + ".properties");
			return esClient;
		}
		Properties prop = configProvider.loadProperties(ES_CONFIG_PATH + esClientConf + ".properties");
		if (null == prop) {
			throw new IllegalArgumentException("esClient config not found. path : " + ES_CONFIG_PATH + esClientConf + ".properties");
		}
		String keystore_filepath = prop.getProperty("searchguard.ssl.transport.keystore_filepath");
		if (keystore_filepath != null) {
			prop.setProperty("searchguard.ssl.transport.keystore_filepath", configProvider.getAbsolutePath(ES_CONFIG_PATH + keystore_filepath));
		}
		String truststore_filepath = prop.getProperty("searchguard.ssl.transport.truststore_filepath");
		if (truststore_filepath != null) {
			prop.setProperty("searchguard.ssl.transport.truststore_filepath", configProvider.getAbsolutePath(ES_CONFIG_PATH + truststore_filepath));
		}
		return getEsClient(esClientConf, prop);
	}

	public EsClient getEsClient(String esClientConf, String content){
		Properties prop = new Properties();
		try {
			prop.load(new ByteArrayInputStream(content.getBytes()));
		} catch (Exception e) {
			LOGGER.info("getEsClient for Content.{},{}", esClientConf, content);
		}
		return getEsClient(esClientConf, prop);
	}
	public EsClient getEsClient(String esClientConf, Properties prop) {
		// Load Config
		EsClient esClient = createEsClient(prop);
		esClientMap.put(esClientConf, esClient);
		LOGGER.warn("esClient cache hasn't, put esClient into cache, conf: {}", esClient + ".properties");
		return esClient;
	}

	private synchronized EsClient createEsClient(Properties prop) {
		LOGGER.info("create EsClient, config :{}", prop.toString());
		return new EsClientImpl(prop);
	}
}

