/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 4, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.duplex;

import org.helium.rpc.channel.*;
import org.helium.rpc.client.RpcClientTransactionHandler;
import org.helium.util.ServiceEnviornment;

/**
 * 用于双共连接的RpcClientTransactionHandler
 * <p>
 * Created by Coral
 */
public class RpcClientTransactionHandlerDuplex implements RpcClientTransactionHandler {
	private RpcDuplexClient client;
	private String service;
	private String method;

	public RpcClientTransactionHandlerDuplex(RpcDuplexClient client, String service, String method) {
		this.client = client;
		this.service = service;
		this.method = method;
	}

	@Override
	public RpcClientTransaction createTransaction() {
		RpcConnectionReal conn = client.getConnection();
		if (conn == null) {
			throw new IllegalStateException("Not Connected");
		}
		if (conn.getStatus() != RpcConnectionStatus.CONNECTED) {
			throw new IllegalStateException("Not Connected:" + conn.getStatus());
		}

		RpcClientMethodCache cache = RpcClientMethodManager.INSTANCE.getMethodCache(RpcDuplexCallbackEndpoint.INSTANCE, service, method);

		RpcClientTransaction tx = conn.createTransaction();
		tx.setMethodCache(cache);
		RpcRequestHeader header = tx.getRequest().getHeader();
		header.setToService(service);
		header.setToMethod(method);
		header.setFromComputer(ServiceEnviornment.getComputerName());
		header.setFromService(ServiceEnviornment.getServiceName());

		return tx;
	}
}
