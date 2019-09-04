package org.helium.rpc.client;

import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.channel.RpcEndpoint;

/**
 * Created by Coral on 9/10/16.
 */
public class RpcProtocolResolverSimple implements RpcProtocolResolver {
	private String protocol;

	@Override
	public String getProtocol() {
		return protocol;
	}

	public RpcProtocolResolverSimple(String protocol) {
		this.protocol = protocol;
	}

	@Override
	public ResolveResult resolveServiceUrl(RpcServiceUrl url) {
		RpcEndpoint ep = RpcEndpointFactory.parse(url.getProtocol() + "://" + url.getHost());
		ResolveResult r = new ResolveResult();
		r.setServiceName(url.getServiceName());
		r.setRouter(() -> ep);
		return r;
	}
}
