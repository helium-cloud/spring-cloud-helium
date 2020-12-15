package org.helium.redis.widgets.redis.client;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import redis.clients.util.Pool;
import redis.clients.util.SafeEncoder;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yibo on 2017-2-10.
 */

public class RedisSentinelClient2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisSentinelClient2.class);

    private IRouteProvider2<CFG_RedisSentinels, Pool<Jedis>> jedisPool;
    private RedisCounters counters;
    private RedisCounters borrowCounter;

    public RedisSentinelClient2(String redisRoleName, List<CFG_RedisSentinels> configs) {
        initialPool(redisRoleName, configs);
        this.counters = PerformanceCounterFactory.getCounters(RedisCounters.class, "RedisSentinel-Client");
        this.borrowCounter = PerformanceCounterFactory.getCounters(RedisCounters.class, "RedisSentinel-borrowCounter");
    }

    private void initialPool(String redisRoleName, List<CFG_RedisSentinels> configs) {
        jedisPool = RedisRouteDirector2.getInstance().initRouteProvider(redisRoleName, configs);
    }


    public void returnJedis2(RedisKey2 key, Jedis jedis) {
        if (jedis != null) {
            try {
                jedisPool.resolvePool(key).returnResource(jedis);
            } catch (Exception e) {
                LOGGER.error("return Jedis resource error.key:" + key, e);
            }
        }
    }

    public void returnBrokenJedis2(RedisKey2 key, Jedis jedis) {
        if (jedis != null) {
            try {
                jedisPool.resolvePool(key).returnBrokenResource(jedis);

            } catch (Exception e) {
                LOGGER.error("return returnBrokenJedis2 error.key:" + key, e);
            }
        }
    }

    private String printHostAndPort(Jedis jedis, double ms) {
        String result = "";
        if (jedis != null && jedis.getClient() != null) {
            result = "cost ms:" + ms + " jedis host and port:" + jedis.getClient().getHost() + ":" + jedis.getClient().getPort();
        }
        return result;
    }


    private void processEx(Jedis jedis, Stopwatch watch, RedisKey2 redisKey2, Exception e) {
        String trace = printHostAndPort(jedis, watch.getMillseconds());
        returnBrokenJedis2(redisKey2, jedis);
        LOGGER.error(trace, e);
        watch.fail(e);
    }


    public Jedis getJedis(RedisKey2 key) {

        Stopwatch stopwatch = borrowCounter.getTx().begin();
        Jedis result = null;
        long begin = System.nanoTime();
        try {
            Pool<Jedis> target = jedisPool.resolvePool(key);

            result = target.getResource();
            stopwatch.end();

        } catch (Exception ex) {
            returnBrokenJedis2(key, result);
            double ms = (double) (System.nanoTime() - begin) / 1E6;
            stopwatch.fail(ex);
            if (result != null) {
                LOGGER.error("getJedis getResource error but result is not null ,Impossile! ");
            }

            LOGGER.error("getJedis used ms:" + ms, ex);
            throw ex;
        }

        return result;

    }


    public byte[] hget(RedisKey2 redisKey2, byte[] bsKey, byte[] bsField) {

        byte[] result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hget(bsKey, bsField);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public String hget(RedisKey2 redisKey2, String key, String field) {

        String result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hget(key, field);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long hset(RedisKey2 redisKey2, String key, String field, byte[] bs) {
        byte[] bsKey = SafeEncoder.encode(key);
        byte[] bsField = SafeEncoder.encode(field);
        Long result = null;
        Stopwatch watch = counters.getTx().begin();

        Jedis jedis = null;
        try {
            jedis = getJedis(redisKey2);
            result = jedis.hset(bsKey, bsField, bs);
            watch.end();
            returnJedis2(redisKey2, jedis);

        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;

    }

    public Long hset(RedisKey2 redisKey2, byte[] bsKey, byte[] bsField, byte[] bs) {

        Long result = null;
        Stopwatch watch = counters.getTx().begin();

        Jedis jedis = null;
        try {
            jedis = getJedis(redisKey2);
            result = jedis.hset(bsKey, bsField, bs);
            watch.end();
            returnJedis2(redisKey2, jedis);

        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;

    }

    public Long hset(RedisKey2 redisKey2, String key, String field, String value) {

        Long result = null;
        Stopwatch watch = counters.getTx().begin();

        Jedis jedis = null;
        try {
            jedis = getJedis(redisKey2);
            result = jedis.hset(key, field, value);
            watch.end();
            returnJedis2(redisKey2, jedis);

        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;

    }

    public Long hdel(RedisKey2 redisKey2, String key, String... fields) {

        Jedis jedis = null;
        Long result = null;
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis = getJedis(redisKey2);
            result = jedis.hdel(key, fields);
            watch.end();
            returnJedis2(redisKey2, jedis);

        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long hdel(RedisKey2 redisKey2, final byte[] key, final byte[]... fields) {

        Jedis jedis = null;
        Long result = null;
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis = getJedis(redisKey2);
            result = jedis.hdel(key, fields);
            watch.end();
            returnJedis2(redisKey2, jedis);

        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public String get(RedisKey2 redisKey2, String key) {
        byte[] bsKey = SafeEncoder.encode(key);

        String result = null;
        byte[] resultBuff = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            resultBuff = jedis.get(bsKey);
            if (resultBuff != null) {
                result = new String(resultBuff);
            }

            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public byte[] get(RedisKey2 redisKey2, byte[] bsKey) {

        byte[] result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.get(bsKey);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long setnx(RedisKey2 redisKey2, String key, String value) {

        Jedis jedis = null;

        Long result = 0L;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.setnx(key, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long setnx(RedisKey2 redisKey2, byte[] key, byte[] value) {

        Jedis jedis = null;

        Long result = 0L;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            ;
            result = jedis.setnx(key, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {


        }

        return result;
    }

    public String getSet(RedisKey2 redisKey2, String key, String value) {

        Jedis jedis = null;

        String result = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            ;
            result = jedis.getSet(key, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public byte[] getSet(RedisKey2 redisKey2, byte[] key, byte[] value) {

        Jedis jedis = null;

        byte[] result = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.getSet(key, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public void set(RedisKey2 redisKey2, String key, byte[] buff) {
        byte[] bsKey = SafeEncoder.encode(key);

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            jedis.set(bsKey, buff);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {


        }
    }

    public Long del(RedisKey2 redisKey2, String key) {

        Long result = null;
        byte[] bsKey = SafeEncoder.encode(key);

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.del(bsKey);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long del(RedisKey2 redisKey2, final byte[] key) {

        Long result = null;

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.del(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long del(RedisKey2 redisKey2, final byte[]... keys) {

        Long result = null;

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.del(keys);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public void expire(RedisKey2 redisKey2, String key, int seconds) {

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            jedis.expire(key, seconds);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }
    }

    public void expire(RedisKey2 redisKey2, byte[] keyBytes, int seconds) {

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            jedis.expire(keyBytes, seconds);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }
    }

    public Long expireAt(RedisKey2 redisKey2, final byte[] key, final long unixTime) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.expireAt(key, unixTime);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public  Long expireAt(RedisKey2 redisKey2,String key, long unixTime) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.expireAt(key, unixTime);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public List<String> time(RedisKey2 redisKey2) {

        List<String> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.time();
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long incr(RedisKey2 redisKey2, String key) {
        Jedis jedis = null;
        Long result = null;
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis = getJedis(redisKey2);
            result = jedis.incr(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }
        return result;

    }

    public Map<byte[], byte[]> hgetAll(RedisKey2 redisKey2, byte[] bsKey) {


        Map<byte[], byte[]> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hgetAll(bsKey);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {


        }

        return result;
    }

    public Map<String, String> hgetAll(RedisKey2 redisKey2, String key) {


        Map<String, String> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hgetAll(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {


        }

        return result;
    }

    public Long zadd(RedisKey2 redisKey2, final byte[] key, final Map<byte[], Double> scoreMembers) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zadd(key, scoreMembers);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long zadd(RedisKey2 redisKey2, byte[] key, double score, byte[] member) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zadd(key, score, member);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long zadd(RedisKey2 redisKey2, String key, double score, String member) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zadd(key, score, member);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long zadd(RedisKey2 redisKey2, String key, Map<String, Double> scoreMembers) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zadd(key, scoreMembers);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {
            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {
        }

        return result;
    }


    public Long zrank(RedisKey2 redisKey2, byte[] key, byte[] member) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrank(key, member);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long zcard(RedisKey2 redisKey2, byte[] key) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zcard(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long zcard(RedisKey2 redisKey2, String key) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zcard(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, byte[] key, double min, double max) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, byte[] key, double min, double max,
                                              int offset, int count) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max,
                    offset, count);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, byte[] key, byte[] min, byte[] max) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, byte[] key, byte[] min, byte[] max,
                                              int offset, int count) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max,
                    offset, count);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, String key, double min, double max) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, String key, double min, double max,
                                              int offset, int count) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, String key, String min, String max) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<Tuple> zrangeByScoreWithScores(RedisKey2 redisKey2, String key, String min, String max,
                                              int offset, int count) {

        Set<Tuple> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public Long incrBy(RedisKey2 redisKey2, byte[] key, long integer) {

        Jedis jedis = null;
        Long result = null;
        Stopwatch watch = counters.getTx().begin();
        try {
            jedis = getJedis(redisKey2);

            result = jedis.incrBy(key, integer);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }
        return result;

    }

    public void set(RedisKey2 redisKey2, final byte[] key, final byte[] value) {

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            jedis.set(key, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {


        }
    }

    public void set(RedisKey2 redisKey2, String key, String value) {

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            jedis.set(key, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {


        }
    }
    //


    public String setex(RedisKey2 redisKey2, String key, int seconds, String value) {

        String result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.setex(key, seconds, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;

    }

    public String setex(RedisKey2 redisKey2, byte[] key, int seconds, byte[] value) {

        String result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.setex(key, seconds, value);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;

    }

    public String hmset(RedisKey2 redisKey2, byte[] key, Map<byte[], byte[]> hash) {

        String result = null;

        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hmset(key, hash);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public String hmset(RedisKey2 redisKey2, String key, Map<String, String> hash) {

        String result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hmset(key, hash);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public List<String> hmget(RedisKey2 redisKey2, String key, String... fields) {
        List<String> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hmget(key, fields);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public List<byte[]> hmget(RedisKey2 redisKey2, final byte[] key, final byte[]... fields) {
        List<byte[]> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hmget(key, fields);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public Set<String> smembers(RedisKey2 redisKey2, String key) {
        Set<String> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.smembers(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {
            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Set<byte[]> smembers(RedisKey2 redisKey2, byte[] keyBuff) {
        Set<byte[]> result = null;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.smembers(keyBuff);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {
            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long sadd(RedisKey2 redisKey2, final byte[] key, final byte[]... members) {
        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.sadd(key, members);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {
            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long sadd(RedisKey2 redisKey2, String key, String... members) {
        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.sadd(key, members);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {
            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public Long hlen(RedisKey2 redisKey2, String key) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hlen(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    public Long hlen(RedisKey2 redisKey2, final byte[] key) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.hlen(key);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }


    public Long srem(RedisKey2 redisKey2, String key, String... member) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.srem(key, member);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

    //

    public Long srem(RedisKey2 redisKey2, final byte[] key, final byte[]... member) {

        Long result = -1L;
        Jedis jedis = null;
        Stopwatch watch = counters.getTx().begin();

        try {
            jedis = getJedis(redisKey2);
            result = jedis.srem(key, member);
            watch.end();
            returnJedis2(redisKey2, jedis);
        } catch (Exception e) {

            processEx(jedis, watch, redisKey2, e);
            throw e;
        } finally {

        }

        return result;
    }

}