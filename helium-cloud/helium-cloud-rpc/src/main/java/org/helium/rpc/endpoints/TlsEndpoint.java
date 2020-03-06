package org.helium.rpc.endpoints;

import java.net.InetSocketAddress;


public class TlsEndpoint extends ServiceEndpoint {
	public static final String PROTOCOL = "tls";
	public static final String LOCALHOST = "127.0.0.1";

	private InetSocketAddress address;

	public TlsEndpoint(String host, int port) {
		address = InetSocketAddress.createUnresolved(host, port);
	}

	public String getHost() {
		return address.getHostString();
	}

	public int getPort() {
		return address.getPort();
	}

	@Override
	public String getValue() {
		return address.toString();
	}

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	public TlsEndpoint getLocalizedEndpoint() {
		TlsEndpoint te = new TlsEndpoint(LOCALHOST, getPort());
		return te;
	}
}
