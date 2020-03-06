package org.helium.ehcache.utils;

import org.helium.ehcache.EhcacheClient;
import org.helium.ehcache.imp.EhcacheServiceImpl;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 *  读取ehcache的配置文件，并生成客户端
 *  @author  wudashuai
 *  @date    2018-08-31
 */
public class EhCacheLoader implements FieldLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheLoader.class);
	/**
	 * 配置
	 */
	private ConfigProvider configProvider;

	private static final String CONFIG_PATH = "ehcache" + File.separator;

	public String getConfigPath() {
		return CONFIG_PATH;
	}

	@Override
	public Object loadField(SetterNode node) {
		try {
			String ehcacheName = node.getInnerText();
			String configFilePath = getConfigPath() + ehcacheName + ".xml";
			configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
			EhcacheServiceImpl ehcacheServiceImpl = new EhcacheServiceImpl(configProvider.loadRaw(configFilePath));
			EhcacheClient ehcacheClient = new EhcacheClient(ehcacheServiceImpl);
			return ehcacheClient;
		} catch (Exception e) {
			LOGGER.error("Ehcache loadField is error!",e);
			e.printStackTrace();
		}
		return null;
	}

}