package org.helium.redis.cluster;


import com.alibaba.fastjson.JSONObject;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.redis.RedisSentinelClientImpl;
import org.helium.redis.sentinel.RedisSentinelsCfg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过配置文件加载.
 * 配置文件需要指定
 * <p>
 * 迁移代码到RedisManager 为了兼容已有业务.
 */
public class RedisSentinelClusterManager {
    private static final Map<String, RedisSentinelCluster> redisMap = new HashMap<String, RedisSentinelCluster>();

    public static RedisSentinelClusterManager INSTANCE = new RedisSentinelClusterManager();

    private ConfigProvider configProvider;

    private static final String CONFIG_PATH = "redis/sentinel" + File.separator;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelClusterManager.class);

    private RedisSentinelClusterManager() {
        if (BeanContext.getContextService() != null) {
            configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
        }
    }
    public RedisSentinelCluster getRedisClient(String redisRoleName){
        if (configProvider != null){
            String content = configProvider.loadText(CONFIG_PATH + redisRoleName + ".json");
            return getRedisClient(redisRoleName, content);
        }

        return null;

    }
    public RedisSentinelCluster getRedisClient(String redisRoleName, String content) {
        //
        RedisSentinelCluster redisClient = redisMap.get(redisRoleName);
        if (redisClient != null) {
            return redisClient;
        }

        redisClient = getAndUpdateRedisClient(redisRoleName, content);

        synchronized (this) {
            redisMap.put(redisRoleName, redisClient);
        }

        return redisClient;
    }
    public RedisSentinelCluster getAndUpdateRedisClient(String redisRoleName, String content) {
        RedisSentinelCluster redisClient = null;
        try {
            List<RedisSentinelsCfg> redisSentinelsCfgs = JSONObject.parseArray(content, RedisSentinelsCfg.class);
            for (RedisSentinelsCfg sentinelsCfg : redisSentinelsCfgs) {
                sentinelsCfg.setRoleName(redisRoleName);
            }
            redisClient = new RedisSentinelCluster(new RedisSentinelClientImpl(redisRoleName, redisSentinelsCfgs));
        } catch (Exception e) {
            LOGGER.error("getAndUpdateRedisClient Exception:{}", content, e);
        }

        return redisClient;
    }

}
