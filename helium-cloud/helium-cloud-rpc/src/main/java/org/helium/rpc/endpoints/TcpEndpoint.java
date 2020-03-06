package org.helium.rpc.endpoints;

import java.net.InetSocketAddress;


public class TcpEndpoint extends ServiceEndpoint {
	public static final String PROTOCOL = "tcp";
	public static final String LOCALHOST = "127.0.0.1";

	private InetSocketAddress address;

	public TcpEndpoint(String host, int port) {
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

	public TcpEndpoint getLocalizedEndpoint() {
		TcpEndpoint te = new TcpEndpoint(LOCALHOST, getPort());
		return te;
	}
}
