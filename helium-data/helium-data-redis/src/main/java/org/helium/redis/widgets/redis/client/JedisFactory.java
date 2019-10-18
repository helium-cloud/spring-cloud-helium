package org.helium.redis.widgets.redis.client;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by yibo on 2017-6-9.
 */
class JedisFactory implements PooledObjectFactory<Jedis> {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisFactory.class);

    private final AtomicReference<HostAndPort> hostAndPort = new AtomicReference<HostAndPort>();
    private final int timeout;
    private final String password;
    private final int database;
    private final String clientName;
    private RedisCounters connMakerCounter;

    public JedisFactory(final String host, final int port, final int timeout,
                        final String password, final int database) {
        this(host, port, timeout, password, database, null);
    }

    public JedisFactory(final String host, final int port, final int timeout,
                        final String password, final int database, final String clientName) {
        super();
        this.hostAndPort.set(new HostAndPort(host, port));
        this.timeout = timeout;
        this.password = password;
        this.database = database;
        this.clientName = clientName;
        this.connMakerCounter = PerformanceCounterFactory.getCounters(RedisCounters.class, "RedisSentinel-connMake");
    }

    public void setHostAndPort(final HostAndPort hostAndPort) {
        this.hostAndPort.set(hostAndPort);
    }

    @Override
    public void activateObject(PooledObject<Jedis> pooledJedis)
            throws Exception {
        final BinaryJedis jedis = pooledJedis.getObject();
        if (jedis.getDB() != database) {
            jedis.select(database);
        }

    }

    @Override
    public void destroyObject(PooledObject<Jedis> pooledJedis) throws Exception {

        //todo counter

        final BinaryJedis jedis = pooledJedis.getObject();
        if (jedis.isConnected()) {
            try {
                try {
                    jedis.quit();
                } catch (Exception e) {
                }
                jedis.disconnect();
            } catch (Exception e) {

            }
        }

    }

    @Override
    public PooledObject<Jedis> makeObject() throws Exception {
        final HostAndPort hostAndPort = this.hostAndPort.get();
        final Jedis jedis = new Jedis(hostAndPort.getHost(), hostAndPort.getPort(), this.timeout);


        Stopwatch stopwatch= this.connMakerCounter.getTx().begin();
        try {

            jedis.connect();
            if (null != this.password) {
                jedis.auth(this.password);
            }
            if (database != 0) {
                jedis.select(database);
            }
            if (clientName != null) {
                jedis.clientSetname(clientName);
            }

            stopwatch.end();

        } catch (Exception ex) {
            stopwatch.fail(ex);

            LOGGER.error("makeObject->jedis connect cost ms:" + stopwatch.getMillseconds() + " hostAndPort:" + hostAndPort);
            throw ex;
        }

        return new DefaultPooledObject<Jedis>(jedis);
    }

    @Override
    public void passivateObject(PooledObject<Jedis> pooledJedis)
            throws Exception {
        // TODO maybe should select db 0? Not sure right now.
    }

    @Override
    public boolean validateObject(PooledObject<Jedis> pooledJedis) {
        final BinaryJedis jedis = pooledJedis.getObject();
        try {
            HostAndPort hostAndPort = this.hostAndPort.get();

            String connectionHost = jedis.getClient().getHost();
            int connectionPort = jedis.getClient().getPort();

            return hostAndPort.getHost().equals(connectionHost) && hostAndPort.getPort() == connectionPort &&
                    jedis.isConnected() && jedis.ping().equals("PONG");
        } catch (final Exception e) {
            return false;
        }
    }
}