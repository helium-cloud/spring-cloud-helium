/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-6
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import java.io.IOException;

/**
 * Rpc客户端短连接, 创建一个连接仅能发送一个Transaction, 连接不能复用
 * <p>
 * Created by Coral
 */
public abstract class RpcConnectionShortClient extends RpcConnection {
	private RpcClientTransaction clientTx;

	public RpcConnectionShortClient(RpcEndpoint ep) {
		super(ep, true);
	}

	protected RpcClientTransaction getTransaction() {
		return clientTx;
	}

	@Override
	public void sendRequest(RpcClientTransaction tx, RpcRequest request) throws IOException {
		this.clientTx = tx;
		doSendRequest(request);
	}

	@Override
	public void sendResponse(RpcServerTransaction tx, RpcResponse response) {
		throw new IllegalStateException("RpcConectionShortServer should't sendResponse()");
	}

	@Override
	public void requestReceived(RpcRequest request) {
		throw new IllegalStateException("RpcConectionShortServer should't raise requestReceived()");
	}

	@Override
	public void responseReceived(RpcResponse response) {
		clientTx.setResponse(response);
	}

	public abstract void doSendRequest(RpcRequest request) throws IOException;
}
