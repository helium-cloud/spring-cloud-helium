/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 1, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

/**
 * 客户端信道抽象基类
 * <p>
 * Created by Coral
 */
public abstract class RpcClientChannel {
	private RpcChannelSettings settings;

	/**
	 * 获取设置
	 *
	 * @return
	 */
	public RpcChannelSettings getSettings() {
		return settings;
	}

	public String getProtocol() {
		return settings.getProtocol();
	}

	protected RpcClientChannel(RpcChannelSettings settings) {
		this.settings = settings;
	}

	/**
	 * 创建一个连接
	 *
	 * @param ep
	 * @return
	 */
	public abstract RpcConnection createConnection(RpcEndpoint ep);

	/**
	 * 获取一个连接, 无可用连接时再创建, 对于短连接Channel等同与创建连接
	 *
	 * @param ep
	 * @return
	 */
	public RpcConnection getConnection(RpcEndpoint ep) {
		return createConnection(ep);
	}

	/**
	 * 创建一组连接
	 *
	 * @param ep
	 * @param count
	 * @return
	 */
	public RpcConnectionGroup getConnectionGroup(RpcEndpoint ep, int count) {
		throw new AbstractMethodError("Not implemented in base class");
	}
}
