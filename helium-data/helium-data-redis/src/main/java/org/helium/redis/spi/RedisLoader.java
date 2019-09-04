package org.helium.redis.spi;


import com.alibaba.fastjson.JSONObject;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.entitys.SetterNodeLoadType;
import org.helium.redis.RedisClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lvmingwei on 6/2/15.
 */
public class RedisLoader implements FieldLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisLoader.class);
    @Override
    public Object loadField(SetterNode node) {
		RedisClient redisClient = null;
		try {
			String configName = node.getInnerText();
			String configStr = node.getValue();
			SetterNodeLoadType loadType = node.getLoadType();
			switch (loadType) {
				//配置中心或者value加载
				case CONFIG_VALUE:
					redisClient = RedisManager.INSTANCE.getRedisClient(configName, configStr);
					break;
				//动态加载
				case CONFIG_DYNAMIC:
					redisClient = RedisManager.INSTANCE.getAndUpdateRedisClient(configName, configStr);
					break;
				//helium加载
				case CONFIG_PROVIDE:
				case UNKNOWN:
					redisClient = RedisManager.INSTANCE.getRedisClient(configName);
					break;
				default:
					redisClient = RedisManager.INSTANCE.getRedisClient(configName);
					break;
			}
		} catch (Exception e) {
			LOGGER.error("loadField.{},", JSONObject.toJSONString(node), e);
		}
		return redisClient;
    }


}
