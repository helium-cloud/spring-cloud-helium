package org.helium.rpc.client;

import org.helium.rpc.channel.RpcEndpoint;

import java.util.function.Supplier;

/**
 * Created by Coral on 9/10/16.
 */
public interface RpcProtocolResolver {
	/**
	 * 获取协议
	 *
	 * @return
	 */
	String getProtocol();

	/**
	 * RpcServiceUrl
	 *
	 * @param url
	 * @return
	 */
	ResolveResult resolveServiceUrl(RpcServiceUrl url);

	class ResolveResult {
		private String serviceName;
		private Supplier<RpcEndpoint> router;

		public String getServiceName() {
			return serviceName;
		}

		public void setServiceName(String serviceName) {
			this.serviceName = serviceName;
		}

		public Supplier<RpcEndpoint> getRouter() {
			return router;
		}

		public void setRouter(Supplier<RpcEndpoint> router) {
			this.router = router;
		}
	}
}
