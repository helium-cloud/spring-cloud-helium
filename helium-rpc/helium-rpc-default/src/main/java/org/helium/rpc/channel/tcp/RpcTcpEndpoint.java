/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import org.helium.rpc.channel.RpcClientChannel;
import org.helium.rpc.channel.RpcEndpoint;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 基于TCP传输的RPC终结点
 *
 * @author Coral
 */
public class RpcTcpEndpoint extends RpcEndpoint {
	private InetSocketAddress address;

	/**
	 * 通过反射创建, 必须存在默认构造函数
	 */
	public RpcTcpEndpoint() {
	}

	public RpcTcpEndpoint(String ip, int port) {
		this.address = new InetSocketAddress(ip, port);
	}

	public RpcTcpEndpoint(InetSocketAddress address) {
		this.address = address;
	}

	public RpcTcpEndpoint(SocketAddress address) {
		this.address = (InetSocketAddress) address;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	public static RpcTcpEndpoint parse(String uri) {
		if (uri.startsWith("tcp://"))
			return parse(uri, "tcp://".length());
		else if (uri.startsWith("tcp:"))
			return parse(uri, "tcp:".length());
		else
			throw new IllegalArgumentException("Unreconized tcp uri:" + uri);
	}

	public static RpcTcpEndpoint parse(String uri, int start) {
		RpcTcpEndpoint ep = new RpcTcpEndpoint();
		ep.parseWith(uri.substring(start));
		return ep;
	}

	@Override
	public String toString() {
		return "tcp://" + address.getAddress().getHostAddress() + ":" + address.getPort();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcTcpEndpoint other = (RpcTcpEndpoint) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
			return false;
		return true;
	}

	@Override
	public String getProtocol() {
		return "tcp";
	}

	@Override
	public RpcClientChannel getClientChannel() {
		return RpcTcpClientChannel.INSTANCE;
	}

	/*
	 * @see org.helium.rpc.RpcEndpoint#parseWithInner(java.lang.String)
	 */

	/**
	 * {在这里补充功能说明}
	 *
	 * @param strWithoutProtocol
	 */
	@Override
	protected void parseAddress(String strWithoutProtocol) {
		int p = strWithoutProtocol.lastIndexOf(':');
		int end = strWithoutProtocol.indexOf('/');

		if (end < 0)
			end = strWithoutProtocol.length();

		String ip = strWithoutProtocol.substring(0, p);
		String portstr = strWithoutProtocol.substring(p + 1, end);

		int port = Integer.parseInt(portstr);

		address = new InetSocketAddress(ip, port);
	}

	public SocketAddress getSocketAddress() {
		return this.address;
	}
}