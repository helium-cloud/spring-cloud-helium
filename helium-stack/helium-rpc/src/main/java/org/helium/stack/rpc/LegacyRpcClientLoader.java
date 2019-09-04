package org.helium.stack.rpc;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.KeyValueNode;
import org.helium.framework.entitys.SetterNode;
import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.channel.RpcEndpoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 6/15/15.
 */
public class LegacyRpcClientLoader implements FieldLoader {
	@Override
	public Object loadField(SetterNode node) {
		String path = node.getInnerText();
		ConfigProvider configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		String text = configProvider.loadText(path);
		LegacyRpcConfiguration rpcConfiguration = new LegacyRpcConfiguration();
		rpcConfiguration.parseXmlFrom(text);

		String service = rpcConfiguration.getService();
		List<RpcEndpoint> eps = new ArrayList<>();
		for (KeyValueNode e: rpcConfiguration.getEndpoints()) {
			RpcEndpoint ep = RpcEndpointFactory.parse(e.getValue());
			eps.add(ep);
		}
		return new LegacyRpcClientImpl(service, eps);
	}
}