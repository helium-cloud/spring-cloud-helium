package org.helium.redis;


import com.feinno.superpojo.SuperPojo;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.framework.utils.Closeable;
import org.helium.redis.spi.RedisLoader;
import redis.clients.jedis.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 从原有的同步过来, 其实更好的方法是去掉这个 实现Jedis自带的几个接口.那个方法太多. 尽管这个也不少.
 */
@SuppressWarnings("deprecation")
@FieldLoaderType(loaderType = RedisLoader.class)
public interface RedisClient {
    /**
     * 给指定key的字符串值追加value,返回新字符串值的长度。
     *
     * @param key   key
     * @param value value
     * @return result
     */
    Long append(String key, String value);

    String ping(String key);


    Long bitcount(String key);

    Long bitcount(String key, long start, long end);

    List<String> blpop(int timeout, String key);

    /**
     * 它是 LPOP 命令的阻塞版本
     */
    List<String> blpop(String key);

    /**
     * LPOP的阻塞版本
     *
     * @param key   key
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> List<E> blpop(String key, Class<E> clazz);

    List<String> brpop(int timeout, String key);

    /**
     * 它是 RPOP 命令的阻塞版本
     */
    List<String> brpop(String key);

    /**
     * RPOP的阻塞版本
     *
     * @param key   key
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> List<E> brpop(String key, Class<E> clazz);

    /**
     * 但是做的是减减操作，decr一个不存在key，则设置key为-1
     *
     * @param key key
     * @return result
     */
    Long decr(String key);

    /**
     * 同decr，减指定值。
     *
     * @param key     key
     * @param integer
     * @return result
     */
    Long decrBy(String key, long integer);

    /**
     * 删除key对应的值
     */
    Long del(String key);

    String echo(String string);

    /**
     * 是否存在key
     *
     * @param key key
     * @return result
     */
    Boolean exists(String key);

    /**
     * 设置超时时间，单位秒；返回1成功，0表示key已经设置过过期时间或者不存在
     *
     * @param key    key
     * @param expire expire
     * @return result
     */
    Long expire(String key, int expire);

    Long expire(byte[] key, int expire);

    /**
     * EXPIREAT命令接受的时间参数是UNIX时间戳(unix timestamp)。
     *
     * @param key      key
     * @param unixTime unixTime
     * @return result
     */
    Long expireAt(String key, long unixTime);

    /**
     * 获取key对应的value
     *
     * @param key key
     * @return result
     */
    String get(String key);

    byte[] get(byte[] key);

    /**
     * 获取key对应的value
     *
     * @param key key
     * @return result
     */
    <E extends SuperPojo> E get(String key, Class<E> clazz);

    /**
     * 返回在指定Offset上BIT的值，0或1。如果Offset超过string value的长度，该命令将返回0，所以对于空字符串始终返回0。
     */
    Boolean getbit(String key, long offset);

    /**
     * 获取Jedis实例
     *
     * @return result
     */
    String getrange(String key, long startOffset, long endOffset);

    /**
     * 原子的设置key的值，并返回key的旧值。如果key不存在返回nil
     *
     * @param key   key
     * @param value value
     * @return result
     */
    String getSet(String key, String value);

    /**
     * 原子的设置key的值，并返回key的旧值。如果key不存在返回null
     *
     * @param key   key
     * @param value value
     * @return result
     */
    <E extends SuperPojo> E getSet(String key, SuperPojo value, Class<E> clazz);

    /**
     * 删除指定的hash field
     *
     * @param key    key
     * @param fields fields
     * @return result
     */
    Long hdel(String key, String... fields);

    /**
     * 测试指定field是否存在
     *
     * @param key   key
     * @param field field
     * @return result
     */
    Boolean hexists(String key, String field);

    /**
     * 获取指定的hash field
     *
     * @param key   key
     * @param field field
     * @return result
     */
    String hget(String key, String field);

    /**
     * 获取指定的hash field
     *
     * @param key   key
     * @param field field
     * @return result
     */
    <E extends SuperPojo> E hget(String key, String field, Class<E> clazz);

    /**
     * 获取指定的hash field
     *
     * @param key   key
     * @param field field
     * @return result
     */
    byte[] hgetPojo(String key, String field);

