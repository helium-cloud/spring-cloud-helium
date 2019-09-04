package org.helium.redis.sentinel.pipeline;

import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Response;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IBinaryRedisPipeline {
    Response<Long> append(byte[] key, byte[] value);

    Response<List<byte[]>> blpop(byte[] arg);

    Response<List<byte[]>> brpop(byte[] arg);

    Response<Long> decr(byte[] key);

    Response<Long> decrBy(byte[] key, long integer);

    Response<Long> del(byte[] keys);

    Response<byte[]> echo(byte[] string);

    Response<Boolean> exists(byte[] key);

    Response<Long> expire(byte[] key, int seconds);

    Response<Long> expireAt(byte[] key, long unixTime);

    Response<byte[]> get(byte[] key);

    Response<Boolean> getbit(byte[] key, long offset);

    Response<byte[]> getSet(byte[] key, byte[] value);

    Response<Long> getrange(byte[] key, long startOffset, long endOffset);
    
    Response<byte[]> getrange2(byte[] key, long startOffset, long endOffset);

    Response<Long> hdel(byte[] key, byte[] field);

    Response<Boolean> hexists(byte[] key, byte[] field);

    Response<byte[]> hget(byte[] key, byte[] field);

    Response<Map<byte[], byte[]>> hgetAll(byte[] key);

    Response<Long> hincrBy(byte[] key, byte[] field, long value);

    Response<Set<byte[]>> hkeys(byte[] key);

    Response<Long> hlen(byte[] key);

    Response<List<byte[]>> hmget(byte[] key, byte[]... fields);

    Response<String> hmset(byte[] key, Map<byte[], byte[]> hash);

    Response<Long> hset(byte[] key, byte[] field, byte[] value);

    Response<Long> hsetnx(byte[] key, byte[] field, byte[] value);

    Response<List<byte[]>> hvals(byte[] key);

    Response<Long> incr(byte[] key);

    Response<Long> incrBy(byte[] key, long integer);

    Response<byte[]> lindex(byte[] key, int index);

    Response<Long> linsert(byte[] key, BinaryClient.LIST_POSITION where,
                           byte[] pivot, byte[] value);

    Response<Long> llen(byte[] key);

    Response<byte[]> lpop(byte[] key);



    Response<List<byte[]>> lrange(byte[] key, long start, long end);

    Response<Long> lrem(byte[] key, long count, byte[] value);

    Response<String> lset(byte[] key, long index, byte[] value);

    Response<String> ltrim(byte[] key, long start, long end);

    Response<Long> move(byte[] key, int dbIndex);

    Response<Long> persist(byte[] key);

    Response<byte[]> rpop(byte[] key);

    Response<Long> scard(byte[] key);

    Response<String> set(byte[] key, byte[] value);

    Response<Boolean> setbit(byte[] key, long offset, byte[] value);

    Response<Long> setrange(byte[] key, long offset, byte[] value);

    Response<String> setex(byte[] key, int seconds, byte[] value);

    Response<Long> setnx(byte[] key, byte[] value);

    Response<Long> setrange(String key, long offset, String value);

    Response<Set<byte[]>> smembers(byte[] key);

    Response<Boolean> sismember(byte[] key, byte[] member);

    Response<List<byte[]>> sort(byte[] key);

    Response<List<byte[]>> sort(byte[] key,
                                SortingParams sortingParameters);

    Response<byte[]> spop(byte[] key);

    Response<byte[]> srandmember(byte[] key);

    //wanghongze add 
    Response<Long> sadd(byte[] key, byte[] member);
    
    Response<Long> srem(byte[] key, byte[] member);

    Response<Long> strlen(byte[] key);

    Response<String> substr(byte[] key, int start, int end);

    Response<Long> ttl(byte[] key);

    Response<String> type(byte[] key);

    Response<Long> zadd(byte[] key, double score, byte[] member);

    Response<Long> zcard(byte[] key);

    Response<Double> zincrby(byte[] key, double score, byte[] member);

    Response<Set<byte[]>> zrange(byte[] key, int start, int end);

    Response<Long> zrank(byte[] key, byte[] member);

    Response<Long> zrem(byte[] key, byte[] member);

    Response<Long> zremrangeByRank(byte[] key, int start, int end);

    Response<Set<byte[]>> zrevrange(byte[] key, int start, int end);

    Response<Set<Tuple>> zrevrangeWithScores(byte[] key, int start,
                                             int end);

    Response<Long> zrevrank(byte[] key, byte[] member);

    Response<Double> zscore(byte[] key, byte[] member);
    
    Response<Long> lpush(byte[] key, byte[] value);
    
    Response<Long> rpush(byte[] key, byte[] value);
}

