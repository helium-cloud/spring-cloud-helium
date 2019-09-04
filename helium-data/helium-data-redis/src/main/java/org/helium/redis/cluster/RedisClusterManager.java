package org.helium.redis.cluster;

import com.alibaba.fastjson.JSONArray;
import org.helium.database.ConnectionString;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.redis.RedisClient;
import org.helium.redis.RedisClientImpl;
import org.helium.redis.RedisSentinelClientImpl;
import org.helium.redis.RedisShardedImpl;
import org.helium.redis.spi.RedisCounters;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lvmingwei on 6/2/15.
 */
public class RedisClusterManager {

    private Map<String, RedisCluster> redisMap;

    public static RedisClusterManager INSTANCE = new RedisClusterManager();

    private ConfigProvider configProvider;

    private static final String REDIS_CONFIG_PATH = "redis" + File.separator;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisClusterManager.class);

    private RedisClusterManager() {
        redisMap = new HashMap<String, RedisCluster>();
    }

    public RedisCluster getRedisClient(String redisRoleName){
        if (configProvider != null){
            String content = configProvider.loadText(REDIS_CONFIG_PATH + redisRoleName + ".json");

            return getRedisClient(redisRoleName, content);
        }

        return null;
    }
    public RedisCluster getRedisClient(String redisRoleName, String content){
        RedisCluster redisClient = redisMap.get(redisRoleName);
        if (redisClient != null) {
            return redisClient;
        }
        return getAndUpdateRedisClient(redisRoleName, content);
    }

    public RedisCluster getAndUpdateRedisClient(String redisRoleName, String content) {
        RedisCluster redisClient = null;
        try {
            List<RedisClusterItem> configs = JSONArray.parseArray(content, RedisClusterItem.class);

            for (RedisClusterItem redisClusterItem : configs) {
                redisClusterItem.setRoleName(redisRoleName);
            }
            redisClient = new RedisCluster(redisRoleName, configs);
        } catch (Exception e) {
            LOGGER.error("{}", content, e);
        }

        return redisClient;
    }

}
