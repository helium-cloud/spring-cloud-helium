/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Jun 23, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.duplex;

import org.helium.rpc.channel.*;
import org.helium.rpc.client.RpcClientTransactionHandler;
import org.helium.util.ServiceEnviornment;

/**
 * {在这里补充类的功能说明}
 * <p>
 * Created by Coral
 */
public class RpcClientTransactionHandlerDuplexCallback implements RpcClientTransactionHandler {
	private RpcConnectionReal connection;
	private String service;
	private String method;

	public RpcClientTransactionHandlerDuplexCallback(RpcConnectionReal connection, String service, String method) {
		this.connection = connection;
		this.service = service;
		this.method = method;
	}

	@Override
	public RpcClientTransaction createTransaction() {
		if (connection.getStatus() != RpcConnectionStatus.CONNECTED) {
			throw new IllegalStateException("Bad Connection Status!:" + connection.getStatus());
		}

		RpcClientMethodCache cache = RpcClientMethodManager.INSTANCE.getMethodCache(RpcDuplexCallbackEndpoint.INSTANCE, service, method);

		RpcClientTransaction tx = connection.createTransaction();
		tx.setMethodCache(cache);
		RpcRequestHeader header = tx.getRequest().getHeader();
		header.setToService(service);
		header.setToMethod(method);
		header.setFromComputer(ServiceEnviornment.getComputerName());
		header.setFromService(ServiceEnviornment.getServiceName());
		return tx;
	}
}