    /**
     * 返回hash的所有filed和value
     *
     * @param key key
     * @return result
     */
    Map<String, String> hgetAll(String key);

    /**
     * 返回hash的所有filed和value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
     *
     * @param key key
     * @return result
     */
    Map<String, byte[]> hgetAllPojo(String key);

    /**
     * 将指定的hash filed 加上给定值
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return result
     */
    Long hincrBy(String key, String field, long value);

    /**
     * 返回hash的所有field
     *
     * @param key key
     * @return result
     */
    Set<String> hkeys(String key);

    /**
     * 返回指定hash的field数量
     *
     * @param key key
     * @return result
     */
    Long hlen(String key);

    /**
     * 获取全部指定的hash filed
     *
     * @param key    key
     * @param fields fields
     * @return result
     */
    List<String> hmget(String key, String... fields);

    /**
     * 获取全部指定的hash filed
     *
     * @param key    key
     * @param fields fields
     * @return result
     */
    List<byte[]> hmgetPojo(String key, String... fields);

    String hmset(String key, Map<String, String> hash);

    /**
     * 设置Map
     *
     * @param key  key
     * @param hash hash
     * @return result
     */
    <E extends SuperPojo> String hmsetPojo(String key, Map<String, E> hash);

    ScanResult<Entry<String, String>> hscan(String key, int cursor);

    ScanResult<Entry<String, String>> hscan(String key, String cursor);

    /**
     * 设置hash field为指定值，如果key不存在，则先创建
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return result
     */
    Long hset(String key, String field, String value);

    /**
     * 设置hash field为指定值，如果key不存在，则先创建
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return result
     */
    Long hset(String key, String field, SuperPojo value);

    /**
     * 设置hash field为指定值，如果key不存在，则先创建
     *
     * @param key   key
     * @param field field
     * @param bs
     * @return result
     */
    Long hset(String key, String field, byte[] bs);

    /**
     * 设置hash field为指定值，nx:not exist ,Key不存在的情况下才可以设置
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return result
     */
    Long hsetnx(String key, String field, String value);

    /**
     * not exist key 和 field的情况下才能设置
     *
     * @param key   key
     * @param field field
     * @param value value
     * @return result
     */
    Long hsetnx(String key, String field, SuperPojo value);

    /**
     * 返回hash的所有value
     *
     * @param key key
     * @return result
     */
    List<String> hvals(String key);

    /**
     * 返回hash的所有value value类型可能不相同，所以还是返回byte[]，然后自己再根据类型处理
     *
     * @param key key
     * @return result
     */
    Collection<byte[]> hvalsPojo(String key);

    /**
     * 对key的值做加加操作,并返回新的值。注意incr一个不是int的value会返回错误，incr一个不存在的key，则设置key为1
     *
     * @param key key
     * @return result
     */
    Long incr(String key);

    /**
     * 同incr，加指定值。
     *
     * @param key     key
     * @param integer
     * @return result
     */
    Long incrBy(String key, long integer);

    /**
     * 从左侧取第index个String
     */
    String lindex(String key, long index);

    /**
     * List 从左侧数index获得元素
     *
     * @param key   key
     * @param index index
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> E lindex(String key, long index, Class<E> clazz);

    /**
     * 在指定的元素pivot前面或是后面插入一个元素
     */
    Long linsert(String key, BinaryClient.LIST_POSITION where, String pivot, String value);

    /**
     * 在指定pivot的前面或者后面插入
     *
     * @param key   key
     * @param where where
     * @param pivot pivot
     * @return result
     */
    Long linsert(String key, BinaryClient.LIST_POSITION where, SuperPojo pivot, SuperPojo value);

    /**
     * 返回key对应list的长度，key不存在返回0,如果key对应类型不是list返回错
     *
     * @param key key
     * @return result
     */
    Long llen(String key);

    /**
     * 从list的头部删除元素，并返回删除元素。如果key对应list不存在或者是空返回nil，如果key对应值不是list返回错误
     *
     * @param key key
     * @return result
     */
    String lpop(String key);

    /**
     * 从list的头部删除元素，并返回删除元素。如果key对应list不存在或者是空返回null，如果key对应值不是list返回错误
     *
     * @param key key
     * @return result
     */
    <E extends SuperPojo> E lpop(String key, Class<E> clazz);

