package org.helium.redis.cluster;

import com.alibaba.fastjson.JSONObject;
import com.feinno.superpojo.util.StringUtils;
import org.helium.data.sharding.ShardedDataSource;
import org.helium.database.ConnectionString;
import org.helium.framework.annotations.FieldLoaderType;
import org.helium.framework.configuration.legacy.spi.ObjectHelper;
import org.helium.redis.RedisClient;
import org.helium.redis.cluster.lambda.LambdaActionBool;
import org.helium.redis.cluster.lambda.LambdaActionObject;
import org.helium.redis.cluster.lambda.LambdaImpl;
import org.helium.redis.sentinel.helper.RedisHelper;
import org.helium.redis.spi.RedisManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.*;

/**
 * Created by Leon on 8/3/16.
 */
@FieldLoaderType(loaderType = RedisClusterLoader.class)
public class RedisCluster extends ShardedDataSource<String, RedisClient> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCluster.class);

    private String name;
    private RedisClient[] clientSockets;


    RedisCluster(String name, List<RedisClusterItem> clusters) throws Exception {
        this.name = name;
        clusters = validateClusters(name, clusters);

        int totalWeight = 0;
        Map<Integer, RedisClient> redisClients = new HashMap<>();
        for (RedisClusterItem c : clusters) {
            if (!StringUtils.isNullOrEmpty(c.getRouteValue())) {
                try {
                    totalWeight += c.getWeight();
                    Properties properties = JSONObject.parseObject(c.getRouteValue(), Properties.class);
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    properties.store(outputStream, "rediscluster");
                    RedisClient client = RedisManager.INSTANCE.getAndUpdateRedisClient(name + "." + c.getNodeOrder(),
                            new String(outputStream.toByteArray()));
                    redisClients.put(c.getNodeOrder(), client);
                } catch (Exception e) {
                    LOGGER.error("RedisCluster Exception:{}", c.getRouteValue(), e);
                }
            } else {
                LOGGER.error("RedisCluster getRouteValue Must Not Be Null:{}", name);
            }

        }

        clientSockets = new RedisClient[totalWeight];

        int next = 0;
        RedisClusterItem node;
        for (Iterator i$ = clusters.iterator(); i$.hasNext(); next += node.getWeight()) {
            node = (RedisClusterItem) i$.next();
            RedisClient redis = redisClients.get(node.getNodeOrder());
            for (int j = 0; j < node.getWeight(); j++) {
                this.clientSockets[next + j] = redis;
            }
        }
    }

    @Override
    protected RedisClient loadDataSource(String dsName) {
        throw new UnsupportedOperationException("BY DESIGN");
    }

    @Override
    protected RedisClient getSharding(RedisClient redisClient, String shardingName) {
        throw new UnsupportedOperationException("BY DESIGN");
    }

    @Override
    public RedisClient getSharding(String shardingKey) {
        if (StringUtils.isNullOrEmpty(shardingKey)) {
            throw new IllegalArgumentException("empty key");
        }
        int k = ObjectHelper.compatibleGetHashCode(shardingKey);
        k = k >= 0 ? k : -k;
        int r = k % clientSockets.length;
        return clientSockets[r];
    }

    public RedisClient getShardingByLong(long key) {
        int k = ObjectHelper.compatibleGetHashCode(key);
        k = k >= 0 ? k : -k;
        int r = k % clientSockets.length;
        return clientSockets[r];
    }

    private static List<RedisClusterItem> validateClusters(String roleName, List<RedisClusterItem> nodes) {
        nodes = LambdaImpl.where(nodes, new LambdaActionBool<RedisClusterItem>() {
            public boolean run(RedisClusterItem item) {
                return item.isEnabled();
            }
        });
        nodes = LambdaImpl.orderBy(nodes, new LambdaActionObject<RedisClusterItem, Integer>() {
            public Integer run(RedisClusterItem item) {
                return Integer.valueOf(item.getNodeOrder());
            }
        });
        if (nodes.size() > 0) {
            List tmpGroupList = LambdaImpl.groupBy(nodes, new LambdaActionObject<RedisClusterItem, Integer>() {
                public Integer run(RedisClusterItem item) {
                    return Integer.valueOf(item.getNodeOrder());
                }
            });
            tmpGroupList = LambdaImpl.where(tmpGroupList, new LambdaActionBool<List<RedisClusterItem>>() {
                public boolean run(List<RedisClusterItem> item) {
                    return item != null && item.size() > 1;
                }
            });
            if (tmpGroupList != null && tmpGroupList.size() > 0) {
                throw new RuntimeException("RedisClusterItem duplicated RouteId");
            }

            RedisClusterItem f = (RedisClusterItem) LambdaImpl.max(nodes, new LambdaActionObject<RedisClusterItem, Integer>() {
                public Integer run(RedisClusterItem item) {
                    return Integer.valueOf(item.getNodeOrder());
                }
            });

            if (f.getNodeOrder() != nodes.size()) {
                throw new RuntimeException("RedisClusterItem Count!= max Order");
            }
        }
        return nodes;
    }
}
