package org.helium.redis.widgets.redis.client;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yibo on 2017-2-10.
 */


public class RedisSentinelManager2 {

    private Map<String, RedisSentinelClient2> redisMap;
    public static RedisSentinelManager2 INSTANCE = new RedisSentinelManager2();

    private RedisSentinelManager2() {
        redisMap = new HashMap<String, RedisSentinelClient2>();

    }

    public RedisSentinelClient2 getRedisClient(String redisRoleName, List<CFG_RedisSentinels> configs) {
        RedisSentinelClient2 redisClient = redisMap.get(redisRoleName);

        if (redisClient != null) {
            return redisClient;
        }


        redisClient = new RedisSentinelClient2(redisRoleName, configs);

        synchronized (this) {
            redisMap.put(redisRoleName, redisClient);
        }


        return redisClient;
    }
}