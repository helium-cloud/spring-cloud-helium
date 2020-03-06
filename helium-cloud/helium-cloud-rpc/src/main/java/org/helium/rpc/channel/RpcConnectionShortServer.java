/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 7, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import java.io.IOException;

/**
 * Rpc服务器端短连接
 * <p>
 * Created by Coral
 */
public abstract class RpcConnectionShortServer extends RpcConnection {
	private RpcServerChannel channel;
	private RpcServerTransaction serverTx;

	protected RpcConnectionShortServer(RpcServerChannel channel, RpcEndpoint ep) {
		super(ep, false);
		this.channel = channel;
	}

	protected RpcServerTransaction getTransaction() {
		return serverTx;
	}

	@Override
	public void sendRequest(RpcClientTransaction tx, RpcRequest request) {
		throw new IllegalStateException("RpcConectionShortServer should't sendRequest()");
	}

	@Override
	public void sendResponse(RpcServerTransaction tx, RpcResponse response) throws IOException {
		doSendResponse(response);
	}

	@Override
	public void responseReceived(RpcResponse response) {
		throw new IllegalStateException("RpcConectionShortServer should't raise responseReceived()");
	}

	@Override
	public void requestReceived(RpcRequest request) {
		serverTx = new RpcServerTransaction(this, request);
		RpcRequestHeader h = request.getHeader();
		RpcServerMethodCache cache = RpcServerMethodManager.INSTANCE.getMethodCache(
				h.getFromComputer(), h.getFromService(), h.getToService(), h.getToMethod());
		serverTx.setMethodCache(cache);

		channel.getTransactionCreated().fireEvent(serverTx);
	}

	public abstract void doSendResponse(RpcResponse response) throws IOException;
}
