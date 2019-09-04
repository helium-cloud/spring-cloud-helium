/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Jun 23, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.duplex;

import org.helium.rpc.channel.RpcClientChannel;
import org.helium.rpc.channel.RpcEndpoint;

/**
 * 用于在整个体系中构造一个伪造的RpcEndpoint, 保证在统计阶段不造成过多的统计结果l
 * <p>
 * Created by Coral
 */
public final class RpcDuplexCallbackEndpoint extends RpcEndpoint {
	public static final RpcDuplexCallbackEndpoint INSTANCE = new RpcDuplexCallbackEndpoint();

	private RpcDuplexCallbackEndpoint() {
	}

	@Override
	protected void parseAddress(String strWithoutProtocol) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getProtocol() {
		return "duplex";
	}

	@Override
	public RpcClientChannel getClientChannel() {
		return null;
	}

	@Override
	public String toString() {
		return "duplex:///";
	}

	@Override
	public boolean equals(Object o) {
		return o != null && o instanceof RpcDuplexCallbackEndpoint;
	}

	@Override
	public int hashCode() {
		return "duplex".hashCode();
	}
}
