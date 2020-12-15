package org.helium.redis;


import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.SuperPojoManager;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.framework.utils.Closeable;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.redis.sentinel.RedisKey;
import org.helium.redis.sentinel.RedisRouteDirector;
import org.helium.redis.sentinel.RedisSentinelsCfg;
import org.helium.redis.sentinel.router.provider.IRouteProvider;
import org.helium.redis.spi.RedisCounters;
import org.helium.redis.spi.RedisLoader;
import org.helium.redis.utils.ByteArrayUtils;
import redis.clients.jedis.*;
import redis.clients.util.Pool;
import redis.clients.util.SafeEncoder;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * Redis 的客户端类
 */
@FieldLoaderType(loaderType = RedisLoader.class)
public class RedisSentinelClientImpl implements RedisClient {

	private IRouteProvider<RedisSentinelsCfg, Pool<Jedis>> jedisPool;// 切片连接池
	private RedisCounters counters;

	/**
	 * 构造函数，根据Properties配置文件初始化切片池
	 *
	 * @param redisRoleName roleName
	 * @param prop          配置内容
	 */
	public RedisSentinelClientImpl(String redisRoleName, Properties prop) {
		initialPool(redisRoleName, prop);
		this.counters = PerformanceCounterFactory.getCounters(RedisCounters.class, "Redis-Client");
	}

	public RedisSentinelClientImpl(String redisRoleName, Properties prop, RedisCounters counters) {
		initialPool(redisRoleName, prop);
		this.counters = counters;
	}

	public RedisSentinelClientImpl(String redisRoleName, List<RedisSentinelsCfg> configs) {
		initialPool(redisRoleName, configs);
		this.counters = PerformanceCounterFactory.getCounters(RedisCounters.class, "RedisSentinel-Client");
	}

	/**
	 * 初始化切片池
	 */
	private void initialPool(String redisRoleName, Properties prop) {
		jedisPool = RedisRouteDirector.getInstance().initRouteProvider(redisRoleName, prop);
	}

	private void initialPool(String redisRoleName, List<RedisSentinelsCfg> configs) {
		jedisPool = RedisRouteDirector.getInstance().initRouteProvider(redisRoleName, configs);
	}

