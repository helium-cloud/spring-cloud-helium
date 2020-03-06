package org.helium.redis.widgets.redis.client;

import redis.clients.jedis.Jedis;
import redis.clients.util.Pool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yibo on 2017-2-10.
 */


public class HashRouteProvider2 implements IRouteProvider2<CFG_RedisSentinels, Pool<Jedis>> {

    List<CFG_RedisSentinels> nodes;
    private CFG_RedisSentinels[] hash;
    private Pool<Jedis>[] hashPools;
    private List<Pool<Jedis>> standAlonePools;

    public HashRouteProvider2(String roleName, List<CFG_RedisSentinels> nodes) {
        nodes = nodes.stream().filter(i -> i.isEnabled() && i.getRoleName().equals(roleName)).sorted().collect(Collectors.toList());
        this.nodes = nodes;
        hash = new CFG_RedisSentinels[nodes.stream().mapToInt(it -> it.getWeight()).sum()];
        hashPools = new Pool[hash.length];
        standAlonePools = new ArrayList<Pool<Jedis>>();
        int next = 0;
        for (CFG_RedisSentinels node : nodes) {
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
    public List<CFG_RedisSentinels> getNodes() {
        return this.nodes;
    }

    @Override
    public String resolve(RedisKey2 key) {
        if (hash.length == 0)
            return null;

        int keys = key.getHashValueForRoute();
        keys = keys >= 0 ? keys : -keys;
        int r = (int) (keys % hash.length);
        return hash[r].getAddrs();
    }

    @Override
    public Pool<Jedis> resolvePool(RedisKey2 key) {
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
    public int resolvePoolId(RedisKey2 key) {
        int keys = key.getHashValueForRoute();
        keys = keys >= 0 ? keys : -keys;
        int r = (int) (keys % hash.length);
        return r;
    }
}
