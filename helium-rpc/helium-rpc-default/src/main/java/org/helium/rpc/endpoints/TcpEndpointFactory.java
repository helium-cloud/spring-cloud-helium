package org.helium.rpc.endpoints;

import org.helium.util.Outer;
import org.helium.util.StringUtils;


public class TcpEndpointFactory implements ServiceEndpointFactory {

	@Override
	public String getProtocol() {
		return TcpEndpoint.PROTOCOL;
	}

	@Override
	public ServiceEndpoint parseEndpoint(String str) {
		Outer<String> addr = new Outer<String>();
		Outer<String> port = new Outer<String>();
		if (StringUtils.splitWithFirst(str, ":", addr, port)) {
			return new TcpEndpoint(addr.value(), Integer.parseInt(port.value()));
		} else {
			throw new IllegalArgumentException("BadTcpUri:" + str);
		}
	}
}