    /**
     * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key     key
     * @param strings
     * @return result
     */
    Long lpush(String key, String... strings);

    /**
     * 在key对应list的头部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key key
     * @return result
     */
    Long lpush(String key, SuperPojo pojo);

    /**
     * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     */
    Long lpushx(String key, String... string);

    /**
     * 和 LPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     *
     * @param key  key
     * @param pojo
     * @return result
     */
    Long lpushx(String key, SuperPojo pojo);

    /**
     * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素 ，key不存在返回空列表
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    List<String> lrange(String key, long start, long end);

    /**
     * 返回指定区间内的元素，下标从0开始，负值表示从后面计算，-1表示倒数第一个元素 ，key不存在返回空列表
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    <E extends SuperPojo> List<E> lrange(String key, long start, long end, Class<E> clazz);

    Long lrem(String key, long count, String value);

    Long lrem(String key, long count, SuperPojo value);

    /**
     * 设置list中指定下标的元素值，成功返回1，key或者下标不存在返回错误
     *
     * @param key   key
     * @param index index
     * @param value value
     * @return result
     */
    String lset(String key, long index, String value);

    /**
     * 设置list中指定下标的元素值，成功返回1，key或者下标不存在返回错误
     *
     * @param key   key
     * @param index index
     * @param value value
     * @return result
     */
    String lset(String key, long index, SuperPojo value);

    /**
     * 截取list，保留指定区间内元素，成功返回1，key不存在返回错误
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    String ltrim(String key, long start, long end);

    /**
     * 将当前数据库(默认为0)的key移动到给定的数据库db当中。
     *
     * @param key     key
     * @param dbIndex
     * @return result
     */
    Long move(String key, int dbIndex);

    /**
     * 移除给定key的生存时间。
     *
     * @param key key
     * @return result
     */
    Long persist(String key);

    /**
     * Available since 2.8.9. Adds all the element arguments to the HyperLogLog
     * data structure stored at the variable name specified as first argument.
     * If the approximated cardinality estimated by the HyperLogLog changed
     * after executing the command, PFADD returns 1, otherwise 0 is returned.
     * 参考：HyperLogLog算法，高大上，主要用于大数据下统计不同element的数量
     */
    Long pfadd(String key, String... elements);

    Long pfadd(String key, SuperPojo element);

    /**
     * Available since 2.8.9. pfadd在key下面的不重复elements的数量
     */
    long pfcount(String key);


    /**
     * 同上，但是从尾部删除
     *
     * @param key key
     * @return result
     */
    String rpop(String key);

    /**
     * 同上，但是从尾部删除
     *
     * @param key key
     * @return result
     */
    <E extends SuperPojo> E rpop(String key, Class<E> clazz);

    /**
     * 在key对应list的尾部添加字符串元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key     key
     * @param strings
     * @return result
     */
    Long rpush(String key, String... strings);

    /**
     * 在key对应list的尾部添加SuperPojo元素，返回1表示成功，0表示key存在且不是list类型
     *
     * @param key  key
     * @param pojo
     * @return result
     */
    Long rpush(String key, SuperPojo pojo);

    /**
     * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     */
    Long rpushx(String key, String... string);

    /**
     * 和 RPUSH 命令相反，当 key 不存在时， LPUSHX 命令什么也不做
     *
     * @param key  key
     * @param pojo
     * @return result
     */
    Long rpushx(String key, SuperPojo pojo);

    /**
     * sadd key member
     * 添加一个string元素到,key对应的set集合中，成功返回1,如果元素以及在集合中返回0,key对应的set不存在返回错误
     *
     * @param key     key
     * @param members members
     * @return result
     */
    Long sadd(String key, String... members);

    /**
     * sadd key member
     * 添加一个SuperPojo元素到key对应的set集合中，成功返回1,如果元素已经在集合中返回0,key对应的set不存在返回错误
     *
     * @param key    key
     * @param member
     * @return result
     */
    Long sadd(String key, SuperPojo member);

    /**
     * 返回set的元素个数，如果set是空或者key不存在返回0
     *
     * @param key key
     * @return result
     */
    Long scard(String key);

