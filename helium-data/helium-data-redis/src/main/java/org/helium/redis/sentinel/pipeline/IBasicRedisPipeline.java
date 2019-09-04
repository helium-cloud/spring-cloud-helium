package org.helium.redis.sentinel.pipeline;

import redis.clients.jedis.Response;

public interface IBasicRedisPipeline {

    Response<String> bgrewriteaof();

    Response<String> bgsave();

    Response<String> configGet(String pattern);

    Response<String> configSet(String parameter, String value);

    Response<String> configResetStat();

    Response<String> save();

    Response<Long> lastsave();

    Response<String> flushDB();

    Response<String> flushAll();

    Response<String> info();

    Response<Long> dbSize();

    Response<String> shutdown();

    Response<String> ping();

    Response<String> select(int index);
}