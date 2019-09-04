package org.helium.redis.sentinel.router.provider;

import org.helium.redis.sentinel.RedisKey;

import java.util.Collection;
import java.util.List;


public interface IRouteProvider<T, E> {
    /**
     * 获取所有节点
     *
     * @return
     */
    List<T> getNodes();


    /**
     * 通过key 获得相应的 连接串
     *
     * @param key
     * @return
     */
    String resolve(RedisKey key);


    /**
     * 通过key 获取资源对应的addr字段配置.
     *
     * @param key
     * @return
     */
    E resolvePool(RedisKey key);


    /**
     * 通过key 获取对应id,可能是一致性hash 环上的id, 也可能是对索引
     *
     * @param key
     * @return
     */
    int resolvePoolId(RedisKey key);


    Collection<E> getPoolNodes();

}
