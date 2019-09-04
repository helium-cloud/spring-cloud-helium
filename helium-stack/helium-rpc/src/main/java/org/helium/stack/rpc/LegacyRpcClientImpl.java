package org.helium.stack.rpc;


import org.helium.rpc.channel.RpcEndpoint;
import org.helium.rpc.client.RpcMethodStub;
import org.helium.rpc.client.RpcProxyFactory;

import java.util.List;

/**
 * Created by Coral on 6/15/15.
 */
class LegacyRpcClientImpl implements LegacyRpcClient {
	private String service;
	private RpcEndpoint[] endpoints;
	private int count;
	private int p;


	public LegacyRpcClientImpl(String service, List<RpcEndpoint> endpoints) {
		this.service = service;
		this.count = endpoints.size();
		this.endpoints = new RpcEndpoint[this.count];
		for (int i = 0; i < this.count; i++) {
			this.endpoints[i] = endpoints.get(i);
		}
	}

	@Override
	public RpcMethodStub getMethodStub(String method) {
		RpcEndpoint ep;
		synchronized (this) {
			p++;
			if (p >= count) {
				p = 0;
			}
			ep = endpoints[p];
		}

		return RpcProxyFactory.getMethodStub(ep, service, method);
	}
}
