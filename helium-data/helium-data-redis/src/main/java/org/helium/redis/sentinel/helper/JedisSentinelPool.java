package org.helium.redis.sentinel.helper;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.helium.util.Combo3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.jedis.exceptions.JedisException;
import redis.clients.util.Pool;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JedisSentinelPool extends Pool<Jedis> {

	protected GenericObjectPoolConfig poolConfig;

	protected int timeout = Protocol.DEFAULT_TIMEOUT;

	protected String password;

	protected int database = Protocol.DEFAULT_DATABASE;


	protected static Logger log = LoggerFactory.getLogger(JedisSentinelPool.class);

	public JedisSentinelPool(String masterName, Set<String> sentinels, final GenericObjectPoolConfig poolConfig) {
		this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
	}

	public JedisSentinelPool(String masterName, Set<String> sentinels) {
		this(masterName, sentinels, new GenericObjectPoolConfig(), Protocol.DEFAULT_TIMEOUT, null,
				Protocol.DEFAULT_DATABASE);
	}

	public JedisSentinelPool(String masterName, Set<String> sentinels, String password) {
		this(masterName, sentinels, new GenericObjectPoolConfig(), Protocol.DEFAULT_TIMEOUT, password);
	}

	public JedisSentinelPool(String masterName, Set<String> sentinels, final GenericObjectPoolConfig poolConfig,
							 int timeout, final String password) {
		this(masterName, sentinels, poolConfig, timeout, password, Protocol.DEFAULT_DATABASE);
	}

	public JedisSentinelPool(String masterName, Set<String> sentinels, final GenericObjectPoolConfig poolConfig,
							 final int timeout) {
		this(masterName, sentinels, poolConfig, timeout, null, Protocol.DEFAULT_DATABASE);
	}

	public JedisSentinelPool(String masterName, Set<String> sentinels, final GenericObjectPoolConfig poolConfig,
							 final String password) {
		this(masterName, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, password);
	}

	public JedisSentinelPool(String masterName, Set<String> sentinels, final GenericObjectPoolConfig poolConfig,
							 int timeout, final String password, final int database) {
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		this.database = database;

		refreshMaster(masterName,sentinels);
		SentinelManager.addEntry(masterName, sentinels, this);
	}

	private volatile JedisFactory factory;
	private volatile HostAndPort currentHostMaster;

	@Override
	public void destroy() {

		super.destroy();
	}

	public HostAndPort getCurrentHostMaster() {
		return currentHostMaster;
	}

	public void refreshMaster(String masterName, Set<String> sentinels) {
		HostAndPort master = null;
		boolean sentinelAvailable = false;
		for (String sentinel : sentinels) {
			final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));
			Jedis jedis = null;
			try {
				jedis = new Jedis(hap.getHost(), hap.getPort());
				List<String> masterAddr = jedis.sentinelGetMasterAddrByName(masterName);
				// connected to sentinel...
				sentinelAvailable = true;
				if (masterAddr == null || masterAddr.size() != 2) {
					log.error("Can not get master addr, master name: " + masterName + ". Sentinel: " + hap + ".");
					continue;
				}
				master = toHostAndPort(masterAddr);
//				log.info("Found Redis master at " + master);
				break;
			} catch (JedisConnectionException e) {
				log.warn("Cannot connect to sentinel running @ " + hap + ". Trying next one.");
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		if (master == null) {
			if (sentinelAvailable) {
				currentHostMaster = null;
				// can connect to sentinel, but master name seems to not
				// monitored
				log.error("sentinel is Available, but " + masterName + " seems to be not monitored...");
			} else {
				log.error("All sentinels down, cannot determine where is " + masterName
						+ " master is running...");
			}

		}else{
			initPool(master);
		}


	}

	private void initPool(HostAndPort master) {
		if (master!=null&&!master.equals(currentHostMaster)) {
			currentHostMaster = master;
			if (factory == null) {
				factory = new JedisFactory(master.getHost(), master.getPort(), timeout, password, database);
				initPool(poolConfig, factory);
			} else {
				factory.setHostAndPort(currentHostMaster);
				// although we clear the pool, we still have to check the
				// returned object
				// in getResource, this call only clears idle instances, not
				// borrowed instances
				internalPool.clear();
			}
			log.info("Created JedisPool to master at " + master);
		}
	}

	private HostAndPort initSentinels(Set<String> sentinels, final String masterName) {

		HostAndPort master = null;
		boolean sentinelAvailable = false;

		log.info("Trying to find master from available Sentinels...");

		for (String sentinel : sentinels) {
			final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel.split(":")));

			log.info("Connecting to Sentinel " + hap);

			Jedis jedis = null;
			try {
				jedis = new Jedis(hap.getHost(), hap.getPort());

				List<String> masterAddr = jedis.sentinelGetMasterAddrByName(masterName);

				// connected to sentinel...
				sentinelAvailable = true;

				if (masterAddr == null || masterAddr.size() != 2) {
					log.warn("Can not get master addr, master name: " + masterName + ". Sentinel: " + hap + ".");
					continue;
				}

				master = toHostAndPort(masterAddr);
				log.info("Found Redis master at " + master);
				break;
			} catch (JedisConnectionException e) {
				log.warn("Cannot connect to sentinel running @ " + hap + ". Trying next one.");
			} finally {
				if (jedis != null) {
					jedis.close();
				}
			}
		}

		if (master == null) {
			if (sentinelAvailable) {
				// can connect to sentinel, but master name seems to not
				// monitored
				throw new JedisException("Can connect to sentinel, but " + masterName + " seems to be not monitored...");
			} else {
				throw new JedisConnectionException("All sentinels down, cannot determine where is " + masterName
						+ " master is running...");
			}
		}

		log.info("Redis master running at " + master + ", starting Sentinel listeners...");

		// for (String sentinel : sentinels) {
		// final HostAndPort hap =
		// toHostAndPort(Arrays.asList(sentinel.split(":")));
		// MasterListener masterListener = new MasterListener(masterName,
		// hap.getHost(), hap.getPort());
		// masterListeners.add(masterListener);
		// masterListener.start();
		// }

		return master;
	}

	private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult) {
		String host = getMasterAddrByNameResult.get(0);
		int port = Integer.parseInt(getMasterAddrByNameResult.get(1));

		return new HostAndPort(host, port);
	}

	@Override
	public Jedis getResource() {

		if(currentHostMaster==null)
			return null;
		while (true) {
			Jedis jedis = super.getResource();
			jedis.setDataSource(this);

			// get a reference because it can change concurrently
			final HostAndPort master = currentHostMaster;
			final HostAndPort connection = new HostAndPort(jedis.getClient().getHost(), jedis.getClient().getPort());

			if (master.equals(connection)) {
				// connected to the correct master
				return jedis;
			} else {
				returnBrokenResource(jedis);
			}
		}
	}

	@Override
	public void returnBrokenResource(final Jedis resource) {
		if (resource != null) {
			try {
				returnBrokenResourceObject(resource);
			} catch (Throwable e){
				log.error("returnBrokenResourceObject error", e);
			}

		}
	}
	@Override
	public void returnResource(final Jedis resource) {
		if (resource != null) {
			try {
				resource.resetState();
			} catch (Throwable e){
				log.error("resetState error", e);
			}

			try {
				returnResourceObject(resource);
			} catch (Throwable e){
				log.error("returnResourceObject error", e);
			}

		}
	}

	static class SentinelManager {
		private static Thread masterLoopThread;
		private static ConcurrentLinkedQueue<Combo3<String, Set<String>, JedisSentinelPool>> sentinelGroups;

		static {
			sentinelGroups = new ConcurrentLinkedQueue<Combo3<String, Set<String>, JedisSentinelPool>>();
			masterLoopThread = new Thread(new Runnable() {
				@Override
				public void run() {
					checkSentinel();
				}
			}, "SentinelmasterLoopThread");
			masterLoopThread.setDaemon(true);
			masterLoopThread.start();
		}

		static void addEntry(String masterName, Set<String> sentinels, JedisSentinelPool instance) {
			sentinelGroups.offer(new Combo3<String, Set<String>, JedisSentinelPool>(masterName, sentinels,
					instance));
		}

		private static void checkSentinel() {
			while (true) {
				try {
					for (Combo3<String, Set<String>, JedisSentinelPool> entry : sentinelGroups) {
						entry.getV3().refreshMaster(entry.getV1(), entry.getV2());

					}
					Thread.sleep(1000);
				} catch (Exception e) {
					log.error("loop sentinel error", e);
				}
			}
		}
	}


}
