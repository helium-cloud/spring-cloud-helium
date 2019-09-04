package org.helium.rpc.endpoints;

import java.net.InetSocketAddress;

public class UdpEndpoint extends ServiceEndpoint {
	public static final String PROTOCOL = "udp";
	public static final String LOCALHOST = "127.0.0.1";

	private InetSocketAddress address;

	public UdpEndpoint(String host, int port) {
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

	public UdpEndpoint getLocalizedEndpoint() {
		UdpEndpoint ue = new UdpEndpoint(LOCALHOST, getPort());
		return ue;
	}
}
