package com.feinno.urcs.data.redis.test.client;

import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.test.ServiceForTest;
import org.helium.redis.RedisClient;
import org.helium.redis.cluster.RedisCluster;
import org.helium.redis.cluster.RedisClusterLoader;
import org.helium.redis.cluster.RedisSentinelCluster;
import org.helium.redis.cluster.RedisSentinelClusterLoader;
import org.helium.redis.spi.RedisLoader;
import org.helium.redis.spi.RedisSentinelLoader;

import java.util.List;

/**
 * Created by Leon on 8/3/16.
 */
@ServiceImplementation
public class RedisClusterTester implements ServiceForTest {
    @FieldSetter(value = "UTEST_RD", loader = RedisLoader.class)
    private RedisClient redisClient;

    @FieldSetter(value = "UTEST_RDS", loader = RedisSentinelLoader.class)
    private RedisClient redisClientSentinel;

    @FieldSetter(value = "UTEST_RD", loader = RedisClusterLoader.class)
    private RedisCluster cluster;

    @FieldSetter(value = "UTEST_RDS", loader = RedisSentinelClusterLoader.class)
    private RedisSentinelCluster sentinelCluster;

    @Override
    public void test() throws Exception {
        RedisClient client2 = sentinelCluster.getClient();
        for (int i = 0; i < 5; i++) {
            client2.set("keyeyeyey", "keyeyeyey");
            System.out.println(client2.get("keyeyeyey"));
        }


    }

    void listTest() {
        String key = "testKey:";
        for (int i = 0; i < 100; i++) {

            redisClient.lpush(key, key + i);
        }

        for (int i = 0 ; i < 20; i++) {
            System.out.println("push key");
            List<String> stringList = redisClient.lrange(key, redisClient.llen(key) - (redisClient.llen(key) - 10), redisClient.llen(key));
            for (String item : stringList) {
                System.out.println(item);
            }
            redisClient.ltrim(key, redisClient.llen(key) - (redisClient.llen(key) - 10), redisClient.llen(key));

        }

    }
    void simpleTest2() throws InterruptedException {

        redisClientSentinel.set("testkeys", "testkeys");
        System.out.println(redisClientSentinel.get("testkeys"));
    }
    void simpleTest() throws InterruptedException {
        RedisClient client2 = sentinelCluster.getClient();
        for (int i = 0; i < 5; i++) {
            Thread aa = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            System.out.println(client2.get("keyeyeyey"));
                            Thread.sleep(100);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }
            });
            aa.start();
        }


        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(500);
                byte key[] = "keyeyeyey".getBytes();
                byte value[] = "222".getBytes();
                client2.set(key, value);
                System.out.println("sentinelCluster Result:" + client2.get(key));
                System.out.println("sentinelCluster Result:" + client2.get(key));
                System.out.println("sentinelCluster Result:" + client2.get(key));

            } catch (Throwable e) {
                e.printStackTrace();
            }
        }


        Thread.sleep(100000);
		redisClient.set("testkey", "testkey");
		System.out.println(redisClient.get("testkey"));

		redisClientSentinel.set("testkeys", "testkeys");
		System.out.println(redisClientSentinel.get("testkeys"));
    }
}
