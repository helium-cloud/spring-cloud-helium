package org.helium.rpc.endpoints;


public class InprocEndpointFactory implements ServiceEndpointFactory {
	@Override
	public String getProtocol() {
		return InprocEndpoint.PROTOCOL;
	}

	/**
	 * 只接受形如 "inproc://"格式的输入
	 */
	@Override
	public ServiceEndpoint parseEndpoint(String str) {
		if (str != null && str.equals("")) {
			return InprocEndpoint.INSTANCE;
		} else {
			throw new IllegalArgumentException("Unexcepted inproc endpoint=" + str);
		}
	}
}
