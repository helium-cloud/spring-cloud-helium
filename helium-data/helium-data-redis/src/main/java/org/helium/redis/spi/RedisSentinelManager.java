package org.helium.redis.spi;


import com.alibaba.fastjson.JSONObject;
import org.helium.database.ConnectionString;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.redis.RedisSentinelClientImpl;
import org.helium.redis.sentinel.RedisSentinelsCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 通过配置文件加载.
 * 配置文件需要指定
 * <p>
 * 迁移代码到RedisManager 为了兼容已有业务.
 */
public class RedisSentinelManager {
    private static final Map<String, RedisSentinelClientImpl> redisMap = new HashMap<String, RedisSentinelClientImpl>();

    public static RedisSentinelManager INSTANCE = new RedisSentinelManager();

    private ConfigProvider configProvider;

    private static final String CONFIG_PATH = "redis/sentinel" + File.separator;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelManager.class);

    private RedisSentinelManager() {
        if (BeanContext.getContextService() != null) {
            configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
        }
    }
    public RedisSentinelClientImpl getRedisClient(String redisRoleName){
        if (configProvider != null){
            String content = configProvider.loadText(CONFIG_PATH + redisRoleName + ".properties");
            return getRedisClient(redisRoleName, content);
        }

        return null;

    }
    public RedisSentinelClientImpl getRedisClient(String redisRoleName, String content) {
        RedisSentinelClientImpl redisClient = redisMap.get(redisRoleName);
        if (redisClient != null) {
            return redisClient;
        }

        redisClient = getAndUpdateRedisClient(redisRoleName, content);

        synchronized (this) {
            redisMap.put(redisRoleName, redisClient);
        }

        return redisClient;
    }
    public RedisSentinelClientImpl getAndUpdateRedisClient(String redisRoleName, String content) {
        RedisSentinelClientImpl redisClient = null;
        try {
            List<RedisSentinelsCfg> redisSentinelsCfgs = new ArrayList<>();
            Properties properties = ConnectionString.fromText(content).getProperties();
            String enableValue = properties.getProperty("enabled", "1");
            if (enableValue.equals("true") || enableValue.equals("1")){
                properties.setProperty("enabled", "1");
            }
            String propJson = JSONObject.toJSONString(properties);
            RedisSentinelsCfg cfg = JSONObject.parseObject(propJson, RedisSentinelsCfg.class);
            redisSentinelsCfgs.add(cfg);
            for (RedisSentinelsCfg sentinelsCfg : redisSentinelsCfgs) {
                sentinelsCfg.setRoleName(redisRoleName);
            }
            redisClient = new RedisSentinelClientImpl(redisRoleName, redisSentinelsCfgs);
        } catch (IOException e) {
            LOGGER.error("getAndUpdateRedisClient Exception:{}", content, e);
        }

        return redisClient;
    }

}
