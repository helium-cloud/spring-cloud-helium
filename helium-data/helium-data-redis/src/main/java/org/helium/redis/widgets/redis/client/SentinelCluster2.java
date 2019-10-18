package org.helium.redis.widgets.redis.client;

import org.helium.framework.annotations.FieldLoaderType;

/**
 * Created by yibo on 2017-3-29.
 */

@FieldLoaderType(loaderType = SentinelClusterLoader2.class)
public class SentinelCluster2 {
    private RedisSentinelClient2 client;

    SentinelCluster2(RedisSentinelClient2 client) {
        this.client = client;
    }

    public RedisSentinelClient2 getClient() {
        return client;
    }


}
