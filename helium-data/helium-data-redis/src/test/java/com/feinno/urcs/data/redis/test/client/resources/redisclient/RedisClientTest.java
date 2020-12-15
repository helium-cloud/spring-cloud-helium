package com.feinno.urcs.data.redis.test.client.resources.redisclient;


import com.feinno.superpojo.util.FileUtil;
import org.helium.redis.RedisClient;
import org.helium.redis.RedisSentinelClientImpl;
import org.helium.redis.cluster.RedisCluster;
import org.helium.redis.cluster.RedisClusterManager;
import org.helium.redis.cluster.RedisSentinelCluster;
import org.helium.redis.cluster.RedisSentinelClusterManager;
import org.helium.redis.spi.RedisManager;
import org.helium.redis.spi.RedisSentinelManager;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by Coral on 5/5/15.
 */
public class RedisClientTest {
    @Test
    public void testRedisSentinelCluster() throws IOException {
        String path = "src/test/java/com/feinno/urcs/data/redis/test/client/resources/redis/sentinel/UTEST_RDS.json";
        String str = FileUtil.read(path);
        RedisSentinelCluster redisClient = RedisSentinelClusterManager.INSTANCE.getRedisClient("UTEST_RDS", str);
        System.out.println("testRedisSentinelCluster set: " + redisClient.getClient().set("testRedisSentinelCluster","testRedisSentinelCluster"));
        System.out.println("testRedisSentinelCluster get: " + redisClient.getClient().get("testRedisSentinelCluster"));
    }


    @Test
    public void testRedisSentinel() throws IOException {
        String path = "src/test/java/com/feinno/urcs/data/redis/test/client/resources/redis/sentinel/UTEST_RDS.properties";
        String str = FileUtil.read(path);
        RedisSentinelClientImpl redisSentinelClient = RedisSentinelManager.INSTANCE.getRedisClient("UTEST_RDS", str);
        System.out.println("testRedisSentinel set: " + redisSentinelClient.set("testRedisSentinel","testRedisSentinel"));
        System.out.println("testRedisSentinel get: " + redisSentinelClient.get("testRedisSentinel"));
    }


    @Test
    public void testRedisCluster() throws IOException {
        String path = "src/test/java/com/feinno/urcs/data/redis/test/client/resources/redis/UTEST_RD.json";
        String str = FileUtil.read(path);
        RedisCluster redisCluster = RedisClusterManager.INSTANCE.getRedisClient("UTEST_RD", str);
        System.out.println("RedisCluster set: " + redisCluster.getSharding("testRedisCluster").set("testRedisCluster","testRedisCluster"));
        System.out.println("RedisCluster get: " + redisCluster.getSharding("testRedisCluster").get("testRedisCluster"));
    }


    @Test
    public void testRedisClient() throws IOException {
        String path = "src/test/java/com/feinno/urcs/data/redis/test/client/resources/redis/UTEST_RD.properties";
        String str = FileUtil.read(path);
        RedisClient redisClient = RedisManager.INSTANCE.getRedisClient("UTEST_RD", str);
        System.out.println("RedisClient set: " + redisClient.set("testRedisClient","testRedisClient"));
        System.out.println("RedisClient get: " + redisClient.get("testRedisClient"));
    }

}
