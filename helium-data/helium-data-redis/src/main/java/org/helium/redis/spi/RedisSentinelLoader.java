package org.helium.redis.spi;


import com.alibaba.fastjson.JSONObject;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.helium.redis.RedisSentinelClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
public class RedisSentinelLoader implements FieldLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelLoader.class);

	@Override
	public Object loadField(SetterNode node) {
		RedisSentinelClientImpl redisSentinelClient = null;
		try {
			String configName = node.getInnerText();
			String configStr = node.getValue();
			SetterNodeLoadType loadType = node.getLoadType();
			switch (loadType) {
				//配置中心或者value加载
				case CONFIG_VALUE:
					redisSentinelClient = RedisSentinelManager.INSTANCE.getRedisClient(configName, configStr);
					break;
				//动态加载
				case CONFIG_DYNAMIC:
					redisSentinelClient = RedisSentinelManager.INSTANCE.getAndUpdateRedisClient(configName, configStr);
					break;
				//helium加载
				case CONFIG_PROVIDE:
				case UNKNOWN:
					redisSentinelClient = RedisSentinelManager.INSTANCE.getRedisClient(configName);
					break;
				default:
					redisSentinelClient = RedisSentinelManager.INSTANCE.getRedisClient(configName);
					break;
			}
		} catch (Exception e) {
			LOGGER.error("loadField.{},", JSONObject.toJSONString(node), e);
		}
		return redisSentinelClient;
	}



}