package org.helium.redis;


import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.framework.utils.Closeable;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.redis.spi.RedisCounters;
import org.helium.redis.spi.RedisLoader;
import org.helium.redis.utils.ByteArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.*;
import redis.clients.util.SafeEncoder;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Redis 的客户端类
 * <p>
 * get set : String类型操作 lget lset : List类型操作函数 hget hset : Hash类型操作函数 sget sset
 * : Set类型操作函数 zget zset : SortedSet操作类型
 *
 * @author lichunlei
 */
@FieldLoaderType(loaderType = RedisLoader.class)
public class RedisShardedImpl implements RedisClient {

    private ShardedJedisPool shardedJedisPool;// 切片连接池

    private static final Logger logger = LoggerFactory.getLogger(RedisShardedImpl.class);

    private RedisCounters counters;

    /**
     * 构造函数，根据Properties配置文件初始化切片池
     *
     * @param prop
     */
    public RedisShardedImpl(Properties prop) {
        initPool(prop);
        this.counters = PerformanceCounterFactory.getCounters(RedisCounters.class, "Redis-Client");
    }

    public RedisShardedImpl(Properties prop, RedisCounters counters) {
        initPool(prop);
        this.counters = counters;
    }

    /**
     * 初始化切片池
     */
    private void initPool(Properties prop) {
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
        JedisPoolConfig pool = new JedisPoolConfig();
        Class<JedisPoolConfig> clazz = JedisPoolConfig.class;
        Method[] methods = null;
        String key = null;
        String val = null;
        String methodName = null;
        Enumeration<?> eunm = prop.propertyNames();
        while (eunm.hasMoreElements()) {
            key = (String) eunm.nextElement();
            val = prop.getProperty(key);
            methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
            methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    try {
                        if (method.getParameterTypes()[0].getName() == "int") {
                            method.invoke(pool, Integer.parseInt(val));
                        } else if (method.getParameterTypes()[0].getName() == "long") {
                            method.invoke(pool, Long.parseLong(val));
                        } else if (method.getParameterTypes()[0].getName() == "float") {
                            method.invoke(pool, Float.parseFloat(val));
                        } else if (method.getParameterTypes()[0].getName() == "double") {
                            method.invoke(pool, Double.parseDouble(val));
                        } else if (method.getParameterTypes()[0].getName() == "boolean") {
                            method.invoke(pool, Boolean.parseBoolean(val));
                        } else if (method.getParameterTypes()[0].getName() == "java.lang.String") {
                            method.invoke(pool, val);
                        } else {
                            method.invoke(pool, val);
                        }
                    } catch (Exception e) {
                        logger.error("init RedisClient error{}", e);
                        // throw e;
                    }
                    break;
                }
            }
        }
        // 分片
        List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
        int serverNum = Integer.parseInt(prop.getProperty("serverNumber"));
        for (int i = 0; i < serverNum; i++) {
            String name = "server." + i;
            if (prop.containsKey(name)) {
                String address = prop.getProperty(name);
                String[] ipPort = address.split(":");
                JedisShardInfo shareInfo = new JedisShardInfo(ipPort[0], Integer.parseInt(ipPort[1]), name);
                shards.add(shareInfo);
            }
        }
        // 构造池
        shardedJedisPool = new ShardedJedisPool(pool, shards);
    }

    /**
     * 给指定key的字符串值追加value,返回新字符串值的长度。
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public Long append(String key, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.append(key, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }


    @Override
    public String ping(String key) {
        throw new UnsupportedOperationException("ShardedJedis Unsupported ping ");
    }

    @Override
    public Long bitcount(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.bitcount(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long bitcount(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.bitcount(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public List<String> blpop(int timeout, String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.blpop(timeout, key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 它是 LPOP 命令的阻塞版本
     */
    @Override
    public List<String> blpop(String arg) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.blpop(arg);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * LPOP的阻塞版本
     *
     * @param arg
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> List<E> blpop(String arg, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsArg = SafeEncoder.encode(arg);
            List<byte[]> value = jedis.blpop(bsArg);

            if (value == null)
                return null;

            List<E> list = new ArrayList<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                list.add(e);
            }
            return list;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public List<String> brpop(int timeout, String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.brpop(timeout, key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 它是 RPOP 命令的阻塞版本
     */
    @Override
    public List<String> brpop(String arg) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.brpop(arg);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * RPOP的阻塞版本
     *
     * @param arg
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> List<E> brpop(String arg, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsArg = SafeEncoder.encode(arg);

            List<byte[]> value = jedis.brpop(bsArg);

            if (value == null)
                return null;

            List<E> list = new ArrayList<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                list.add(e);
            }
            return list;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 但是做的是减减操作，decr一个不存在key，则设置key为-1
     *
     * @param key
     * @return
     */
    @Override
    public Long decr(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.decr(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同decr，减指定值。
     *
     * @param key
     * @param integer
     * @return
     */
    @Override
    public Long decrBy(String key, long integer) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.decrBy(key, integer);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 删除key对应的值
     */
    @Override
    public Long del(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.del(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public String echo(String string) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.echo(string);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 是否存在key
     *
     * @param key
     * @return
     */
    @Override
    public Boolean exists(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Boolean tmp = jedis.exists(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置超时时间，单位秒；返回1成功，0表示key已经设置过过期时间或者不存在
     *
     * @param key
     * @param expire
     * @return
     */
    @Override
     public Long expire(String key, int expire) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.expire(key, expire);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Long expire(byte[] key, int expire) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.expire(key, expire);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * EXPIREAT命令接受的时间参数是UNIX时间戳(unix timestamp)。
     *
     * @param key
     * @param unixTime
     * @return
     */
    @Override
    public Long expireAt(String key, long unixTime) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.expireAt(key, unixTime);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取key对应的value
     *
     * @param key
     * @return
     */
    @Override
     public String get(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.get(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public byte[] get(byte[] key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] tmp = jedis.get(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取key对应的value
     *
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E get(String key, Class<E> clazz) {
        byte[] bsKey = SafeEncoder.encode(key);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] value = jedis.get(bsKey);
            if (value == null)
                return null;
            // 首先释放资源
            returnJedis(jedis);
            watch.end();
            jedis = null;
            // 反序列化
            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(value, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回在指定Offset上BIT的值，0或1。如果Offset超过string value的长度，该命令将返回0，所以对于空字符串始终返回0。
     */
    @Override
    public Boolean getbit(String key, long offset) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Boolean tmp = jedis.getbit(key, offset);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取Jedis实例
     *
     * @return
     */
    public ShardedJedis getJedis() {
        counters.getQps().increase();
        return shardedJedisPool.getResource();
    }

    @Override
    public String getrange(String key, long startOffset, long endOffset) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.getrange(key, startOffset, endOffset);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 原子的设置key的值，并返回key的旧值。如果key不存在返回nil
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String getSet(String key, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.getSet(key, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 原子的设置key的值，并返回key的旧值。如果key不存在返回null
     *
     * @param key
     * @param value
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E getSet(String key, SuperPojo value, Class<E> clazz) {
        byte[] bs = value.toPbByteArray();
        byte[] bsKey = SafeEncoder.encode(key);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsOld = jedis.getSet(bsKey, bs);

            if (bsOld == null)
                return null;

            returnJedis(jedis);
            watch.end();
            jedis = null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bsOld, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 删除指定的hash field
     *
     * @param key
     * @param fields
     * @return
     */
    @Override
    public Long hdel(String key, String... fields) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hdel(key, fields);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 测试指定field是否存在
     *
     * @param key
     * @param field
     * @return
     */
    @Override
    public Boolean hexists(String key, String field) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Boolean tmp = jedis.hexists(key, field);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取指定的hash field
     *
     * @param key
     * @param field
     * @return
     */
    @Override
    public String hget(String key, String field) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.hget(key, field);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取指定的hash field
     *
     * @param key
     * @param field
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E hget(String key, String field, Class<E> clazz) {
        byte[] bsKey = SafeEncoder.encode(key);
        byte[] bsField = SafeEncoder.encode(field);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bs = jedis.hget(bsKey, bsField);

            if (bs == null)
                return null;

            returnJedis(jedis);
            watch.end();
            jedis = null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bs, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取指定的hash field
     *
     * @param key
     * @param field
     * @return
     */
    public byte[] hgetPojo(String key, String field) {
        byte[] bsKey = SafeEncoder.encode(key);
        byte[] bsField = SafeEncoder.encode(field);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bs = jedis.hget(bsKey, bsField);

            if (bs == null)
                return null;

            returnJedis(jedis);
            watch.end();
            jedis = null;
            return bs;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回hash的所有filed和value
     *
     * @param key
     * @return
     */
    @Override
    public Map<String, String> hgetAll(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Map<String, String> tmp = jedis.hgetAll(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回hash的所有filed和value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
     *
     * @param key
     * @return
     */
    public Map<String, byte[]> hgetAllPojo(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            Map<byte[], byte[]> map = jedis.hgetAll(bsKey);

            if (map == null)
                return null;

            Map<String, byte[]> tmp = new HashMap<String, byte[]>();
            for (byte[] bsField : map.keySet())
                tmp.put(SafeEncoder.encode(bsField), map.get(bsField));

            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 将指定的hash filed 加上给定值
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    @Override
    public Long hincrBy(String key, String field, long value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hincrBy(key, field, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回hash的所有field
     *
     * @param key
     * @return
     */
    @Override
    public Set<String> hkeys(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.hkeys(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回指定hash的field数量
     *
     * @param key
     * @return
     */
    @Override
    public Long hlen(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hlen(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取全部指定的hash filed
     *
     * @param key
     * @param fields
     * @return
     */
    @Override
    public List<String> hmget(String key, String... fields) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.hmget(key, fields);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取全部指定的hash filed
     *
     * @param key
     * @param fields
     * @return
     */
    public List<byte[]> hmgetPojo(String key, String... fields) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);

            List<byte[]> tmp = jedis.hmget(bsKey, SafeEncoder.encodeMany(fields));
            return tmp;

        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public String hmset(String key, Map<String, String> hash) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.hmset(key, hash);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置Map
     *
     * @param key
     * @param hash
     * @return
     */
    public <E extends SuperPojo> String hmsetPojo(String key, Map<String, E> hash) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);

            Map<byte[], byte[]> map = new HashMap<byte[], byte[]>();
            for (String field : hash.keySet()) {
                byte[] bsField = SafeEncoder.encode(field);
                E e = hash.get(field);
                byte[] bs = e.toPbByteArray();
                map.put(bsField, bs);
            }

            String tmp = jedis.hmset(bsKey, map);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    @Deprecated
    public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            ScanResult<Entry<String, String>> tmp = jedis.hscan(key, cursor);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            ScanResult<Entry<String, String>> tmp = jedis.hscan(key, cursor);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置hash field为指定值，如果key不存在，则先创建
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    @Override
    public Long hset(String key, String field, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hset(key, field, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置hash field为指定值，如果key不存在，则先创建
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hset(String key, String field, SuperPojo value) {
        byte[] bs = value.toPbByteArray();
        byte[] bsKey = SafeEncoder.encode(key);
        byte[] bsField = SafeEncoder.encode(field);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hset(bsKey, bsField, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置hash field为指定值，如果key不存在，则先创建
     *
     * @param key
     * @param field
     * @param bs
     * @return
     */
    public Long hset(String key, String field, byte[] bs) {
        byte[] bsKey = SafeEncoder.encode(key);
        byte[] bsField = SafeEncoder.encode(field);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hset(bsKey, bsField, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置hash field为指定值，nx:not exist ,Key不存在的情况下才可以设置
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    @Override
    public Long hsetnx(String key, String field, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hsetnx(key, field, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * not exist key 和 field的情况下才能设置
     *
     * @param key
     * @param field
     * @param value
     * @return
     */
    public Long hsetnx(String key, String field, SuperPojo value) {
        byte[] bsKey = SafeEncoder.encode(key);
        byte[] bsField = SafeEncoder.encode(field);
        byte[] bs = value.toPbByteArray();

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.hsetnx(bsKey, bsField, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回hash的所有value
     *
     * @param key
     * @return
     */
    @Override
    public List<String> hvals(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.hvals(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回hash的所有value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
     *
     * @param key
     * @return
     */
    public Collection<byte[]> hvalsPojo(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            Collection<byte[]> tmp = jedis.hvals(bsKey);
            return tmp;

        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 对key的值做加加操作,并返回新的值。注意incr一个不是int的value会返回错误，incr一个不存在的key，则设置key为1
     *
     * @param key
     * @return
     */
    @Override
    public Long incr(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.incr(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同incr，加指定值。
     *
     * @param key
     * @param integer
     * @return
     */
    @Override
    public Long incrBy(String key, long integer) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.incrBy(key, integer);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }


    /**
     * 从左侧取第index个String
     */
    @Override
    public String lindex(String key, long index) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.lindex(key, index);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * List 从左侧数index获得元素
     *
     * @param key
     * @param index
     * @param clazz
     * @return
     */
    public <E extends SuperPojo> E lindex(String key, long index, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = jedis.lindex(bsKey, index);

            if (bsValue == null)
                return null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bsValue, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 在指定的元素pivot前面或是后面插入一个元素
     */
    @Override
    public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.linsert(key, where, pivot, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 在指定pivot的前面或者后面插入
     *
     * @param key
     * @param where
     * @param pivot
     * @param value
     * @return
     */
    public Long linsert(String key, BinaryClient.LIST_POSITION where, SuperPojo pivot, SuperPojo value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = value.toPbByteArray();
            byte[] bsPivot = pivot.toPbByteArray();
            Long tmp = jedis.linsert(bsKey, where, bsPivot, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回key对应list的长度，key不存在返回0,如果key对应类型不是list返回错
     *
     * @param key
     * @return
     */
    @Override
    public Long llen(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.llen(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 从list的头部删除元素，并返回删除元素。如果key对应list不存在或者是空返回nil，如果key对应值不是list返回错误
     *
     * @param key
     * @return
     */
    @Override
    public String lpop(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.lpop(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 从list的头部删除元素，并返回删除元素。如果key对应list不存在或者是空返回null，如果key对应值不是list返回错误
     *
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E lpop(String key, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = jedis.lpop(bsKey);

            if (bsValue == null)
                return null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bsValue, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key
     * @param strings
     * @return
     */
    @Override
    public Long lpush(String key, String... strings) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.lpush(key, strings);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key
     * @return
     */
    public Long lpush(String key, SuperPojo pojo) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bs = pojo.toPbByteArray();
            Long tmp = jedis.lpush(bsKey, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     */
    @Override
    public Long lpushx(String key, String... string) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.lpushx(key, string);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     *
     * @param key
     * @param pojo
     * @return
     */
    public Long lpushx(String key, SuperPojo pojo) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = pojo.toPbByteArray();
            Long tmp = jedis.lpushx(bsKey, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素 ，key不存在返回空列表
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<String> lrange(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.lrange(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素 ，key不存在返回空列表
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> List<E> lrange(String key, long start, long end, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            List<byte[]> values = jedis.lrange(bsKey, start, end);

            if (values == null)
                return null;
            List<E> list = new ArrayList<E>(values.size());
            for (byte[] value : values) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(value, e);
                list.add(e);
            }
            return list;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Long lrem(String key, long count, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.lrem(key, count, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    public Long lrem(String key, long count, SuperPojo value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = value.toPbByteArray();
            Long tmp = jedis.lrem(bsKey, count, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置list中指定下标的元素值，成功返回1，key或者下标不存在返回错误
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    @Override
    public String lset(String key, long index, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.lset(key, index, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置list中指定下标的元素值，成功返回1，key或者下标不存在返回错误
     *
     * @param key
     * @param index
     * @param value
     * @return
     */
    public String lset(String key, long index, SuperPojo value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = value.toPbByteArray();

            String tmp = jedis.lset(bsKey, index, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 截取list，保留指定区间内元素，成功返回1，key不存在返回错误
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public String ltrim(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.ltrim(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 将当前数据库(默认为0)的key移动到给定的数据库db当中。
     *
     * @param key
     * @param dbIndex
     * @return
     */
    @Override
    public Long move(String key, int dbIndex) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.move(key, dbIndex);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 移除给定key的生存时间。
     *
     * @param key
     * @return
     */
    @Override
    public Long persist(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.persist(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * Available since 2.8.9. Adds all the element arguments to the HyperLogLog
     * data structure stored at the variable name specified as first argument.
     * If the approximated cardinality estimated by the HyperLogLog changed
     * after executing the command, PFADD returns 1, otherwise 0 is returned.
     * 参考：HyperLogLog算法，高大上，主要用于大数据下统计不同element的数量
     */
    @Override
    public Long pfadd(String key, String... elements) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.pfadd(key, elements);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    public Long pfadd(String key, SuperPojo element) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = element.toPbByteArray();
            Long tmp = jedis.pfadd(bsKey, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * Available since 2.8.9. pfadd在key下面的不重复elements的数量
     */
    @Override
    public long pfcount(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            long tmp = jedis.pfcount(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 归还Jedis实例
     *
     * @param jedis
     */
    public void returnJedis(ShardedJedis jedis) {
        if (jedis != null) {
            shardedJedisPool.returnResource(jedis);
        }
    }

    /**
     * 同上，但是从尾部删除
     *
     * @param key
     * @return
     */
    @Override
    public String rpop(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.rpop(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同上，但是从尾部删除
     *
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E rpop(String key, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = jedis.rpop(bsKey);

            if (bsValue == null)
                return null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bsValue, tmp);

            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 在key对应list的尾部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key
     * @param strings
     * @return
     */
    @Override
    public Long rpush(String key, String... strings) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.rpush(key, strings);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 在key对应list的尾部添加SuperPojo元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key
     * @param pojo
     * @return
     */
    public Long rpush(String key, SuperPojo pojo) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bs = pojo.toPbByteArray();

            Long tmp = jedis.rpush(bsKey, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     */
    @Override
    public Long rpushx(String key, String... string) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.rpushx(key, string);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     *
     * @param key
     * @param pojo
     * @return
     */
    public Long rpushx(String key, SuperPojo pojo) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = pojo.toPbByteArray();
            Long tmp = jedis.rpushx(bsKey, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * sadd key member
     * 添加一个string元素到,key对应的set集合中，成功返回1,如果元素以及在集合中返回0,key对应的set不存在返回错误
     *
     * @param key
     * @param members
     * @return
     */
    @Override
    public Long sadd(String key, String... members) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.sadd(key, members);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * sadd key member
     * 添加一个SuperPojo元素到key对应的set集合中，成功返回1,如果元素已经在集合中返回0,key对应的set不存在返回错误
     *
     * @param key
     * @param member
     * @return
     */
    public Long sadd(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsValue = member.toPbByteArray();
            Long tmp = jedis.sadd(bsKey, bsValue);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回set的元素个数，如果set是空或者key不存在返回0
     *
     * @param s
     * @return
     */
    @Override
    public Long scard(String s) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.scard(s);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置key对应的value
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public String set(byte[] key, byte[] value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.set(key, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public String set(String key, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.set(key, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public String set(String key, String value, String nxxx, String expx, long time) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.set(key, value, nxxx, expx, time);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 设置key对应的value
     *
     * @param key
     * @param value
     * @return
     */

    public String set(String key, SuperPojo value) {
        byte[] bs = value.toPbByteArray();
        byte[] bsKey = SafeEncoder.encode(key);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.set(bsKey, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置在指定Offset上BIT的值，该值只能为1或0，在设定后该命令返回该Offset上原有的BIT值。如果指定Key不存在，该命令将创建一个新值
     * ，并在指定的Offset上设定参数中的BIT值
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    @Override
    public Boolean setbit(String key, long offset, boolean value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Boolean tmp = jedis.setbit(key, offset, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Boolean setbit(String key, long offset, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Boolean tmp = jedis.setbit(key, offset, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置带超时时间的value
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    @Override
    public String setex(String key, int seconds, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.setex(key, seconds, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 设置带超时时间的value
     *
     * @param key
     * @param seconds
     * @param value
     * @return
     */
    public String setex(String key, int seconds, SuperPojo value) {
        byte[] bs = value.toPbByteArray();
        byte[] bsKey = SafeEncoder.encode(key);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.setex(bsKey, seconds, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 如果key已经存在，返回0 。nx 是not exist的意思
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public Long setnx(String key, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.setnx(key, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 如果key已经存在，返回0 。nx 是not exist的意思
     *
     * @param key
     * @param value
     * @return
     */
    public Long setnx(String key, SuperPojo value) {
        byte[] bs = value.toPbByteArray();
        byte[] bsKey = SafeEncoder.encode(key);

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.setnx(bsKey, bs);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 从第offset个开始替换
     */
    @Override
    public Long setrange(String key, long offset, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.setrange(key, offset, value);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 判断member是否在set中，存在返回1，0表示不存在或者key不存在
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Boolean sismember(String key, String member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Boolean tmp = jedis.sismember(key, member);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 判断member是否在set中，存在返回1，0表示不存在或者key不存在
     *
     * @param key
     * @param member
     * @return
     */
    public Boolean sismember(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();
            Boolean tmp = jedis.sismember(bsKey, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回key对应set的所有元素，结果是无序的
     *
     * @param key
     * @return
     */
    @Override
    public Set<String> smembers(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.smembers(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回key对应set的所有元素，结果是无序的
     *
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> Set<E> smembers(String key, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);

            Set<byte[]> value = jedis.smembers(bsKey);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public List<String> sort(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.sort(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    public <E extends SuperPojo> List<E> sort(String key, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            List<byte[]> value = jedis.sort(bsKey);

            if (value == null)
                return null;

            List<E> list = new ArrayList<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                list.add(e);
            }
            return list;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public List<String> sort(String key, SortingParams sortingParameters) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.sort(key, sortingParameters);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    public <E extends SuperPojo> List<E> sort(String key, SortingParams sortingParameters, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            List<byte[]> value = jedis.sort(bsKey, sortingParameters);

            if (value == null)
                return null;

            List<E> list = new ArrayList<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                list.add(e);
            }
            return list;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 删除并返回key对应set中随机的一个元素,如果set是空或者key不存在返回nil
     *
     * @param key
     * @return
     */
    @Override
    public String spop(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.spop(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 删除并返回key对应set中随机的一个元素,如果set是空或者key不存在返回null
     *
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E spop(String key, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = jedis.spop(bsKey);

            if (bsMember == null)
                return null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bsMember, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同spop，随机取set中的一个元素，但是不删除元素
     *
     * @param key
     * @return
     */
    @Override
    public String srandmember(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.srandmember(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同spop，随机取set中的一个元素，但是不删除元素
     *
     * @param key
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> E srandmember(String key, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = jedis.srandmember(bsKey);

            if (bsMember == null)
                return null;

            E tmp = clazz.newInstance();
            SuperPojoManager.parsePbFrom(bsMember, tmp);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public List<String> srandmember(String key, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<String> tmp = jedis.srandmember(key, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 从key对应set中移除给定元素，成功返回1，如果member在集合中不存在或者key不存在返回0，如果key对应的不是set类型的值返回错误
     *
     * @param key
     * @param members
     * @return
     */
    @Override
    public Long srem(String key, String... members) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.srem(key, members);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 从key对应set中移除给定元素，成功返回1，如果member在集合中不存在或者key不存在返回0，如果key对应的不是set类型的值返回错误
     *
     * @param key
     * @param member
     * @return
     */
    public Long srem(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();
            Long tmp = jedis.srem(bsKey, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    @Deprecated
    public ScanResult<String> sscan(String key, int cursor) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            ScanResult<String> tmp = jedis.sscan(key, cursor);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public ScanResult<String> sscan(String key, String cursor) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            ScanResult<String> tmp = jedis.sscan(key, cursor);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long strlen(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.strlen(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回截取过的key的字符串值,注意并不修改key的值。
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public String substr(String key, int start, int end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.substr(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /******************** 以下内容为自创部分--支持SuperPojo ***********************************/

    /**
     * 返回设置过过期时间的key的剩余过期秒数 -1表示key不存在或者没有设置过过期时间
     *
     * @param key
     * @return
     */
    @Override
    public Long ttl(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.ttl(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Long ttl(byte[] key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.ttl(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回给定key的value的类型 none,string,list,set
     *
     * @param key
     * @return
     */
    @Override
    public String type(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String tmp = jedis.type(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 添加元素到集合，元素在集合中存在则更新对应score
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    @Override
    public Long zadd(String key, double score, String member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zadd(key, score, member);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 添加元素到集合，元素在集合中存在则更新对应score
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Long zadd(String key, double score, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();

            Long tmp = jedis.zadd(bsKey, score, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long zadd(String key, Map<String, Double> scoreMembers) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zadd(key, scoreMembers);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中元素个数
     *
     * @param key
     * @return
     */
    @Override
    public Long zcard(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zcard(key);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的数量
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Long zcount(String key, double min, double max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zcount(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long zcount(String key, String min, String max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zcount(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 增加对应member的score值，然后移动元素并保持skip list保持有序。返回更新后的score值
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    @Override
    public Double zincrby(String key, double score, String member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Double tmp = jedis.zincrby(key, score, member);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 增加对应member的score值，然后移动元素并保持skip list保持有序。返回更新后的score值
     *
     * @param key
     * @param score
     * @param member
     * @return
     */
    public Double zincrby(String key, double score, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();
            Double tmp = jedis.zincrby(bsKey, score, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long zlexcount(String key, String min, String max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zlexcount(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 类似lrange操作从集合中去指定区间的元素。返回的是有序结果
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<String> zrange(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrange(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 类似lrange操作从集合中去指定区间的元素。返回的是有序结果
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> Set<E> zrange(String key, long start, long end, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);

            Set<byte[]> value = jedis.zrange(bsKey, start, end);
            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrangeByLex(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrangeByLex(key, min, max, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrangeByScore(String key, double min, double max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrangeByScore(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key
     * @param min
     * @param max
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> Set<E> zrangeByScore(String key, double min, double max, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            Set<byte[]> value = jedis.zrangeByScore(bsKey, min, max);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrangeByScore(key, min, max, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrangeByScore(String key, double min, double max, int offset, int count, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            Set<byte[]> value = jedis.zrangeByScore(bsKey, min, max, offset, count);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素(min.score ,max.score)
     *
     * @param key
     * @param min
     * @param max
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrangeByScore(String key, E min, E max, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMin = min.toPbByteArray();
            byte[] bsMax = max.toPbByteArray();
            Set<byte[]> value = jedis.zrangeByScore(bsKey, bsMin, bsMax);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素(min.score,max.score)
     *
     * @param key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrangeByScore(String key, E min, E max, int offset, int count, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMax = max.toPbByteArray();
            byte[] bsMin = min.toPbByteArray();

            Set<byte[]> value = jedis.zrangeByScore(bsKey, bsMin, bsMax, offset, count);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素(min.score,max.score)
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrangeByScore(String key, String min, String max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrangeByScore(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrangeByScore(key, min, max, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<Tuple> zrangeWithScores(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrangeWithScores(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回指定元素在集合中的排名（下标）,集合中元素是按score从小到大排序的
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Long zrank(String key, String member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zrank(key, member);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回指定元素在集合中的排名（下标）,集合中元素是按score从小到大排序的
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrank(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();
            Long tmp = jedis.zrank(bsKey, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
     *
     * @param key
     * @param members
     * @return
     */
    @Override
    public Long zrem(String key, String... members) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zrem(key, members);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
     *
     * @param key
     * @return
     */
    public Long zrem(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();
            Long tmp = jedis.zrem(bsKey, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long zremrangeByLex(String key, String min, String max) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zremrangeByLex(key, min, max);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 删除集合中排名在给定区间的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Long zremrangeByRank(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zremrangeByRank(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 删除集合中score在给定区间的元素
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Long zremrangeByScore(String key, double start, double end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zremrangeByScore(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Long zremrangeByScore(String key, String start, String end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zremrangeByScore(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回结果是按score逆序的
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<String> zrevrange(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrevrange(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回结果是按score逆序的
     *
     * @param key
     * @param start
     * @param end
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public <E extends SuperPojo> Set<E> zrevrange(String key, long start, long end, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            Set<byte[]> value = jedis.zrevrange(bsKey, start, end);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素,逆序，从大到小
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrevrangeByScore(key, max, min);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的元素,逆序，从大到小
     *
     * @param key
     * @param max
     * @param min
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, double max, double min, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);

            Set<byte[]> value = jedis.zrevrangeByScore(bsKey, max, min);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    @Override
    public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrevrangeByScore(key, max, min, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的元素,逆序，从大到小
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, double max, double min, int offset, int count, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);

            Set<byte[]> value = jedis.zrevrangeByScore(bsKey, max, min, offset, count);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素,逆序
     *
     * @param key
     * @param max
     * @param min
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, E max, E min, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMax = max.toPbByteArray();
            byte[] bsMin = min.toPbByteArray();
            Set<byte[]> value = jedis.zrevrangeByScore(bsKey, bsMax, bsMin);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @param clazz
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, E max, E min, int offset, int count, Class<E> clazz) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {

            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMax = max.toPbByteArray();
            byte[] bsMin = min.toPbByteArray();

            Set<byte[]> value = jedis.zrevrangeByScore(bsKey, bsMax, bsMin, offset, count);

            if (value == null)
                return null;

            Set<E> set = new HashSet<E>();
            for (byte[] bs : value) {
                E e = clazz.newInstance();
                SuperPojoManager.parsePbFrom(bs, e);
                set.add(e);
            }
            return set;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

    /**
     * 返回集合中score在给定区间的元素(max.score,min.score),逆序，从大到小
     *
     * @param key
     * @param min
     * @param max
     * @return
     */
    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrevrangeByScore(key, max, min);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<String> tmp = jedis.zrevrangeByScore(key, max, min, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回集合中score在给定区间的元素 逆序排列
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    @Override
    public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Set<Tuple> tmp = jedis.zrevrangeWithScores(key, start, end);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同上,但是集合中元素是按score从大到小排序
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Long zrevrank(String key, String member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Long tmp = jedis.zrevrank(key, member);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 同上,但是集合中元素是按score从大到小排序
     *
     * @param key
     * @param member
     * @return
     */
    public Long zrevrank(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();
            Long tmp = jedis.zrevrank(bsKey, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    @Deprecated
    public ScanResult<Tuple> zscan(String key, int cursor) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            ScanResult<Tuple> tmp = jedis.zscan(key, cursor);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    @Override
    public ScanResult<Tuple> zscan(String key, String cursor) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            ScanResult<Tuple> tmp = jedis.zscan(key, cursor);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回给定元素对应的score
     *
     * @param key
     * @param member
     * @return
     */
    @Override
    public Double zscore(String key, String member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Double tmp = jedis.zscore(key, member);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 返回给定元素对应的score
     *
     * @param key
     * @param member
     * @return
     */
    public Double zscore(String key, SuperPojo member) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte[] bsKey = SafeEncoder.encode(key);
            byte[] bsMember = member.toPbByteArray();

            Double tmp = jedis.zscore(bsKey, bsMember);
            return tmp;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }

    }

    /**
     * 获取redis时间
     *
     * @param key
     * @return
     */
    public List<String> time(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.getShard(key).time();
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
            watch.end();
        }
    }

	@Override
	public Closeable<Pipeline> getPipeline(String key) {
		ShardedJedis jedis = getJedis();
		Pipeline pipeline = jedis.getShard(key).pipelined();
		return new Closeable<Pipeline>() {
			@Override
			public Pipeline get() {
				return pipeline;
			}

			@Override
			public void close() {
				pipeline.sync();
				returnJedis(jedis);
			}
		};
	}

    @Override
    public boolean setObjectEx(String key, Object value, int time) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.setex(key.getBytes(), time, ByteArrayUtils.toByteArray(value));
            watch.end();
            return true;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }


    @Override
    public boolean setex(String key, byte[] value, int time) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.setex(key.getBytes(), time, value);
            watch.end();
            return true;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public boolean set(String key, byte[] value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.set(key.getBytes(), value);
            watch.end();
            return true;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }


    @Override
    public boolean setObject(String key, Object value) {

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.set(key.getBytes(), ByteArrayUtils.toByteArray(value));
            watch.end();
            return true;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }


    @Override
    public boolean sadd(String id, Object value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.sadd(id.getBytes(), ByteArrayUtils.toByteArray(value));
            watch.end();
            return true;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }

    }

    @Override
    public boolean sadd(String id, byte[] value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.sadd(id.getBytes(), value);
            watch.end();
            return true;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public Object getObject(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte [] values = jedis.get(key.getBytes());
            watch.end();
            if(values == null) {
                return null;
            }
            return ByteArrayUtils.toObject(values);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }

    }

    @Override
    public byte[] getBytes(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            byte [] values = jedis.get(key.getBytes());
            watch.end();
            if(values == null) {
                return null;
            }
            return values;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public List<String> lrangeAll(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public List<String> lrange(String key, int start, int end) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public long lleng(String key) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.llen(key);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public void setSubscribe(JedisPubSub pubSub) {
        throw new RuntimeException("not support setSubscribe");
    }

    @Override
    public void sadd(byte[] key, byte[] value) {

        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.sadd(key, value);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public void sadd(String key, String value) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.sadd(key, value);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public Set<byte[]> smembers(byte[] key) {
        
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.smembers(key);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public void srem(byte[] key, byte[]... value) {
        
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.srem(key, value);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public boolean sismember(byte[] setKey, byte[] value) {
        String keyStr = new String(setKey);
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.sismember(setKey, value);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public void hmset(byte[] key, Map<byte[], byte[]> map) {
        
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis.hmset(key, map);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public Map<byte[], byte[]> hmget(byte[] key) {
        
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            return jedis.hgetAll(key);
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public byte[] hmgetValue(byte[] key, byte[] keyword) {
        
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<byte[]> value = jedis.hmget(key, keyword);
            if(value != null && value.size() > 0){
                return value.get(0);
            }else{
                return null;
            }
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }

    }

    @Override
    public List<byte[]> hmget(byte[] key, byte[][] fields) {
        
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            List<byte[]> value = jedis.hmget(key, fields);
            return value;
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis( jedis);
        }
    }

    @Override
    public Object eval(String script, List<String> keys, List<String> args) {
        String keyStr = new String("eval");
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            Object result = jedis.getShard(keyStr).eval(script, keys, args);
            if(result != null){
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }

    @Override
    public boolean set(String key, String value, String nxxx, String expx, int expire) {
        ShardedJedis jedis = getJedis();
        Stopwatch watch = counters.getTx().begin();
        try {
            String result = jedis.set(key, value, nxxx, expx, expire);
            if("OK".equals(result)){
                return true;
            }else{
                return false;
            }
        } catch (Exception e) {
            watch.fail(e);
            throw new RuntimeException(e);
        } finally {
            returnJedis(jedis);
        }
    }
    

    @Override
	public void pipelined(String key, Consumer<Pipeline> func) {
		ShardedJedis jedis = getJedis();
		Pipeline pipeline = jedis.getShard(key).pipelined();
		try {
			func.accept(pipeline);
		} finally {
			pipeline.sync();
			returnJedis(jedis);
		}
	}
}
