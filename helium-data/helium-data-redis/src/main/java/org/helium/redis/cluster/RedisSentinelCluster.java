package org.helium.redis.cluster;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.redis.RedisSentinelClientImpl;

/**
 * Created by Leon on 1/5/17.
 */
@FieldLoaderType(loaderType = RedisSentinelClusterLoader.class)
public class RedisSentinelCluster {
	private RedisSentinelClientImpl client;

	RedisSentinelCluster(RedisSentinelClientImpl client) {
		this.client = client;
	}

	public RedisSentinelClientImpl getClient() {
		return client;
	}
}
