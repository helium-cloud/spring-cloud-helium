/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-12
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.inproc;

import org.helium.rpc.channel.RpcClientChannel;
import org.helium.rpc.channel.RpcEndpoint;

/**
 * 进程内RpcEndpoint, 单例, 仅有这一个
 * <p>
 * Created by Coral
 */
public final class RpcInprocEndpoint extends RpcEndpoint {
	public static final RpcInprocEndpoint INSTANCE = new RpcInprocEndpoint();

	private RpcInprocEndpoint() {
	}

	/*
	 * @see org.helium.route.Uri#hashCode()
	 */

	/**
	 * 所有RpcInprocEndpoint均相等
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		return 1;
	}

	/*
	 * @see org.helium.route.Uri#equals(java.lang.Object)
	 */

	/**
	 * 所有RpcInprocEndpoint均相等
	 *
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof RpcInprocEndpoint);
	}

	/*
	 * @see org.helium.rpc.RpcEndpoint#getProtocol()
	 */

	/**
	 * {在这里补充功能说明}
	 *
	 * @return
	 */
	@Override
	public String getProtocol() {
		return "inproc";
	}

	/*
	 * @see org.helium.route.Uri#toString()
	 */

	/**
	 * {在这里补充功能说明}
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return "inproc://";
	}

	/*
	 * @see org.helium.rpc.RpcEndpoint#getClientChannel()
	 */

	/**
	 * {在这里补充功能说明}
	 *
	 * @return
	 */
	@Override
	public RpcClientChannel getClientChannel() {
		return RpcInprocClientChannel.INSTANCE;
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
		// DO NOTHING
	}
}
