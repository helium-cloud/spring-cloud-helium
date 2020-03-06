package org.helium.rpc.endpoints;


public class HttpsEndpoint extends TcpEndpoint {
	public static final String PROTOCOL = "https";
	public static final String LOCALHOST = "127.0.0.1";

	public HttpsEndpoint(String host, int port) {
		super(host, port);
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	public HttpsEndpoint getLocalizedEndpoint() {
		HttpsEndpoint te = new HttpsEndpoint(LOCALHOST, getPort());
		return te;
	}
}
