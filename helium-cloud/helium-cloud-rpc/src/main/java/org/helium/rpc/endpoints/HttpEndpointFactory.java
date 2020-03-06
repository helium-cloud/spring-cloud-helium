package org.helium.rpc.endpoints;

import org.helium.util.Outer;
import org.helium.util.StringUtils;


public class HttpEndpointFactory implements ServiceEndpointFactory {

	@Override
	public String getProtocol() {
		return HttpEndpoint.PROTOCOL;
	}

	@Override
	public ServiceEndpoint parseEndpoint(String str) {
		Outer<String> addr = new Outer<String>();
		Outer<String> port = new Outer<String>();
		if (StringUtils.splitWithFirst(str, ":", addr, port)) {
			return new HttpEndpoint(addr.value(), Integer.parseInt(port.value()));
		} else {
			throw new IllegalArgumentException("BadHttpUri:" + str);
		}
	}

}
