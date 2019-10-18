package org.helium.redis.widgets.redis.client;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.util.Pool;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by yibo on 2017-6-9.
 */
public class RedisHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisHelper.class);


    /**
     * 解析RedisInstances 获得一个Pool
     *
     * @param redisCluster
     * @return
     */
    public static Pool<Jedis> getPool(CFG_RedisSentinels redisCluster) {
        String cacheName = redisCluster.getRoleName();
        String config = redisCluster.getPropertiesExt();
//        Properties property = new RedisClusterProperties(true);
        Properties prop = new Properties();
        try {
            if (config != null)
                prop.load(new StringReader(config));
        } catch (IOException e) {
            LOGGER.error(String.format("load cacheName %s  config  %s in table RedisInstances error ",
                    cacheName, config), e);
            throw new RuntimeException(e);
        }

        if (!prop.containsKey("database"))
            prop.setProperty("database", "0");

        if (!prop.containsKey("minIdle"))
            prop.setProperty("minIdle", "1");
        if (!prop.containsKey("maxIdle"))
            prop.setProperty("maxIdle", "3");
        if (!prop.containsKey("maxTotal"))
            prop.setProperty("maxTotal", "10");
        if (!prop.containsKey("testWhileIdle"))
            prop.setProperty("testWhileIdle", "true");
        if (!prop.containsKey("maxWaitMillis"))
            prop.setProperty("maxWaitMillis", "3000");
        if (!prop.containsKey("timeBetweenEvictionRunsMillis"))
            prop.setProperty("timeBetweenEvictionRunsMillis", "60000");
        if (!prop.containsKey("whenExhaustedAction"))
            prop.setProperty("whenExhaustedAction", "1");
        // 自己加的内容，redis服务器的数量
        if (!prop.containsKey("serverNumber"))
            prop.setProperty("serverNumber", "100");

        // 池基本配置
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        Class<JedisPoolConfig> clazz = JedisPoolConfig.class;
        Enumeration<?> eunm = prop.propertyNames();
        while (eunm.hasMoreElements()) {
            String key = (String) eunm.nextElement();
            String val = prop.getProperty(key);
            String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    try {
                        if (method.getParameterTypes()[0].getName() == "int") {
                            method.invoke(jedisPoolConfig, Integer.parseInt(val));
                        } else if (method.getParameterTypes()[0].getName() == "long") {
                            method.invoke(jedisPoolConfig, Long.parseLong(val));
                        } else if (method.getParameterTypes()[0].getName() == "float") {
                            method.invoke(jedisPoolConfig, Float.parseFloat(val));
                        } else if (method.getParameterTypes()[0].getName() == "double") {
                            method.invoke(jedisPoolConfig, Double.parseDouble(val));
                        } else if (method.getParameterTypes()[0].getName() == "boolean") {
                            method.invoke(jedisPoolConfig, Boolean.parseBoolean(val));
                        } else if (method.getParameterTypes()[0].getName() == "java.lang.String") {
                            method.invoke(jedisPoolConfig, val);
                        } else {
                            method.invoke(jedisPoolConfig, val);
                        }
                    } catch (Exception e) {
                        LOGGER.error("init RedisClient error{}", e);
                    }
                    break;
                }
            }
        }

        jedisPoolConfig.setLifo(true);//不公平的竞争，保证连接数最少
        //得不到连接会锁，但是下面的maxwait保证不会锁太久。 即保证能够提供更好的可用性，又保证不会太耗线程。
        jedisPoolConfig.setBlockWhenExhausted(true);
        //redis等连接的时候不能超过100ms， 不然就会hang死线程。
//        if (maxWaitMillis > 100)
//            maxWaitMillis = 100;
//        if (maxWaitMillis <= 0) //如果不配置，默认给个10ms
//            maxWaitMillis = 10;
//        // 默认
//        if (maxWaitMillis > 0)
//            jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        Set<String> sentinelsSets = null;

        String sentinels = redisCluster.getAddrs();
        if (sentinels != null && sentinels.length() > 0) {
            sentinelsSets = new HashSet<>();
            String[] tmp = sentinels.split(";");
            for (int i = 0; i < tmp.length; i++) {
                sentinelsSets.add(tmp[i]);
            }

        }

        if (sentinelsSets == null) {
            LOGGER.error(String.format(
                    "the redis %s config column in table CFG_RedisSentinels set error, no ip address", cacheName));
            throw new RuntimeException(String.format(
                    "the redis %s config column in table CFG_RedisSentinels set error, no ip address", cacheName));
        }

        //print maxWaitMillis;

//        JedisSentinelPool pool1 =
//                new JedisSentinelPool(redisCluster.getMasterName(), sentinelsSets, jedisPoolConfig);
        // 修改为使用指定database 的构造方法.
        int database = 0;
        try {
            database = Integer.parseInt(prop.getProperty("database"));
        } catch (Exception e) {
            LOGGER.error("Redis Database  error  {}", new Gson().toJson(redisCluster));
        }

        int connectionBuildTimeout=2000;
        if (prop.containsKey("connTimeout"))
        {
            try {
                connectionBuildTimeout=Integer.parseInt( prop.getProperty("connTimeout"));
            } catch (Exception e) {
                LOGGER.error("parseInt connTimeout:"+prop.getProperty("connTimeout"),e);
            }
        }



//        Pool<Jedis> pool1 =
//                new JedisSentinelPoolFeinno(redisCluster.getMasterName(), sentinelsSets, jedisPoolConfig, Protocol.DEFAULT_TIMEOUT, null,
//                        database);

        Pool<Jedis> pool1 =
                new JedisSentinelPoolFeinno(redisCluster.getMasterName(), sentinelsSets, jedisPoolConfig, connectionBuildTimeout, null,
                        database);

        return pool1;
    }


    private static int getIntProperty(String cacheName, String propertyName, Properties property) {
        String rtn = (String) property.get(propertyName);
        int rtnInt = 0;
        if (rtn != null) {
            try {
                rtnInt = Integer.parseInt(rtn);
            } catch (Exception ex) {
                LOGGER.error(String.format("ERROR propertyName:%s, value:%s can't change to int",
                        propertyName, rtn));
                throw new RuntimeException(String.format(
                        "ERROR propertyName:%s, value:%s can't change to int", propertyName, rtn));
            }
        }
        return rtnInt;
    }

}