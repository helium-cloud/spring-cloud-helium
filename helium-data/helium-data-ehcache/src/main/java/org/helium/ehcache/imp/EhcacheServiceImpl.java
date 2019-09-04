package org.helium.ehcache.imp;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.helium.ehcache.api.EhcacheService;
import org.helium.framework.annotations.ServiceImplementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 本地缓存接口实现
 * @author wudashuai
 * @date   2018-08-29
 */
@ServiceImplementation
public class EhcacheServiceImpl implements EhcacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EhcacheServiceImpl.class);

    private CacheManager cacheManger = null;

    private boolean createCacheIfNotFound = true;

//    @FieldSetter("${cacheConfig}")
//    private String cacheConfig;

//    @Initializer
//    public void initialize(String cacheConfig){
//        if(cacheManger == null){
//            cacheManger = CacheManager.create(cacheConfig);
//        }
//    }

//    /**
//     * 初始化缓存配置文件
//     * @param cacheConfigUrl
//     */
//    @Override
//    public void initCache(String cacheConfigUrl){
//        //加锁防止并发时重复初始化
//        synchronized(this){
//            if(cacheManger == null){
//                cacheManger = CacheManager.create(cacheConfigUrl);
//            }
//        }
//    }

    public EhcacheServiceImpl(InputStream inputStream){
        if(cacheManger == null){
            cacheManger = CacheManager.create(inputStream);
        }
    }

    @Override
    public void clearCacheByKey(String cacheName, String cacheKey) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.remove(cacheKey);
    }

    @Override
    public void clearCacheByName(String cacheName) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.removeAll();
    }

    @Override
    public void clearAllCache() {
        if(cacheManger != null){
            cacheManger.clearAll();
        }
    }

    @Override
    public void putCache(String cacheName, String cacheKey, Object value) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.put(new Element(cacheKey, value));

    }

    @Override
    public Object getCacheValue(String cacheName, String cacheKey) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return null;
        }
        Element element = cache.get(cacheKey);
        if (element == null || element.isExpired()) {
            return null;
        }
        return element.getObjectValue();
    }

    @Override
    public Cache getCache(String cacheName) {
        Cache cache = cacheManger.getCache(cacheName);
        if (cache == null && createCacheIfNotFound) {
            cache = (Cache) cacheManger.addCacheIfAbsent(cacheName);
        }
        if (cache == null) {
            LOGGER.error("EHCache: cache not config and not auto created, cacheName=" + cacheName);
        }
        return cache;
    }

    @Override
    public void putCacheExpire(String cacheName, String cacheKey, Object value, int timeToLiveSeconds) {
        Cache cache = getCache(cacheName);
        if (cache == null) {
            return;
        }
        //参数：缓存key + 缓存值 + 空闲多少秒 + 存活多少秒
        cache.put(new Element(cacheKey, value, timeToLiveSeconds,timeToLiveSeconds));
    }

    @Override
    public void removeAllCacheName() {
        if(cacheManger != null){
            cacheManger.removeAllCaches();
        }
    }
}