    /**
     * 设置key对应的value
     *
     * @param key   key
     * @param value value
     * @return result
     */
    String set(String key, String value);

    String set(byte[] key, byte[] value);

    String set(String key, String value, String nxxx, String expx, long time);

    /**
     * 设置key对应的value
     *
     * @param key   key
     * @param value value
     * @return result
     */
    String set(String key, SuperPojo value);

    /**
     * 设置在指定Offset上BIT的值，该值只能为1或0，在设定后该命令返回该Offset上原有的BIT值。如果指定Key不存在，该命令将创建一个新值
     * ，并在指定的Offset上设定参数中的BIT值
     *
     * @param key    key
     * @param offset
     * @param value  value
     * @return result
     */
    Boolean setbit(String key, long offset, boolean value);

    Boolean setbit(String key, long offset, String value);

    /**
     * 设置带超时时间的value
     *
     * @param key     key
     * @param seconds seconds
     * @param value   value
     * @return result
     */
    String setex(String key, int seconds, String value);

    /**
     * 设置带超时时间的value
     *
     * @param key     key
     * @param seconds seconds
     * @param value   SuperPojo
     * @return result
     */
    String setex(String key, int seconds, SuperPojo value);

    /**
     * 如果key已经存在，返回0 。nx 是not exist的意思
     *
     * @param key   key
     * @param value value
     * @return result
     */
    Long setnx(String key, String value);

    /**
     * 如果key已经存在，返回0 。nx 是not exist的意思
     *
     * @param key   key
     * @param value value
     * @return result
     */
    Long setnx(String key, SuperPojo value);

    /**
     * 从第offset个开始替换
     */
    Long setrange(String key, long offset, String value);

    /**
     * 判断member是否在set中，存在返回1，0表示不存在或者key不存在
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Boolean sismember(String key, String member);

    /**
     * 判断member是否在set中，存在返回1，0表示不存在或者key不存在
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Boolean sismember(String key, SuperPojo member);

    /**
     * 返回key对应set的所有元素，结果是无序的
     *
     * @param key key
     * @return result
     */
    Set<String> smembers(String key);

    /**
     * 返回key对应set的所有元素，结果是无序的
     *
     * @param key key
     * @return result
     */
    <E extends SuperPojo> Set<E> smembers(String key, Class<E> clazz);

    List<String> sort(String key);

    <E extends SuperPojo> List<E> sort(String key, Class<E> clazz);

    List<String> sort(String key, SortingParams sortingParameters);

    <E extends SuperPojo> List<E> sort(String key, SortingParams sortingParameters, Class<E> clazz);

    /**
     * 删除并返回key对应set中随机的一个元素,如果set是空或者key不存在返回nil
     *
     * @param key key
     * @return result
     */
    String spop(String key);

    /**
     * 删除并返回key对应set中随机的一个元素,如果set是空或者key不存在返回null
     *
     * @param key key
     * @return result
     */
    <E extends SuperPojo> E spop(String key, Class<E> clazz);

    /**
     * 同spop，随机取set中的一个元素，但是不删除元素
     *
     * @param key key
     * @return result
     */
    String srandmember(String key);

    /**
     * 同spop，随机取set中的一个元素，但是不删除元素
     *
     * @param key key
     * @return result
     */
    <E extends SuperPojo> E srandmember(String key, Class<E> clazz);

    List<String> srandmember(String key, int count);

    /**
     * 从key对应set中移除给定元素，成功返回1，如果member在集合中不存在或者key不存在返回0，如果key对应的不是set类型的值返回错误
     *
     * @param key     key
     * @param members members
     * @return result
     */
    Long srem(String key, String... members);

    /**
     * 从key对应set中移除给定元素，成功返回1，如果member在集合中不存在或者key不存在返回0，如果key对应的不是set类型的值返回错误
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Long srem(String key, SuperPojo member);

    ScanResult<String> sscan(String key, int cursor);

    ScanResult<String> sscan(String key, String cursor);

    Long strlen(String key);

    /**
     * 返回截取过的key的字符串值,注意并不修改key的值。
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    String substr(String key, int start, int end);
    /******************** 以下内容为自创部分--支持SuperPojo ***********************************/
    /**
     * 返回设置过过期时间的key的剩余过期秒数 -1表示key不存在或者没有设置过过期时间
     *
     * @param key key
     * @return result
     */
    Long ttl(String key);

