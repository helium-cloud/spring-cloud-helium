package org.helium.redis.sentinel.router.provider;

import org.helium.redis.sentinel.RedisKey;
import org.helium.redis.sentinel.RedisSentinelsCfg;
import org.helium.redis.sentinel.helper.RedisHelper;
import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * hash的RouteProvider 实现
 */
public class HashRouteProvider implements IRouteProvider<RedisSentinelsCfg, Pool<Jedis>> {

    List<RedisSentinelsCfg> nodes;
    private RedisSentinelsCfg[] hash;
    private Pool<Jedis>[] hashPools;
    private List<Pool<Jedis>> standAlonePools;

    public HashRouteProvider(String roleName, List<RedisSentinelsCfg> nodes) {
        nodes = nodes.stream().filter(i -> i.isEnabled() && i.getRoleName().equals(roleName)).sorted().collect(Collectors.toList());
        this.nodes = nodes;
        hash = new RedisSentinelsCfg[nodes.stream().mapToInt(it -> it.getWeight()).sum()];
        hashPools = new Pool[hash.length];
        standAlonePools = new ArrayList<Pool<Jedis>>();
        int next = 0;
        for (RedisSentinelsCfg node : nodes) {
            Pool<Jedis> tmp = RedisHelper.getPool(node);
            standAlonePools.add(tmp);

            for (int i = 0; i < node.getWeight(); i++) {
                hash[next + i] = node;
                hashPools[next + i] = tmp;
            }
            next += node.getWeight();
        }
    }


    @Override
    public List<RedisSentinelsCfg> getNodes() {
        return this.nodes;
    }

    @Override
    public String resolve(RedisKey key) {
        if (hash.length == 0)
            return null;

        int keys = key.getHashValueForRoute();
        keys = keys >= 0 ? keys : -keys;
        int r = (int) (keys % hash.length);
        return hash[r].getAddrs();
    }

    @Override
    public Pool<Jedis> resolvePool(RedisKey key) {
        if (hash.length == 0)
            return null;

        int keys = key.getHashValueForRoute();
        keys = keys >= 0 ? keys : -keys;
        int r = (int) (keys % hash.length);
        return hashPools[r];
    }

    @Override
    public Collection<Pool<Jedis>> getPoolNodes() {
        return standAlonePools;
    }

    @Override
    public int resolvePoolId(RedisKey key) {
        int keys = key.getHashValueForRoute();
        keys = keys >= 0 ? keys : -keys;
        int r = (int) (keys % hash.length);
        return r;
    }
}
