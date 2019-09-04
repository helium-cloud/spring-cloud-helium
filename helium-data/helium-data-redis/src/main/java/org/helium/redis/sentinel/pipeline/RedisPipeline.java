package org.helium.redis.sentinel.pipeline;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.perfmon.spi.TransactionCounter;
import org.helium.redis.sentinel.RedisKey;
import org.helium.redis.sentinel.RedisRouteDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Builder;
import redis.clients.jedis.Client;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.Pool;

import java.util.ArrayList;
import java.util.List;


public class RedisPipeline extends IMultiKeyPipelineBase {

    static {
        ReidsPipelineCounterCategory c =
                PerformanceCounterFactory.getCounters(ReidsPipelineCounterCategory.class, "redis");
        redisCounter = (TransactionCounter) c.getRedisCmdCounter();
    }

    private static TransactionCounter redisCounter;
    private static Logger LOGGER = LoggerFactory.getLogger(RedisPipeline.class);
    private String cacheName;

    private RedisKey redisKey;

    private Jedis jedis = null;

    private Pool<Jedis> jedisPool = null;


    public RedisPipeline(String rouleName, RedisKey redisKey) throws Exception {
        this.cacheName = rouleName;
        this.redisKey = redisKey;
        try {
            this.jedisPool = RedisRouteDirector.getInstance().getRouteProvider(rouleName).resolvePool(redisKey);
        } catch (RuntimeException ex) {
            LOGGER.error(String.format("NOT FIND RoleName:%s", rouleName));
            throw ex;
        }
        try {
            this.jedis = jedisPool.getResource();
        } catch (Exception ex) {
            LOGGER.error(String.format("get jedis resource error,value:%s", rouleName), ex);
            throw ex;
        }
        this.setClient(jedis.getClient());
    }


    public String getCacheName() {
        return cacheName;
    }


    public RedisKey getRedisKey() {
        return redisKey;
    }


    public Jedis getJedis() {
        return jedis;
    }

    private MultiResponseBuilder currentMulti;

    private class MultiResponseBuilder extends Builder<List<Object>> {
        private List<Response<?>> responses = new ArrayList<Response<?>>();


        @Override
        public List<Object> build(Object data) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) data;
            List<Object> values = new ArrayList<Object>();

            if (list.size() != responses.size()) {
                throw new JedisDataException("Expected data size " + responses.size() + " but was "
                        + list.size());
            }

            for (int i = 0; i < list.size(); i++) {
                Response<?> response = responses.get(i);
                response.set(list.get(i));
                values.add(response.get());
            }
            return values;
        }


        public void addResponse(Response<?> response) {
            responses.add(response);
        }
    }


    @Override
    protected <T> Response<T> getResponse(Builder<T> builder) {
        if (currentMulti != null) {
            super.getResponse(BuilderFactory.STRING); // Expected QUEUED

            Response<T> lr = new Response<T>(builder);
            currentMulti.addResponse(lr);
            return lr;
        } else {
            return super.getResponse(builder);
        }
    }


    public void setClient(Client client) {
        this.client = client;
    }


    @Override
    protected Client getClient(byte[] key) {
        return client;
    }


    @Override
    protected Client getClient(String key) {
        return client;
    }


    /**
     * Syncronize pipeline by reading all responses. This operation close the
     * pipeline. In order to get return values from pipelined commands, capture
     * the different Response<?> of the commands you execute.
     */
    public void sync() throws Exception {

        List<Object> unformatted = null;
        try {
            unformatted = client.getAll();
        } catch (Exception ex) {
            LOGGER.error(String.format("sync error,host:%s,port:%d", client.getHost(), client.getPort()));
            throw ex;
        }

        for (Object o : unformatted) {
            Stopwatch watch = redisCounter.begin();
            generateResponse(o);
            watch.end();
        }

    }


    public Response<String> discard() {
        client.discard();
        return getResponse(BuilderFactory.STRING);
    }


    public Response<List<Object>> exec() {
        client.exec();
        Response<List<Object>> response = super.getResponse(currentMulti);
        currentMulti = null;
        return response;
    }


    public Response<String> multi() {
        client.multi();
        Response<String> response = getResponse(BuilderFactory.STRING); // Expecting
        // OK
        currentMulti = new MultiResponseBuilder();
        return response;
    }


    public void close() {
        if (jedisPool != null) {
            if (getJedis() != null) {
                try {
                    jedisPool.returnResource(getJedis());
                } catch (Exception e) {
                    jedisPool.returnBrokenResource(getJedis());
                }
            }
        }
    }


    public Response<Long> lpush(String key, String value) {
        getClient(key).lpush(key, value);
        return getResponse(BuilderFactory.LONG);
    }


    public Response<Long> lpush(byte[] key, byte[] value) {
        getClient(key).lpush(key, value);
        return getResponse(BuilderFactory.LONG);
    }


    public Response<Long> rpush(String key, String value) {
        getClient(key).rpush(key, value);
        return getResponse(BuilderFactory.LONG);
    }


    public Response<Long> rpush(byte[] key, byte[] value) {
        getClient(key).rpush(key, value);
        return getResponse(BuilderFactory.LONG);
    }
}