	/**
	 * 给指定key的字符串值追加value,返回新字符串值的长度。
	 *
	 * @param key   key
	 * @param value field
	 * @return result long
	 */
	@Override
	public Long append(String key, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.append(key, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public String ping(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.ping();
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Long bitcount(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.bitcount(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long bitcount(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.bitcount(key, start, end);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public List<String> blpop(int timeout, String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.blpop(timeout, key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 它是 LPOP 命令的阻塞版本
	 */
	@Override
	public List<String> blpop(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.blpop(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * LPOP的阻塞版本
	 *
	 * @param key   key
	 * @param clazz clazz
	 * @return result
	 */
	public <E extends SuperPojo> List<E> blpop(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsArg = SafeEncoder.encode(key);
			List<byte[]> value = jedis.blpop(bsArg);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public List<String> brpop(int timeout, String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.brpop(timeout, key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 它是 RPOP 命令的阻塞版本
	 */
	@Override
	public List<String> brpop(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.brpop(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * RPOP的阻塞版本
	 *
	 * @param key   key
	 * @param clazz clazz
	 * @return result
	 */
	public <E extends SuperPojo> List<E> brpop(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsArg = SafeEncoder.encode(key);

			List<byte[]> value = jedis.brpop(bsArg);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 但是做的是减减操作，decr一个不存在key，则设置key为-1
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long decr(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.decr(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同decr，减指定值。
	 *
	 * @param key     key
	 * @param integer int
	 * @return result
	 */
	@Override
	public Long decrBy(String key, long integer) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.decrBy(key, integer);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 删除key对应的值
	 */
	@Override
	public Long del(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.del(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public String echo(String string) {
		Jedis jedis = getJedis(string);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.echo(string);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(string, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 是否存在key
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Boolean exists(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.exists(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置超时时间，单位秒；返回1成功，0表示key已经设置过过期时间或者不存在
	 *
	 * @param key    key
	 * @param expire
	 * @return result
	 */
	@Override
	public Long expire(String key, int expire) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.expire(key, expire);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Long expire(byte[] key, int expire) {
		Jedis jedis = getJedis(new String(key));
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.expire(key, expire);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(new String(key), jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * EXPIREAT命令接受的时间参数是UNIX时间戳(unix timestamp)。
	 *
	 * @param key      key
	 * @param unixTime
	 * @return result
	 */
	@Override
	public Long expireAt(String key, long unixTime) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.expireAt(key, unixTime);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取key对应的value
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public String get(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.get(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public byte[] get(byte[] key) {
		Jedis jedis = getJedis(new String(key));
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.get(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(new String(key), jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取key对应的value
	 *
	 * @param key key
	 * @return result
	 */
	public <E extends SuperPojo> E get(String key, Class<E> clazz) {
		byte[] bsKey = SafeEncoder.encode(key);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] value = jedis.get(bsKey);
			returnJedis(key, jedis);
			jedis = null;
			if (value == null)
				return null;
			// 反序列化
			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(value, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回在指定Offset上BIT的值，0或1。如果Offset超过string value的长度，该命令将返回0，所以对于空字符串始终返回0。
	 */
	@Override
	public Boolean getbit(String key, long offset) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.getbit(key, offset);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取Jedis实例
	 *
	 * @return result
	 */
	public Jedis getJedis(String key) {
		counters.getQps().increase();
		return jedisPool.resolvePool(new RedisKey(key)).getResource();
	}

	@Override
	public String getrange(String key, long startOffset, long endOffset) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.getrange(key, startOffset, endOffset);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 原子的设置key的值，并返回key的旧值。如果key不存在返回nil
	 *
	 * @param key   key
	 * @param value field
	 * @return result
	 */
	@Override
	public String getSet(String key, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.getSet(key, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 原子的设置key的值，并返回key的旧值。如果key不存在返回null
	 *
	 * @param key   key
	 * @param value field
	 * @return result
	 */
	public <E extends SuperPojo> E getSet(String key, SuperPojo value, Class<E> clazz) {
		byte[] bs = value.toPbByteArray();
		byte[] bsKey = SafeEncoder.encode(key);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsOld = jedis.getSet(bsKey, bs);
			returnJedis(key, jedis);
			jedis = null;
			if (bsOld == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsOld, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 删除指定的hash field
	 *
	 * @param key    key
	 * @param fields fields
	 * @return result
	 */
	@Override
	public Long hdel(String key, String... fields) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hdel(key, fields);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 测试指定field是否存在
	 *
	 * @param key   key
	 * @param field field
	 * @return result
	 */
	@Override
	public Boolean hexists(String key, String field) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hexists(key, field);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取指定的hash field
	 *
	 * @param key   key
	 * @param field field
	 * @return result
	 */
	@Override
	public String hget(String key, String field) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hget(key, field);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取指定的hash field
	 *
	 * @param key   key
	 * @param field field
	 * @return result
	 */
	public <E extends SuperPojo> E hget(String key, String field, Class<E> clazz) {
		byte[] bsKey = SafeEncoder.encode(key);
		byte[] bsField = SafeEncoder.encode(field);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bs = jedis.hget(bsKey, bsField);
			returnJedis(key, jedis);
			jedis = null;
			if (bs == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bs, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取指定的hash field
	 *
	 * @param key   key
	 * @param field field
	 * @return result
	 */
	public byte[] hgetPojo(String key, String field) {
		byte[] bsKey = SafeEncoder.encode(key);
		byte[] bsField = SafeEncoder.encode(field);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bs = jedis.hget(bsKey, bsField);

			if (bs == null)
				return null;

			returnJedis(key, jedis);
			jedis = null;
			return bs;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回hash的所有filed和value
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Map<String, String> hgetAll(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hgetAll(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回hash的所有filed和value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
	 *
	 * @param key key
	 * @return result
	 */
	public Map<String, byte[]> hgetAllPojo(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 将指定的hash filed 加上给定值
	 *
	 * @param key   key
	 * @param field field
	 * @param value field
	 * @return result
	 */
	@Override
	public Long hincrBy(String key, String field, long value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hincrBy(key, field, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回hash的所有field
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Set<String> hkeys(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hkeys(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回指定hash的field数量
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long hlen(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hlen(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取全部指定的hash filed
	 *
	 * @param key    key
	 * @param fields fields
	 * @return result
	 */
	@Override
	public List<String> hmget(String key, String... fields) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hmget(key, fields);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取全部指定的hash filed
	 *
	 * @param key    key
	 * @param fields fields
	 * @return result
	 */
	public List<byte[]> hmgetPojo(String key, String... fields) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			return jedis.hmget(bsKey, SafeEncoder.encodeMany(fields));

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public String hmset(String key, Map<String, String> hash) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hmset(key, hash);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置Map
	 *
	 * @param key  key
	 * @param hash
	 * @return result
	 */
	public <E extends SuperPojo> String hmsetPojo(String key, Map<String, E> hash) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Map<byte[], byte[]> map = new HashMap<>();
			for (String field : hash.keySet()) {
				byte[] bsField = SafeEncoder.encode(field);
				E e = hash.get(field);
				byte[] bs = e.toPbByteArray();
				map.put(bsField, bs);
			}
			return jedis.hmset(bsKey, map);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	@Deprecated
	public ScanResult<Entry<String, String>> hscan(String key, int cursor) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hscan(key, cursor);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String key, String cursor) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hscan(key, cursor);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置hash field为指定值，如果key不存在，则先创建
	 *
	 * @param key   key
	 * @param field field field
	 * @param value field value
	 * @return result
	 */
	@Override
	public Long hset(String key, String field, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hset(key, field, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置hash field为指定值，如果key不存在，则先创建
	 *
	 * @param key   key
	 * @param field field
	 * @param value field
	 * @return result
	 */
	public Long hset(String key, String field, SuperPojo value) {
		byte[] bs = value.toPbByteArray();
		byte[] bsKey = SafeEncoder.encode(key);
		byte[] bsField = SafeEncoder.encode(field);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hset(bsKey, bsField, bs);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置hash field为指定值，如果key不存在，则先创建
	 *
	 * @param key   key
	 * @param field field
	 * @param bs
	 * @return result
	 */
	public Long hset(String key, String field, byte[] bs) {
		byte[] bsKey = SafeEncoder.encode(key);
		byte[] bsField = SafeEncoder.encode(field);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hset(bsKey, bsField, bs);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置hash field为指定值，nx:not exist ,Key不存在的情况下才可以设置
	 *
	 * @param key   key
	 * @param field field
	 * @param value field
	 * @return result
	 */
	@Override
	public Long hsetnx(String key, String field, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hsetnx(key, field, value);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * not exist key 和 field的情况下才能设置
	 *
	 * @param key   key
	 * @param field field
	 * @param value field
	 * @return result
	 */
	public Long hsetnx(String key, String field, SuperPojo value) {
		byte[] bsKey = SafeEncoder.encode(key);
		byte[] bsField = SafeEncoder.encode(field);
		byte[] bs = value.toPbByteArray();

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hsetnx(bsKey, bsField, bs);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回hash的所有value
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public List<String> hvals(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.hvals(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回hash的所有value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
	 *
	 * @param key key
	 * @return result
	 */
	public Collection<byte[]> hvalsPojo(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			return jedis.hvals(bsKey);


		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 对key的值做加加操作,并返回新的值。注意incr一个不是int的value会返回错误，incr一个不存在的key，则设置key为1
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long incr(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.incr(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同incr，加指定值。
	 *
	 * @param key     key
	 * @param integer int
	 * @return result
	 */
	@Override
	public Long incrBy(String key, long integer) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.incrBy(key, integer);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}


	/**
	 * 从左侧取第index个String
	 */
	@Override
	public String lindex(String key, long index) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lindex(key, index);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * List 从左侧数index获得元素
	 *
	 * @param key   key
	 * @param index
	 * @param clazz clazz
	 * @return result
	 */
	public <E extends SuperPojo> E lindex(String key, long index, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = jedis.lindex(bsKey, index);
			returnJedis(key, jedis);
			jedis = null;
			if (bsValue == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsValue, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 在指定的元素pivot前面或是后面插入一个元素
	 */
	@Override
	public Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.linsert(key, where, pivot, value);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 在指定pivot的前面或者后面插入
	 *
	 * @param key   key
	 * @param where
	 * @param pivot
	 * @param value field
	 * @return result
	 */
	public Long linsert(String key, BinaryClient.LIST_POSITION where, SuperPojo pivot, SuperPojo value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = value.toPbByteArray();
			byte[] bsPivot = pivot.toPbByteArray();
			return jedis.linsert(bsKey, where, bsPivot, bsValue);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回key对应list的长度，key不存在返回0,如果key对应类型不是list返回错
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long llen(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.llen(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 从list的头部删除元素，并返回删除元素。如果key对应list不存在或者是空返回nil，如果key对应值不是list返回错误
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public String lpop(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lpop(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 从list的头部删除元素，并返回删除元素。如果key对应list不存在或者是空返回null，如果key对应值不是list返回错误
	 *
	 * @param key key
	 * @return result
	 */
	public <E extends SuperPojo> E lpop(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = jedis.lpop(bsKey);
			returnJedis(key, jedis);
			jedis = null;
			if (bsValue == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsValue, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
	 *
	 * @param key     key
	 * @param strings
	 * @return result
	 */
	@Override
	public Long lpush(String key, String... strings) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lpush(key, strings);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
	 *
	 * @param key key
	 * @return result
	 */
	public Long lpush(String key, SuperPojo pojo) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bs = pojo.toPbByteArray();
			return jedis.lpush(bsKey, bs);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
	 */
	@Override
	public Long lpushx(String key, String... string) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lpushx(key, string);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
	 *
	 * @param key  key
	 * @param pojo
	 * @return result
	 */
	public Long lpushx(String key, SuperPojo pojo) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = pojo.toPbByteArray();
			return jedis.lpushx(bsKey, bsValue);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素 ，key不存在返回空列表
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public List<String> lrange(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素 ，key不存在返回空列表
	 *
	 * @param key   key
	 * @param start start start
	 * @param end   start   end
	 * @return result
	 */
	public <E extends SuperPojo> List<E> lrange(String key, long start, long end, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			List<byte[]> values = jedis.lrange(bsKey, start, end);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Long lrem(String key, long count, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lrem(key, count, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	public Long lrem(String key, long count, SuperPojo value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = value.toPbByteArray();
			return jedis.lrem(bsKey, count, bsValue);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置list中指定下标的元素值，成功返回1，key或者下标不存在返回错误
	 *
	 * @param key   key
	 * @param index index
	 * @param value field value
	 * @return result
	 */
	@Override
	public String lset(String key, long index, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.lset(key, index, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置list中指定下标的元素值，成功返回1，key或者下标不存在返回错误
	 *
	 * @param key   key
	 * @param index index
	 * @param value field value
	 * @return result
	 */
	public String lset(String key, long index, SuperPojo value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = value.toPbByteArray();

			return jedis.lset(bsKey, index, bsValue);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 截取list，保留指定区间内元素，成功返回1，key不存在返回错误
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public String ltrim(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.ltrim(key, start, end);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 将当前数据库(默认为0)的key移动到给定的数据库db当中。
	 *
	 * @param key     key
	 * @param dbIndex dbIndex
	 * @return result
	 */
	@Override
	public Long move(String key, int dbIndex) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.move(key, dbIndex);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 移除给定key的生存时间。
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long persist(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.persist(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
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
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.pfadd(key, elements);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	public Long pfadd(String key, SuperPojo element) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = element.toPbByteArray();
			return jedis.pfadd(bsKey, bsValue);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * Available since 2.8.9. pfadd在key下面的不重复elements的数量
	 */
	@Override
	public long pfcount(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.pfcount(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 归还Jedis实例
	 *
	 * @param jedis
	 */
	public void returnJedis(String key, Jedis jedis) {
		if (jedis != null) {
			//jedis.close();
			jedisPool.resolvePool(new RedisKey(key)).returnResource(jedis);
		}
	}

	public void returnJedis(RedisKey key, Jedis jedis) {
		if (jedis != null) {
			//jedis.close();
			jedisPool.resolvePool(key).returnResource(jedis);
		}
	}

	/**
	 * 同上，但是从尾部删除
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public String rpop(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.rpop(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同上，但是从尾部删除
	 *
	 * @param key key
	 * @return result
	 */
	public <E extends SuperPojo> E rpop(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = jedis.rpop(bsKey);
			returnJedis(key, jedis);
			jedis = null;
			if (bsValue == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsValue, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 在key对应list的尾部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
	 *
	 * @param key     key
	 * @param strings
	 * @return result
	 */
	@Override
	public Long rpush(String key, String... strings) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.rpush(key, strings);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 在key对应list的尾部添加SuperPojo元素，返回1表示成功，0表示key存在且不是list类型
	 *
	 * @param key  key
	 * @param pojo
	 * @return result
	 */
	public Long rpush(String key, SuperPojo pojo) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bs = pojo.toPbByteArray();
			return jedis.rpush(bsKey, bs);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
	 */
	@Override
	public Long rpushx(String key, String... string) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.rpushx(key, string);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
	 *
	 * @param key  key
	 * @param pojo
	 * @return result
	 */
	public Long rpushx(String key, SuperPojo pojo) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = pojo.toPbByteArray();
			return jedis.rpushx(bsKey, bsValue);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * sadd key member
	 * 添加一个string元素到,key对应的set集合中，成功返回1,如果元素以及在集合中返回0,key对应的set不存在返回错误
	 *
	 * @param key     key
	 * @param members members
	 * @return result
	 */
	@Override
	public Long sadd(String key, String... members) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.sadd(key, members);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * sadd key member
	 * 添加一个SuperPojo元素到key对应的set集合中，成功返回1,如果元素已经在集合中返回0,key对应的set不存在返回错误
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	public Long sadd(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsValue = member.toPbByteArray();
			return jedis.sadd(bsKey, bsValue);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回set的元素个数，如果set是空或者key不存在返回0
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long scard(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.scard(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置key对应的value
	 *
	 * @param key   key
	 * @param value field
	 * @return result
	 */
	@Override
	public String set(String key, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.set(key, value);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public String set(byte[] key, byte[] value) {
//		if (value == null){
//			return null;
//		}
		Jedis jedis = getJedis(new String(key));
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.set(key, value);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(new String(key), jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public String set(String key, String value, String nxxx, String expx, long time) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.set(key, value, nxxx, expx, time);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 设置key对应的value
	 *
	 * @param key   key
	 * @param value field
	 * @return result
	 */

	public String set(String key, SuperPojo value) {
		byte[] bs = value.toPbByteArray();
		byte[] bsKey = SafeEncoder.encode(key);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.set(bsKey, bs);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置在指定Offset上BIT的值，该值只能为1或0，在设定后该命令返回该Offset上原有的BIT值。如果指定Key不存在，该命令将创建一个新值
	 * ，并在指定的Offset上设定参数中的BIT值
	 *
	 * @param key    key
	 * @param offset offset
	 * @param value  field
	 * @return result
	 */
	@Override
	public Boolean setbit(String key, long offset, boolean value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setbit(key, offset, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Boolean setbit(String key, long offset, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setbit(key, offset, value);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置带超时时间的value
	 *
	 * @param key     key
	 * @param seconds
	 * @param value   field
	 * @return result
	 */
	@Override
	public String setex(String key, int seconds, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setex(key, seconds, value);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 设置带超时时间的value
	 *
	 * @param key     key
	 * @param seconds
	 * @param value   field
	 * @return result
	 */
	public String setex(String key, int seconds, SuperPojo value) {
		byte[] bs = value.toPbByteArray();
		byte[] bsKey = SafeEncoder.encode(key);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setex(bsKey, seconds, bs);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 如果key已经存在，返回0 。nx 是not exist的意思
	 *
	 * @param key   key
	 * @param value field
	 * @return result
	 */
	@Override
	public Long setnx(String key, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setnx(key, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 如果key已经存在，返回0 。nx 是not exist的意思
	 *
	 * @param key   key
	 * @param value field
	 * @return result
	 */
	public Long setnx(String key, SuperPojo value) {
		byte[] bs = value.toPbByteArray();
		byte[] bsKey = SafeEncoder.encode(key);

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setnx(bsKey, bs);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 从第offset个开始替换
	 */
	@Override
	public Long setrange(String key, long offset, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.setrange(key, offset, value);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 判断member是否在set中，存在返回1，0表示不存在或者key不存在
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	@Override
	public Boolean sismember(String key, String member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.sismember(key, member);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 判断member是否在set中，存在返回1，0表示不存在或者key不存在
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	public Boolean sismember(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.sismember(bsKey, bsMember);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回key对应set的所有元素，结果是无序的
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Set<String> smembers(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.smembers(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回key对应set的所有元素，结果是无序的
	 *
	 * @param key key
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> smembers(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.smembers(bsKey);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public List<String> sort(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.sort(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	public <E extends SuperPojo> List<E> sort(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			List<byte[]> value = jedis.sort(bsKey);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public List<String> sort(String key, SortingParams sortingParameters) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.sort(key, sortingParameters);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	public <E extends SuperPojo> List<E> sort(String key, SortingParams sortingParameters, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			List<byte[]> value = jedis.sort(bsKey, sortingParameters);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 删除并返回key对应set中随机的一个元素,如果set是空或者key不存在返回nil
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public String spop(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.spop(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 删除并返回key对应set中随机的一个元素,如果set是空或者key不存在返回null
	 *
	 * @param key key
	 * @return result
	 */
	public <E extends SuperPojo> E spop(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = jedis.spop(bsKey);
			returnJedis(key, jedis);
			jedis = null;
			if (bsMember == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsMember, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同spop，随机取set中的一个元素，但是不删除元素
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public String srandmember(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.srandmember(key);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同spop，随机取set中的一个元素，但是不删除元素
	 *
	 * @param key key
	 * @return result
	 */
	public <E extends SuperPojo> E srandmember(String key, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = jedis.srandmember(bsKey);
			returnJedis(key, jedis);
			jedis = null;
			if (bsMember == null)
				return null;

			E tmp = clazz.newInstance();
			SuperPojoManager.parsePbFrom(bsMember, tmp);
			return tmp;
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public List<String> srandmember(String key, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.srandmember(key, count);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 从key对应set中移除给定元素，成功返回1，如果member在集合中不存在或者key不存在返回0，如果key对应的不是set类型的值返回错误
	 *
	 * @param key     key
	 * @param members members
	 * @return result
	 */
	@Override
	public Long srem(String key, String... members) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.srem(key, members);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 从key对应set中移除给定元素，成功返回1，如果member在集合中不存在或者key不存在返回0，如果key对应的不是set类型的值返回错误
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	public Long srem(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.srem(bsKey, bsMember);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	@Deprecated
	public ScanResult<String> sscan(String key, int cursor) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.sscan(key, cursor);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public ScanResult<String> sscan(String key, String cursor) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.sscan(key, cursor);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long strlen(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.strlen(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回截取过的key的字符串值,注意并不修改key的值。
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public String substr(String key, int start, int end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.substr(key, start, end);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/******************** 以下内容为自创部分--支持SuperPojo ***********************************/

	/**
	 * 返回设置过过期时间的key的剩余过期秒数 -1表示key不存在或者没有设置过过期时间
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long ttl(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.ttl(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Long ttl(byte[] key) {
		Jedis jedis = getJedis(new String(key));
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.ttl(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(new String(key), jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回给定key的value的类型 none,string,list,set
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public String type(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.type(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 添加元素到集合，元素在集合中存在则更新对应score
	 *
	 * @param key    key
	 * @param score  score
	 * @param member member
	 * @return result
	 */
	@Override
	public Long zadd(String key, double score, String member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zadd(key, score, member);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 添加元素到集合，元素在集合中存在则更新对应score
	 *
	 * @param key    key
	 * @param score  score
	 * @param member member
	 * @return result
	 */
	public Long zadd(String key, double score, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();

			return jedis.zadd(bsKey, score, bsMember);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long zadd(String key, Map<String, Double> scoreMembers) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zadd(key, scoreMembers);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中元素个数
	 *
	 * @param key key
	 * @return result
	 */
	@Override
	public Long zcard(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zcard(key);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的数量
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return result
	 */
	@Override
	public Long zcount(String key, double min, double max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zcount(key, min, max);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long zcount(String key, String min, String max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zcount(key, min, max);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 增加对应member的score值，然后移动元素并保持skip list保持有序。返回更新后的score值
	 *
	 * @param key    key
	 * @param score  score
	 * @param member member
	 * @return result
	 */
	@Override
	public Double zincrby(String key, double score, String member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zincrby(key, score, member);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 增加对应member的score值，然后移动元素并保持skip list保持有序。返回更新后的score值
	 *
	 * @param key    key
	 * @param score  score
	 * @param member member
	 * @return result
	 */
	public Double zincrby(String key, double score, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.zincrby(bsKey, score, bsMember);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long zlexcount(String key, String min, String max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zlexcount(key, min, max);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 类似lrange操作从集合中去指定区间的元素。返回的是有序结果
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public Set<String> zrange(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrange(key, start, end);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 类似lrange操作从集合中去指定区间的元素。返回的是有序结果
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrange(String key, long start, long end, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.zrange(bsKey, start, end);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByLex(key, min, max);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByLex(key, min, max, offset, count);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return result
	 */
	@Override
	public Set<String> zrangeByScore(String key, double min, double max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScore(key, min, max);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的元素
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrangeByScore(String key, double min, double max, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Set<byte[]> value = jedis.zrangeByScore(bsKey, min, max);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScore(key, min, max, offset, count);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的元素
	 *
	 * @param key    key
	 * @param min    min
	 * @param max    max
	 * @param offset offset
	 * @param count  count
	 * @param clazz  clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrangeByScore(String key, double min, double max, int offset, int count, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Set<byte[]> value = jedis.zrangeByScore(bsKey, min, max, offset, count);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素(min.score ,max.score)
	 *
	 * @param key   key
	 * @param min   min
	 * @param max   max
	 * @param clazz clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrangeByScore(String key, E min, E max, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMin = min.toPbByteArray();
			byte[] bsMax = max.toPbByteArray();
			Set<byte[]> value = jedis.zrangeByScore(bsKey, bsMin, bsMax);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素(min.score,max.score)
	 *
	 * @param key    key
	 * @param min    min
	 * @param max    max
	 * @param offset offset
	 * @param count  count
	 * @param clazz  clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrangeByScore(String key, E min, E max, int offset, int count, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMax = max.toPbByteArray();
			byte[] bsMin = min.toPbByteArray();

			Set<byte[]> value = jedis.zrangeByScore(bsKey, bsMin, bsMax, offset, count);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素(min.score,max.score)
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return result
	 */
	@Override
	public Set<String> zrangeByScore(String key, String min, String max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScore(key, min, max);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScore(key, min, max, offset, count);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScoreWithScores(key, min, max);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScoreWithScores(key, min, max, offset, count);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScoreWithScores(key, min, max);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeByScoreWithScores(key, min, max, offset, count);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的元素
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public Set<Tuple> zrangeWithScores(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrangeWithScores(key, start, end);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回指定元素在集合中的排名（下标）,集合中元素是按score从小到大排序的
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	@Override
	public Long zrank(String key, String member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrank(key, member);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回指定元素在集合中的排名（下标）,集合中元素是按score从小到大排序的
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	public Long zrank(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.zrank(bsKey, bsMember);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
	 *
	 * @param key     key
	 * @param members members
	 * @return result
	 */
	@Override
	public Long zrem(String key, String... members) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrem(key, members);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
	 *
	 * @param key key
	 * @return result
	 */
	public Long zrem(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.zrem(bsKey, bsMember);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long zremrangeByLex(String key, String min, String max) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zremrangeByLex(key, min, max);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 删除集合中排名在给定区间的元素
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public Long zremrangeByRank(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zremrangeByRank(key, start, end);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 删除集合中score在给定区间的元素
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public Long zremrangeByScore(String key, double start, double end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Long zremrangeByScore(String key, String start, String end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zremrangeByScore(key, start, end);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回结果是按score逆序的
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public Set<String> zrevrange(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrange(key, start, end);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回结果是按score逆序的
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrevrange(String key, long start, long end, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			Set<byte[]> value = jedis.zrevrange(bsKey, start, end);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素,逆序，从大到小
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return result
	 */
	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的元素,逆序，从大到小
	 *
	 * @param key   key
	 * @param max   max
	 * @param min   min
	 * @param clazz clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, double max, double min, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, max, min);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的元素,逆序，从大到小
	 *
	 * @param key    key
	 * @param max    max
	 * @param min    min
	 * @param offset offset
	 * @param count  count
	 * @param clazz  clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, double max, double min, int offset, int count, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);

			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, max, min, offset, count);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素,逆序
	 *
	 * @param key   key
	 * @param max   max
	 * @param min   min
	 * @param clazz clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, E max, E min, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMax = max.toPbByteArray();
			byte[] bsMin = min.toPbByteArray();
			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, bsMax, bsMin);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素
	 *
	 * @param key    key
	 * @param max    max
	 * @param min    min
	 * @param offset offset
	 * @param count  count
	 * @param clazz  clazz
	 * @return result
	 */
	public <E extends SuperPojo> Set<E> zrevrangeByScore(String key, E max, E min, int offset, int count, Class<E> clazz) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {

			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMax = max.toPbByteArray();
			byte[] bsMin = min.toPbByteArray();

			Set<byte[]> value = jedis.zrevrangeByScore(bsKey, bsMax, bsMin, offset, count);
			returnJedis(key, jedis);
			jedis = null;
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
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回集合中score在给定区间的元素(max.score,min.score),逆序，从大到小
	 *
	 * @param key key
	 * @param min min
	 * @param max max
	 * @return result
	 */
	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScore(key, max, min);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScore(key, max, min, offset, count);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScoreWithScores(key, max, min);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScoreWithScores(key, max, min);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回集合中score在给定区间的元素 逆序排列
	 *
	 * @param key   key
	 * @param start start
	 * @param end   start
	 * @return result
	 */
	@Override
	public Set<Tuple> zrevrangeWithScores(String key, long start, long end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrangeWithScores(key, start, end);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同上,但是集合中元素是按score从大到小排序
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	@Override
	public Long zrevrank(String key, String member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zrevrank(key, member);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 同上,但是集合中元素是按score从大到小排序
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	public Long zrevrank(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.zrevrank(bsKey, bsMember);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	@Deprecated
	public ScanResult<Tuple> zscan(String key, int cursor) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zscan(key, cursor);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	@Override
	public ScanResult<Tuple> zscan(String key, String cursor) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zscan(key, cursor);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 返回给定元素对应的score
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	@Override
	public Double zscore(String key, String member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.zscore(key, member);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	/**
	 * 返回给定元素对应的score
	 *
	 * @param key    key
	 * @param member member
	 * @return result
	 */
	public Double zscore(String key, SuperPojo member) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			byte[] bsKey = SafeEncoder.encode(key);
			byte[] bsMember = member.toPbByteArray();
			return jedis.zscore(bsKey, bsMember);

		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}

	}

	/**
	 * 获取redis时间
	 *
	 * @param key key
	 * @return result
	 */
	public List<String> time(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			return jedis.time();
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
			watchEnd(watch, ex);
		}
	}

	@Override
	public Closeable<Pipeline> getPipeline(String key) {
		Jedis jedis = getJedis(key);
		Pipeline pipeline = jedis.pipelined();
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		return new Closeable<Pipeline>() {
			@Override
			public Pipeline get() {
				return pipeline;
			}

			@Override
			public void close() {
				if (pipeline != null) {
					pipeline.sync();
					returnJedis(key, jedis);
				}
				watchEnd(watch, ex);
			}
		};
	}

	@Override
	public boolean setObjectEx(String key, Object value, int time) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.setex(key.getBytes(), time, ByteArrayUtils.toByteArray(value));
			watch.end();
			return true;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}


	@Override
	public boolean setex(String key, byte[] value, int time) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.setex(key.getBytes(), time, value);
			watch.end();
			return true;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}

	@Override
	public boolean set(String key, byte[] value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.set(key.getBytes(), value);
			watch.end();
			return true;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}


	@Override
	public boolean setObject(String key, Object value) {

		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.set(key.getBytes(), ByteArrayUtils.toByteArray(value));
			watch.end();
			return true;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}


	@Override
	public boolean sadd(String id, Object value) {
		Jedis jedis = getJedis(id);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.sadd(id.getBytes(), ByteArrayUtils.toByteArray(value));
			watch.end();
			return true;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(id, jedis);
		}

	}

	@Override
	public boolean sadd(String id, byte[] value) {
		Jedis jedis = getJedis(id);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.sadd(id.getBytes(), value);
			watch.end();
			return true;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(id, jedis);
		}
	}

	@Override
	public Object getObject(String key) {
		Jedis jedis = getJedis(key);
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
			returnJedis(key, jedis);
		}

	}

	@Override
	public byte[] getBytes(String key) {
		Jedis jedis = getJedis(key);
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
			returnJedis(key, jedis);
		}
	}

	@Override
	public List<String> lrangeAll(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.lrange(key, 0, -1);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}

	@Override
	public List<String> lrange(String key, int start, int end) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.lrange(key, start, end);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}

	@Override
	public long lleng(String key) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.llen(key);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}

	@Override
	public void setSubscribe(JedisPubSub pubSub) {
		String keyStr = new String("JedisPubSub");
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.psubscribe(pubSub, "*");
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public void sadd(byte[] key, byte[] value) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.sadd(key, value);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public void sadd(String key, String value) {
		Jedis jedis = getJedis(key);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.sadd(key, value);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(key, jedis);
		}
	}

	@Override
	public Set<byte[]> smembers(byte[] key) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.smembers(key);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public void srem(byte[] key, byte[]... value) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.srem(key, value);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public boolean sismember(byte[] setKey, byte[] value) {
		String keyStr = new String(setKey);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.sismember(setKey, value);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public void hmset(byte[] key, Map<byte[], byte[]> map) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			jedis.hmset(key, map);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public Map<byte[], byte[]> hmget(byte[] key) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			return jedis.hgetAll(key);
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public byte[] hmgetValue(byte[] key, byte[] keyword) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			List<byte[]> value = jedis.hmget(key, keyword);
			if (value != null && value.size() > 0) {
				return value.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}

	}

	@Override
	public List<byte[]> hmget(byte[] key, byte[][] fields) {
		String keyStr = new String(key);
		Jedis jedis = getJedis(keyStr);
		Stopwatch watch = counters.getTx().begin();
		try {
			List<byte[]> value = jedis.hmget(key, fields);
			return value;
		} catch (Exception e) {
			watch.fail(e);
			throw new RuntimeException(e);
		} finally {
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		String keyStr = new String("eval");
		Jedis jedis = getJedis(keyStr);
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
			returnJedis(keyStr, jedis);
		}
	}

	@Override
	public boolean set(String key, String value, String nxxx, String expx, int expire) {
		Jedis jedis = getJedis(key);
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
			returnJedis(key, jedis);
		}
	}

	@Override
	public void pipelined(String key, Consumer<Pipeline> func) {
		Jedis jedis = getJedis(key);
		Pipeline pipeline = jedis.pipelined();
		Stopwatch watch = counters.getTx().begin();
		Exception ex = null;
		try {
			func.accept(pipeline);
		} catch (Exception e) {
			ex = e;
			throw new RuntimeException(e);
		} finally {
			if (pipeline != null) {
				pipeline.sync();
				returnJedis(key, jedis);
			}
			watchEnd(watch, ex);
		}
	}

	/**
	 * 计数器结束
	 *
	 * @param watch 计数器
	 * @param ex    异常(如果有)
	 */
	private void watchEnd(Stopwatch watch, Exception ex) {
		if (watch == null)
			return;

		if (ex == null) {
			watch.end();
		} else {
			watch.fail(ex);
		}
	}


}
