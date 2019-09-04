package org.helium.redis.sentinel;

import org.helium.redis.sentinel.router.provider.HashRouteProvider;
import org.helium.redis.sentinel.router.provider.IRouteProvider;
import org.helium.util.DictionaryList;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.*;

/**
 *
 */

public class RedisRouteDirector {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisRouteDirector.class);
    private static RedisRouteDirector instance = null;
    private static Byte syncRoot = new Byte("0");

    private static Map<String, IRouteProvider<RedisSentinelsCfg, Pool<Jedis>>> hashProvider =
            new HashMap<String, IRouteProvider<RedisSentinelsCfg, Pool<Jedis>>>();

    private static Map<String, Properties> configs = new HashMap<String, Properties>();

    private RedisRouteDirector() {
    }


    public static RedisRouteDirector getInstance() {
        if (instance == null)
            instance = new RedisRouteDirector();
        return instance;
    }

    public IRouteProvider<RedisSentinelsCfg, Pool<Jedis>> getRouteProvider(String rouleName) {
        if (hashProvider.get(rouleName) != null)
            return hashProvider.get(rouleName);

        throw new RuntimeException("RedisRouteDirector.rouleName#" + rouleName);
    }

    /**
     * 用来区分业务,默认使用sentinel MasterName
     *
     * @param roleName
     * @return
     */
    public IRouteProvider<RedisSentinelsCfg, Pool<Jedis>> initRouteProvider(String roleName, Properties prop) {
        updateConfig(roleName, prop);
        return getRouteProvider(roleName);
    }

    public IRouteProvider<RedisSentinelsCfg, Pool<Jedis>> initRouteProvider(String roleName, List<RedisSentinelsCfg> configs) {
        updateConfig(configs);
        return getRouteProvider(roleName);
    }

    /**
     * 把一个properties 初始化成SentinelJedis 的配置.
     * 这个目前有个问题: 还不能同一个配置支持多个自由分配权重,以及连接属性
     * 也就是说如果配置了多组sentinel的地址,
     * 则认为他们有相同的masterame,连接属性配置(比如最大连接数), 权重会是按照顺序平均配置.
     *
     * @param roleName
     * @param prop
     */
    private void updateConfig(String roleName, Properties prop) {
        String masterNamePrefix = null;
        if (prop.containsKey("masterNamePrefix")) {
            masterNamePrefix = prop.getProperty("masterNamePrefix");
        }

        // 实际上 配置的地址是 sentinel 的地址, 从sentinel 以及master name 获取实际地址. 一般sentinel 实例都是多个.
        //192.168.247.228:26380;172.21.35.196:26380;172.21.35.197:26380;
        int serverNum = Integer.parseInt(prop.getProperty("serverNumber"));


        List<RedisSentinelsCfg> cfgs = new ArrayList<>();
        for (int i = 0; i < serverNum; i++) {
            String name = "server." + i;
            if (prop.containsKey(name)) {

                String addrs = prop.getProperty(name);
                String masterName = prop.getProperty(name + ".masterName");
                if (masterName == null || masterName.trim().equals("")) {
                    if (!StringUtils.isNullOrEmpty(masterNamePrefix)) {
                        masterName = masterNamePrefix + i;
                    } else {
                        throw new RuntimeException(String.format("load Redis Error roleName=%s masterName is null", roleName));
                    }
                }

                if (addrs == null || addrs.length() > 0) {
                    RedisSentinelsCfg sentinelNode = new RedisSentinelsCfg();
                    sentinelNode.setId(i)
                            .setAddrs(addrs)
                            .setEnabled(1)
                            .setMasterName(masterName)
                            .setNodeOrder(i)
                            .setPolicy("Hash")
                            .setPropertiesExt(propertiesToStr(prop))
                            .setRoleName(roleName)
                            .setWeight(5);
                    cfgs.add(sentinelNode);
                }
            }
        }

        if (cfgs.size() != serverNum)
            throw new RuntimeException(String.format(" RedisSentinelClientImpl  rouleName=%s  cfgs.size() != serverNum ", roleName));

        hashProvider.put(roleName, new HashRouteProvider(roleName, cfgs));

    }


    private void updateConfig(List<RedisSentinelsCfg> configs) {
        try {

            DictionaryList<String, RedisSentinelsCfg> list =
                    new DictionaryList<String, RedisSentinelsCfg>();
            list.fillWith(new ArrayList<RedisSentinelsCfg>(configs), i -> i.getRoleName());
            Hashtable<String, IRouteProvider<RedisSentinelsCfg, Pool<Jedis>>> hash = new Hashtable<String, IRouteProvider<RedisSentinelsCfg, Pool<Jedis>>>();
            for (String role : list.keys()) {
                List<RedisSentinelsCfg> items = list.get(role);
                hash.put(role, new HashRouteProvider(role, items));

            }
            hashProvider = hash;
        } catch (RuntimeException e) {
            LOGGER.error("updateConfig ERROR", e);
        }

    }

    private String propertiesToStr(Properties prop) {

        StringBuilder sb = new StringBuilder();

        sb.append("database=").append(prop != null && prop.containsKey("database") ? prop.getProperty("database") : 0).append(System.lineSeparator());
        sb.append("minIdle=").append(prop != null && prop.containsKey("minIdle") ? prop.getProperty("minIdle") : 1).append(System.lineSeparator());
        sb.append("maxIdle=").append(prop != null && prop.containsKey("maxIdle") ? prop.getProperty("maxIdle") : 3).append(System.lineSeparator());
        sb.append("maxTotal=").append(prop != null && prop.containsKey("maxTotal") ? prop.getProperty("maxTotal") : 10).append(System.lineSeparator());
        sb.append("testOnReturn=").append(prop != null && prop.containsKey("testOnReturn") ? prop.getProperty("testOnReturn") : true).append(System.lineSeparator());
        sb.append("testOnBorrow=").append(prop != null && prop.containsKey("testOnBorrow") ? prop.getProperty("testOnBorrow") : true).append(System.lineSeparator());
        sb.append("testWhileIdle=").append(prop != null && prop.containsKey("testWhileIdle") ? prop.getProperty("testWhileIdle") : true).append(System.lineSeparator());
        sb.append("maxWaitMillis=").append(prop != null && prop.containsKey("maxWaitMillis") ? prop.getProperty("maxWaitMillis") : 3000).append(System.lineSeparator());
        sb.append("timeBetweenEvictionRunsMillis=").append(prop != null && prop.containsKey("timeBetweenEvictionRunsMillis") ? prop.getProperty("timeBetweenEvictionRunsMillis") : 60000).append(System.lineSeparator());
        sb.append("whenExhaustedAction=").append(prop != null && prop.containsKey("whenExhaustedAction") ? prop.getProperty("whenExhaustedAction") : 1).append(System.lineSeparator());
        return sb.toString();
    }


}