    Long ttl(byte[] key);

    /**
     * 返回给定key的value的类型 none,string,list,set
     *
     * @param key key
     * @return result
     */
    String type(String key);

    /**
     * 添加元素到集合，元素在集合中存在则更新对应score
     *
     * @param key    key
     * @param score
     * @param member member
     * @return result
     */
    Long zadd(String key, double score, String member);

    /**
     * 添加元素到集合，元素在集合中存在则更新对应score
     *
     * @param key    key
     * @param score
     * @param member member
     * @return result
     */
    Long zadd(String key, double score, SuperPojo member);

    Long zadd(String key, Map<String, Double> scoreMembers);

    /**
     * 返回集合中元素个数
     *
     * @param key key
     * @return result
     */
    Long zcard(String key);

    /**
     * 返回集合中score在给定区间的数量
     *
     * @param key key
     * @param min
     * @param max
     * @return result
     */
    Long zcount(String key, double min, double max);

    Long zcount(String key, String min, String max);

    /**
     * 增加对应member的score值，然后移动元素并保持skip list保持有序。返回更新后的score值
     *
     * @param key    key
     * @param score
     * @param member member
     * @return result
     */
    Double zincrby(String key, double score, String member);

    /**
     * 增加对应member的score值，然后移动元素并保持skip list保持有序。返回更新后的score值
     *
     * @param key    key
     * @param score
     * @param member member
     * @return result
     */
    Double zincrby(String key, double score, SuperPojo member);

    Long zlexcount(String key, String min, String max);

    /**
     * 类似lrange操作从集合中去指定区间的元素。返回的是有序结果
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    Set<String> zrange(String key, long start, long end);

    /**
     * 类似lrange操作从集合中去指定区间的元素。返回的是有序结果
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    <E extends SuperPojo> Set<E> zrange(String key, long start, long end, Class<E> clazz);

    Set<String> zrangeByLex(String key, String min, String max);

    Set<String> zrangeByLex(String key, String min, String max, int offset, int count);

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key key
     * @param min
     * @param max
     * @return result
     */
    Set<String> zrangeByScore(String key, double min, double max);

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key key
     * @param min
     * @param max
     * @return result
     */
    <E extends SuperPojo> Set<E> zrangeByScore(String key, double min, double max, Class<E> clazz);

