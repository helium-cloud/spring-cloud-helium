package org.helium.ehcache;

import org.helium.ehcache.imp.EhcacheServiceImpl;
import org.helium.ehcache.utils.EhCacheLoader;
import org.helium.framework.annotations.FieldLoaderType;

/**
 * 缓存客户端
 *
 * @author wudashuai
 * @date 2018-08-31
 */
@FieldLoaderType(loaderType = EhCacheLoader.class)
public class EhcacheClient {

	private EhcacheServiceImpl client;

	public EhcacheClient(EhcacheServiceImpl client) {
		this.client = client;
	}

	public EhcacheServiceImpl getClient() {
		return client;
	}
}
