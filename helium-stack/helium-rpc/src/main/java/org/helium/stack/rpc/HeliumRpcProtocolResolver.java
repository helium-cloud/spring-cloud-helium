package org.helium.stack.rpc;


import org.helium.framework.BeanContext;
import org.helium.framework.route.BeanEndpoint;
import org.helium.framework.spi.BeanReference;
import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.client.RpcProtocolResolver;
import org.helium.rpc.client.RpcServiceUrl;


/**
 * Created by Leon on 9/10/16.
 */
public class HeliumRpcProtocolResolver implements RpcProtocolResolver {
	public HeliumRpcProtocolResolver() {
	}

	@Override
	public String getProtocol() {
		return "helium";
	}

	@Override
	public ResolveResult resolveServiceUrl(RpcServiceUrl url) {
		String beanId = url.getHost();

		ResolveResult result = new ResolveResult();
		result.setServiceName(beanId);

		BeanContext bc = BeanContext.getContextService().getBean(beanId.replace(".", ":"));
		if (bc == null) {
			throw new IllegalArgumentException("BeanNotFound:" + beanId);
		}

		if (bc instanceof BeanReference) {
			BeanReference ref = (BeanReference)bc;
			result.setRouter(() -> {
				BeanEndpoint ep = ref.getRouter().routeBean();
				if (ep == null) {
					throw new RuntimeException("No server for:" + url);
				}
				return RpcEndpointFactory.parse(ep.getServerUrl().getUrl());
			});
		}
		return result;
	}
}
