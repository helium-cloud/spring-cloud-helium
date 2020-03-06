package org.helium.rpc.endpoints;

public class HttpEndpoint extends TcpEndpoint {
	public static final String PROTOCOL = "http";
	public static final String LOCALHOST = "127.0.0.1";

	public HttpEndpoint(String host, int port) {
		super(host, port);
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	public HttpEndpoint getLocalizedEndpoint() {
		HttpEndpoint te = new HttpEndpoint(LOCALHOST, getPort());
		return te;
	}
}
