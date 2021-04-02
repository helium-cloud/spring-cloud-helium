package org.helium.redis;


import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import com.feinno.superpojo.util.StringUtils;
import org.helium.data.utils.PropertiesLoader;
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
public class RedisClientImpl implements RedisClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(RedisClientImpl.class);

	private int databaseIndex;
	private JedisPool jedisPool;// 切片连接池
	private RedisCounters counters;
	private String auth = null;

	/**
	 * 构造函数，根据Properties配置文件初始化切片池
	 *
	 * @param prop
	 */
	public RedisClientImpl(Properties prop) {
		initPool(prop);
		this.counters = PerformanceCounterFactory.getCounters(RedisCounters.class, "Redis-Client");
	}

	public RedisClientImpl(Properties prop, RedisCounters counters) {
		initPool(prop);
		this.counters = counters;
	}

	/**
	 * 初始化切片池
	 */
	private void initPool(Properties props) {
		JedisPoolConfig config = new JedisPoolConfig();
		PropertiesLoader p = new PropertiesLoader(props);

		config.setBlockWhenExhausted(p.getBoolean("blockWhenExhausted", true));
		config.setMinIdle(p.getInt("minIdle", 1));
		config.setMaxIdle(p.getInt("maxIdle", 3));
		config.setMaxTotal(p.getInt("maxTotal", 10));
		config.setTestWhileIdle(p.getBoolean("testWhileIdle", true));
		config.setTestOnBorrow(p.getBoolean("testOnBorrow", false));
		config.setTestOnReturn(p.getBoolean("testOnReturn", false));
		config.setTimeBetweenEvictionRunsMillis(p.getInt("timeBetweenEvictionRunsMillis", 60000));

		databaseIndex = p.getInt("database", 0);
		String host = p.getString("host");
		int port = p.getInt("port");

		jedisPool = new JedisPool(config, host, port);

		if (!StringUtils.isNullOrEmpty(p.getString("auth"))){
			auth = p.getString("auth");
		}

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.append(key, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}


	@Override
	public String ping(String key) {
		throw new UnsupportedOperationException("Jedis Unsupported ping ");
	}

	@Override
	public Long bitcount(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.bitcount(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long bitcount(String key, long start, long end) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.bitcount(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public List<String> blpop(int timeout, String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.blpop(timeout, key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	/**
	 * 它是 LPOP 命令的阻塞版本
	 */
	@Override
	public List<String> blpop(String arg) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.blpop(arg);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
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
			watch.end();
			return list;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.brpop(timeout, key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	/**
	 * 它是 RPOP 命令的阻塞版本
	 */
	@Override
	public List<String> brpop(String arg) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.brpop(arg);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
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
			watch.end();
			return list;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.decr(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.decrBy(key, integer);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 删除key对应的值
	 */
	@Override
	public Long del(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.del(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public String echo(String string) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.echo(string);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Boolean tmp = jedis.exists(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.expire(key, expire);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Long expire(byte[] key, int expire) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.expire(key, expire);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.expireAt(key, unixTime);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.get(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public byte[] get(byte[] key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] tmp = jedis.get(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] value = jedis.get(bsKey);
			if (value == null) {
				watch.end();
				return null;
			}
			// 反序列化
			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(value, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 返回在指定Offset上BIT的值，0或1。如果Offset超过string value的长度，该命令将返回0，所以对于空字符串始终返回0。
	 */
	@Override
	public Boolean getbit(String key, long offset) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Boolean tmp = jedis.getbit(key, offset);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 获取Jedis实例
	 *
	 * @return
	 */
	public Jedis getJedis() {
		counters.getQps().increase();
		Jedis j = jedisPool.getResource();
		if (j != null && auth != null){
			j.auth(auth);
		}
		if (databaseIndex != 0) {
			j.select(databaseIndex);
		}
		return j;
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.getrange(key, startOffset, endOffset);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.getSet(key, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsOld = jedis.getSet(bsKey, bs);

			if (bsOld == null) {
				watch.end();
				return null;
			}

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsOld, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hdel(key, fields);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Boolean tmp = jedis.hexists(key, field);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.hget(key, field);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bs = jedis.hget(bsKey, bsField);

			if (bs == null) {
				watch.end();
				return null;
			}

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bs, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bs = jedis.hget(bsKey, bsField);

			if (bs == null) {
				watch.end();
				return null;
			}
			//returnJedis(jedis);
			watch.end();
			return bs;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Map<String, String> tmp = jedis.hgetAll(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 返回hash的所有filed和value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
	 *
	 * @param key
	 * @return
	 */
	public Map<String, byte[]> hgetAllPojo(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Map<byte[], byte[]> map = jedis.hgetAll(bsKey);

			if (map == null) {
				watch.end();
				return null;
			}
			//returnJedis(jedis);

			Map<String, byte[]> tmp = new HashMap<>();
			for (byte[] bsField : map.keySet())
				tmp.put(SafeEncoder.encode(bsField), map.get(bsField));
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hincrBy(key, field, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.hkeys(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hlen(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.hmget(key, fields);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			List<byte[]> tmp = jedis.hmget(bsKey, SafeEncoder.encodeMany(fields));
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.hmset(key, hash);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Map<byte[], byte[]> map = new HashMap<>();
			for (String field : hash.keySet()) {
				byte[] bsField = SafeEncoder.encode(field);
				E e = hash.get(field);
				byte[] bs = e.toPbByteArray();
				map.put(bsField, bs);
			}

			String tmp = jedis.hmset(bsKey, map);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	@Deprecated
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			ScanResult<Entry<String, String>> tmp = jedis.hscan(key, cursor);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			ScanResult<Entry<String, String>> tmp = jedis.hscan(key, cursor);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hset(key, field, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hset(bsKey, bsField, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hset(bsKey, bsField, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hsetnx(key, field, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.hsetnx(bsKey, bsField, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.hvals(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 返回hash的所有value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
	 *
	 * @param key
	 * @return
	 */
	public Collection<byte[]> hvalsPojo(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			Collection<byte[]> tmp = jedis.hvals(bsKey);
			watch.end();
			return tmp;

		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.incr(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.incrBy(key, integer);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}


	/**
	 * 从左侧取第index个String
	 */
	@Override
	public String lindex(String key, long index) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.lindex(key, index);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = jedis.lindex(bsKey, index);

			if (bsValue == null) {
				watch.end();
				return null;
			}

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsValue, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 在指定的元素pivot前面或是后面插入一个元素
	 */
	@Override
	public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.linsert(key, where, pivot, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = value.toPbByteArray();
			byte[] bsPivot = pivot.toPbByteArray();
			Long tmp = jedis.linsert(bsKey, where, bsPivot, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.llen(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.lpop(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = jedis.lpop(bsKey);

			if (bsValue == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);
			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsValue, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.lpush(key, strings);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
	 *
	 * @param key
	 * @return
	 */
	public Long lpush(String key, SuperPojo pojo) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bs = pojo.toPbByteArray();
			Long tmp = jedis.lpush(bsKey, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
	 */
	@Override
	public Long lpushx(String key, String... string) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.lpushx(key, string);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = pojo.toPbByteArray();
			Long tmp = jedis.lpushx(bsKey, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.lrange(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			List<byte[]> values = jedis.lrange(bsKey, start, end);

			if (values == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);
			List<E> list = new ArrayList<E>(values.size());
			for (byte[] value : values) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(value, e);
				list.add(e);
			}
			watch.end();
			return list;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Long lrem(String key, long count, String value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.lrem(key, count, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	public Long lrem(String key, long count, SuperPojo value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = value.toPbByteArray();
			Long tmp = jedis.lrem(bsKey, count, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.lset(key, index, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = value.toPbByteArray();

			String tmp = jedis.lset(bsKey, index, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.ltrim(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.move(key, dbIndex);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.persist(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.pfadd(key, elements);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	public Long pfadd(String key, SuperPojo element) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = element.toPbByteArray();
			Long tmp = jedis.pfadd(bsKey, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * Available since 2.8.9. pfadd在key下面的不重复elements的数量
	 */
	@Override
	public long pfcount(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			long tmp = jedis.pfcount(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 归还Jedis实例
	 *
	 * @param jedis
	 */
	public void returnJedis(Jedis jedis) {
		if (jedis != null) {
			jedisPool.returnResource(jedis);
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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.rpop(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = jedis.rpop(bsKey);

			if (bsValue == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsValue, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.rpush(key, strings);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bs = pojo.toPbByteArray();

			Long tmp = jedis.rpush(bsKey, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
	 */
	@Override
	public Long rpushx(String key, String... string) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.rpushx(key, string);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = pojo.toPbByteArray();
			Long tmp = jedis.rpushx(bsKey, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.sadd(key, members);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = member.toPbByteArray();
			Long tmp = jedis.sadd(bsKey, bsValue);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.scard(s);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.set(key, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public String set(String key, String value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.set(key, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.set(key, value, nxxx, expx, time);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.set(bsKey, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Boolean tmp = jedis.setbit(key, offset, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Boolean tmp = jedis.setbit(key, offset, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.setex(key, seconds, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.setex(bsKey, seconds, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.setnx(key, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.setnx(bsKey, bs);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 从第offset个开始替换
	 */
	@Override
	public Long setrange(String key, long offset, String value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.setrange(key, offset, value);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Boolean tmp = jedis.sismember(key, member);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			Boolean tmp = jedis.sismember(bsKey, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.smembers(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.smembers(bsKey);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);
			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public List<String> sort(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.sort(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	public <E extends SuperPojo> List<E> sort(String key, Class<E> clazz) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			List<byte[]> value = jedis.sort(bsKey);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			List<E> list = new ArrayList<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				list.add(e);
			}
			watch.end();
			return list;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.sort(key, sortingParameters);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	public <E extends SuperPojo> List<E> sort(String key, SortingParams sortingParameters, Class<E> clazz) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			List<byte[]> value = jedis.sort(bsKey, sortingParameters);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			List<E> list = new ArrayList<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				list.add(e);
			}
			watch.end();
			return list;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.spop(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = jedis.spop(bsKey);

			if (bsMember == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsMember, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.srandmember(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = jedis.srandmember(bsKey);

			if (bsMember == null) {
				watch.end();
				return null;
			}

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsMember, tmp);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public List<String> srandmember(String key, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.srandmember(key, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.srem(key, members);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			Long tmp = jedis.srem(bsKey, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	@Deprecated
	public ScanResult<String> sscan(String key, int cursor) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			ScanResult<String> tmp = jedis.sscan(key, cursor);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			ScanResult<String> tmp = jedis.sscan(key, cursor);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long strlen(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.strlen(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.substr(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.ttl(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Long ttl(byte[] key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.ttl(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String tmp = jedis.type(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zadd(key, score, member);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();

			Long tmp = jedis.zadd(bsKey, score, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zadd(key, scoreMembers);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zcard(key);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zcount(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long zcount(String key, String min, String max) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zcount(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Double tmp = jedis.zincrby(key, score, member);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			Double tmp = jedis.zincrby(bsKey, score, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zlexcount(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrange(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.zrange(bsKey, start, end);
			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrangeByLex(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrangeByLex(key, min, max, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrangeByScore(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Set<byte[]> value = jedis.zrangeByScore(bsKey, min, max);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrangeByScore(key, min, max, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Set<byte[]> value = jedis.zrangeByScore(bsKey, min, max, offset, count);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMin = min.toPbByteArray();
			byte[] bsMax = max.toPbByteArray();
			Set<byte[]> value = jedis.zrangeByScore(bsKey, bsMin, bsMax);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMax = max.toPbByteArray();
			byte[] bsMin = min.toPbByteArray();

			Set<byte[]> value = jedis.zrangeByScore(bsKey, bsMin, bsMax, offset, count);

			if (value == null) {
				watch.end();
				return null;
			}
			returnJedis(jedis);

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrangeByScore(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrangeByScore(key, min, max, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrangeByScoreWithScores(key, min, max, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrangeWithScores(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zrank(key, member);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			Long tmp = jedis.zrank(bsKey, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zrem(key, members);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
	 *
	 * @param key
	 * @return
	 */
	public Long zrem(String key, SuperPojo member) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			Long tmp = jedis.zrem(bsKey, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zremrangeByLex(key, min, max);
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zremrangeByRank(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zremrangeByScore(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zremrangeByScore(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrevrange(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Set<byte[]> value = jedis.zrevrange(bsKey, start, end);

			if (value == null) {
				watch.end();
				return null;
			}
			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrevrangeByScore(key, max, min);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, max, min);

			if (value == null) {
				watch.end();
				return null;
			}

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrevrangeByScore(key, max, min, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, max, min, offset, count);

			if (value == null) {
				watch.end();
				return null;
			}

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMax = max.toPbByteArray();
			byte[] bsMin = min.toPbByteArray();
			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, bsMax, bsMin);

			if (value == null) {
				watch.end();
				return null;
			}

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMax = max.toPbByteArray();
			byte[] bsMin = min.toPbByteArray();

			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, bsMax, bsMin, offset, count);

			if (value == null) {
				watch.end();
				return null;
			}

			Set<E> set = new HashSet<E>();
			for (byte[] bs : value) {
				E e = clazz.newInstance();
				SuperPojoManager.parsePbFrom(bs, e);
				set.add(e);
			}
			watch.end();
			return set;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrevrangeByScore(key, max, min);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<String> tmp = jedis.zrevrangeByScore(key, max, min, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Set<Tuple> tmp = jedis.zrevrangeWithScores(key, start, end);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Long tmp = jedis.zrevrank(key, member);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			Long tmp = jedis.zrevrank(bsKey, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	@Deprecated
	public ScanResult<Tuple> zscan(String key, int cursor) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			ScanResult<Tuple> tmp = jedis.zscan(key, cursor);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			ScanResult<Tuple> tmp = jedis.zscan(key, cursor);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Double tmp = jedis.zscore(key, member);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();

			Double tmp = jedis.zscore(bsKey, bsMember);
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}

	}

	/**
	 * 获取redis时间
	 *
	 * @param key
	 * @return
	 */
	public List<String> time(String key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<String> tmp = jedis.time();
			watch.end();
			return tmp;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);

		}
	}

	@Override
	public Closeable<Pipeline> getPipeline(String key) {
		Jedis jedis = getJedis();
		Pipeline pipeline = jedis.pipelined();
		return new org.helium.framework.utils.Closeable<Pipeline>() {
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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

		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] values = jedis.get(key.getBytes());
			watch.end();
			if (values == null) {
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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			byte[] values = jedis.get(key.getBytes());
			watch.end();
			if (values == null) {
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.psubscribe(pubSub, "*");
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public void sadd(byte[] key, byte[] value) {
		Jedis jedis = getJedis();
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
	public void sadd(String key, String value) {
		Jedis jedis = getJedis();
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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.smembers(key);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public void srem(byte[] key, byte[]... value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.srem(key, value);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public boolean sismember(byte[] setKey, byte[] value) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.sismember(setKey, value);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public void hmset(byte[] key, Map<byte[], byte[]> map) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.hmset(key, map);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public Map<byte[], byte[]> hmget(byte[] key) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.hgetAll(key);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public byte[] hmgetValue(byte[] key, byte[] keyword) {

		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<byte[]> value = jedis.hmget(keyword, keyword);
			if (value != null && value.size() > 0) {
				return value.get(0);
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
	public List<byte[]> hmget(byte[] key, byte[][] fields) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			List<byte[]> value = jedis.hmget(key, fields);
			return value;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(jedis);
		}
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			Object result = jedis.eval(script, keys, args);
			if (result != null) {
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
		Jedis jedis = getJedis();
		Stopwatch watch = counters.getTx().begin();
		try {
			String result = jedis.set(key, value, nxxx, expx, expire);
			if ("OK".equals(result)) {
				return true;
			} else {
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
		Jedis jedis = getJedis();
		Pipeline pipeline = jedis.pipelined();
		try {
			func.accept(pipeline);
		} finally {
			pipeline.sync();
			returnJedis(jedis);
		}
	}
}
