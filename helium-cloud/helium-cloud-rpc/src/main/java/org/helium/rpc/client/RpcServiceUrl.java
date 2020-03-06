package org.helium.rpc.client;

import com.feinno.superpojo.util.StringUtils;
import org.helium.util.StringParser;

/**
 * Created by Coral on 9/10/16.
 */
public class RpcServiceUrl {
	private String protocol;
	private String host;
	private String serviceName;

	public String getProtocol() {
		return protocol;
	}

	public String getHost() {
		return host;
	}

	public String getServiceName() {
		return serviceName;
	}

	public static RpcServiceUrl parse(String serviceUrl) {
		StringParser parser = new StringParser(serviceUrl);

		RpcServiceUrl url = new RpcServiceUrl();
		url.protocol = parser.getToken("://");
		url.host = parser.getToken("/");
		url.serviceName = parser.getLast();
		return url;
	}

	@Override
	public String toString() {
		if (!StringUtils.isNullOrEmpty(serviceName)) {
			return protocol + "://" + host + "/" + serviceName;
		} else {
			return protocol + "://" + host;
		}
	}
}
