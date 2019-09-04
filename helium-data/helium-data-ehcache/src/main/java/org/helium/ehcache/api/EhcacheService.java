package org.helium.ehcache.api;

import net.sf.ehcache.Cache;
import org.helium.framework.annotations.ServiceInterface;

/**
 * 本地缓存接口
 * @author wudashuai
 * @date   2018-08-29
 */
@ServiceInterface(id = "urcscats:EhcacheService")
public interface EhcacheService {

    /**
     * 根据缓存名称和缓存Key清除缓存
     * @param cacheName
     * @param cacheKey
     */
    public void clearCacheByKey(String cacheName, String cacheKey);

    /**
     * 根据缓存名称清除该缓存名下所有的缓存
     * @param cacheName
     */
    public void clearCacheByName(String cacheName);

    /**
     * 清除所有的缓存
     */
    public void clearAllCache();

    /**
     * 设置缓存不含过期时间
     * @param cacheName
     * @param cacheKey
     * @param value
     */
    public void putCache(String cacheName, String cacheKey, Object value);

    /**
     * 根据key获取缓存的具体内容
     * @param cacheName
     * @param cacheKey
     * @return
     */
    public Object getCacheValue(String cacheName, String cacheKey);

    /**
     * 获取缓存
     * @param cacheName
     * @return
     */
    public Cache getCache(String cacheName);

    /**
     * 设置缓存，包含过期时间的设置
     * @param cacheName         缓存名称
     * @param cacheKey          缓存key
     * @param value             值
     * @param timeToLiveSeconds 存在时间，单位秒
     */
    public void putCacheExpire(String cacheName, String cacheKey, Object value, int timeToLiveSeconds);

    /**
     * 清除所有缓存并删除CacheName
     */
    public void removeAllCacheName();

}