    Set<String> zrangeByScore(String key, double min, double max, int offset, int count);

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key    key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrangeByScore(String key, double min, double max, int offset, int count, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素(min.score ,max.score)
     *
     * @param key   key
     * @param min
     * @param max
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrangeByScore(String key, E min, E max, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素(min.score,max.score)
     *
     * @param key    key
     * @param min
     * @param max
     * @param offset
     * @param count
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrangeByScore(String key, E min, E max, int offset, int count, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素(min.score,max.score)
     *
     * @param key key
     * @param min
     * @param max
     * @return result
     */
    Set<String> zrangeByScore(String key, String min, String max);

    Set<String> zrangeByScore(String key, String min, String max, int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max);

    Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count);

    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max);

    Set<Tuple> zrangeByScoreWithScores(String key, String min, String max, int offset, int count);

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    Set<Tuple> zrangeWithScores(String key, long start, long end);

    /**
     * 返回指定元素在集合中的排名（下标）,集合中元素是按score从小到大排序的
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Long zrank(String key, String member);

    /**
     * 返回指定元素在集合中的排名（下标）,集合中元素是按score从小到大排序的
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Long zrank(String key, SuperPojo member);

    /**
     * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
     *
     * @param key     key
     * @param members members
     * @return result
     */
    Long zrem(String key, String... members);

    /**
     * zrem key member 删除指定元素，1表示成功，如果元素不存在返回0
     *
     * @param key key
     * @return result
     */
    Long zrem(String key, SuperPojo member);

    Long zremrangeByLex(String key, String min, String max);

    /**
     * 删除集合中排名在给定区间的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    Long zremrangeByRank(String key, long start, long end);

    /**
     * 删除集合中score在给定区间的元素
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    Long zremrangeByScore(String key, double start, double end);

    Long zremrangeByScore(String key, String start, String end);

    /**
     * 返回结果是按score逆序的
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    Set<String> zrevrange(String key, long start, long end);

    /**
     * 返回结果是按score逆序的
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    <E extends SuperPojo> Set<E> zrevrange(String key, long start, long end, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素,逆序，从大到小
     *
     * @param key key
     * @param min
     * @param max
     * @return result
     */
    Set<String> zrevrangeByScore(String key, double max, double min);

    /**
     * 返回集合中score在给定区间的元素,逆序，从大到小
     *
     * @param key   key
     * @param max
     * @param min
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrevrangeByScore(String key, double max, double min, Class<E> clazz);

    Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count);

    /**
     * 返回集合中score在给定区间的元素,逆序，从大到小
     *
     * @param key    key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrevrangeByScore(String key, double max, double min, int offset, int count, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素,逆序
     *
     * @param key   key
     * @param max
     * @param min
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrevrangeByScore(String key, E max, E min, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素
     *
     * @param key    key
     * @param max
     * @param min
     * @param offset
     * @param count
     * @param clazz
     * @return result
     */
    <E extends SuperPojo> Set<E> zrevrangeByScore(String key, E max, E min, int offset, int count, Class<E> clazz);

    /**
     * 返回集合中score在给定区间的元素(max.score,min.score),逆序，从大到小
     *
     * @param key key
     * @param min
     * @param max
     * @return result
     */
    Set<String> zrevrangeByScore(String key, String max, String min);

    Set<String> zrevrangeByScore(String key, String max, String min, int offset, int count);

    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min);

    Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count);

    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min);

    Set<Tuple> zrevrangeByScoreWithScores(String key, String max, String min, int offset, int count);

    /**
     * 返回集合中score在给定区间的元素 逆序排列
     *
     * @param key   key
     * @param start start
     * @param end   end
     * @return result
     */
    Set<Tuple> zrevrangeWithScores(String key, long start, long end);

    /**
     * 同上,但是集合中元素是按score从大到小排序
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Long zrevrank(String key, String member);

    /**
     * 同上,但是集合中元素是按score从大到小排序
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Long zrevrank(String key, SuperPojo member);

    ScanResult<Tuple> zscan(String key, int cursor);

    ScanResult<Tuple> zscan(String key, String cursor);

    /**
     * 返回给定元素对应的score
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Double zscore(String key, String member);

    /**
     * 返回给定元素对应的score
     *
     * @param key    key
     * @param member member
     * @return result
     */
    Double zscore(String key, SuperPojo member);

    /**
     * 获取redis时间
     *
     * @param key key
     * @return result
     */
    List<String> time(String key);

    /**
     * 使用函数式方式处理pipeline需求
     *
     * @param key
     * @return
     */
    void pipelined(String key, Consumer<Pipeline> consumer);


    /**
     * 获取一个能够流水线处理的Pipeline, 注意，完成操作后必须手动执行close()方法
     *
     * @param key
     * @return
     */
    Closeable<Pipeline> getPipeline(String key);


    boolean setObjectEx(String key, Object value, int time);

    boolean setex(String key, byte[] value, int time);


    boolean set(String key, byte[] value);



    boolean setObject(String key, Object value);


    boolean sadd(String id, Object value);

    boolean sadd(String id, byte[] value);


    Object getObject(String key);

    byte[] getBytes(String key);

    List<String> lrangeAll(String key);

    List<String> lrange(String key, int start, int end);


    long lleng(String key);

    void setSubscribe(JedisPubSub pubSub);

    void sadd(byte[] key, byte[] value);


    void sadd(String key, String value);


    Set<byte[]> smembers(byte[] key);

    void srem(byte[] key, byte[]... value);


    boolean sismember(byte[] setKey, byte[] value);

    void hmset(byte[] key, Map<byte[], byte[]> map);

    Map<byte[], byte[]> hmget(byte[] key);

    byte[] hmgetValue(byte[] key, byte[] keyword);

    List<byte[]> hmget(byte[] key, byte[][] fields);

    Object eval(String script, List<String> keys, List<String> args);

    boolean set(String key, String value, String nxxx, String expx, int expire);
}
