/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Aug 5, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

/**
 * 一组RpcConnectionReal, 用于长连接复用的模式
 * <p>
 * Created by Coral
 */
public class RpcConnectionGroup<E extends RpcConnectionReal> {
	private RpcEndpoint ep;
	private RpcClientChannel channel;
	private RpcConnectionReal[] connections;

	public RpcConnectionGroup(RpcConnectionGroup group, int count) {
	}

	public RpcConnectionGroup(RpcEndpoint ep, int count) {
		this.ep = ep;
		channel = ep.getClientChannel();
		connections = new RpcConnectionReal[count];
		for (int i = 0; i < count; i++) {
			connections[i] = (RpcConnectionReal) channel.createConnection(ep);
			connections[i].autoConnect();
		}
	}

	public RpcConnectionReal getConnection(int order) {
		if (connections[order].isUseable()) {
			return connections[order];
		} else {
			synchronized (this) {
				if (!connections[order].isUseable()) {
					connections[order] = (RpcConnectionReal) channel.createConnection(ep);
				}
			}
			return connections[order];
		}
	}
}
