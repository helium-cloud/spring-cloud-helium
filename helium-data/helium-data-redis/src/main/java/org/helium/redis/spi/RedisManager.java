package org.helium.redis.spi;

import org.helium.database.ConnectionString;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.redis.RedisClient;
import org.helium.redis.RedisClientImpl;
import org.helium.redis.RedisSentinelClientImpl;
import org.helium.redis.RedisShardedImpl;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lvmingwei on 6/2/15.
 */
public class RedisManager {

    private Map<String, RedisClient> redisMap;

    public static RedisManager INSTANCE = new RedisManager();

    private ConfigProvider configProvider;

    private static final String REDIS_CONFIG_PATH = "redis" + File.separator;

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisManager.class);

    private RedisManager() {
        redisMap = new HashMap<String, RedisClient>();
    }

    public RedisClient getRedisClient(String redisRoleName){
        if (configProvider != null){
            String content = configProvider.loadText(REDIS_CONFIG_PATH + redisRoleName + ".properties");
            return getRedisClient(redisRoleName, content);
        }

        return null;
    }
    public RedisClient getRedisClient(String redisRoleName, String content){
        RedisClient redisClient = redisMap.get(redisRoleName);
        if (redisClient != null) {
            return redisClient;
        }
        return getAndUpdateRedisClient(redisRoleName, content);
    }

    public RedisClient getAndUpdateRedisClient(String redisRoleName, String content) {
        RedisCounters counters = PerformanceCounterFactory.getCounters(RedisCounters.class, redisRoleName);
        RedisClient redisClient = null;
        ConnectionString connStr = null;
        try {
            connStr = ConnectionString.fromText(content);
            if ("true".equals(connStr.getProperty("sentinelModel"))) {
                redisClient = new RedisSentinelClientImpl(redisRoleName, connStr.getProperties(), counters);
                synchronized (this) {
                    redisMap.put(redisRoleName, redisClient);
                }
                return redisClient;
            } else {
                String serverNumber = connStr.getProperty("serverNumber");
                if (!StringUtils.isNullOrEmpty(serverNumber) && Integer.parseInt(serverNumber) > 1) {
                    redisClient = new RedisShardedImpl(connStr.getProperties(), counters);
                } else {
                    redisClient = new RedisClientImpl(connStr.getProperties(), counters);
                }
                synchronized (this) {
                    redisMap.put(redisRoleName, redisClient);
                }
            }

        } catch (IOException e) {
            LOGGER.error("getAndUpdateRedisClient.{}", content, e);
        }

        return redisClient;
    }

}
