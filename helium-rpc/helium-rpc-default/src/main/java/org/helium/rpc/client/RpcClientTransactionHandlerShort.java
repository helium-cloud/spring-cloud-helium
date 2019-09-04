/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 2, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.client;

import org.helium.rpc.channel.*;
import org.helium.util.ServiceEnviornment;

/**
 * 用于短连接的RpcClientTransactionHandler
 * <p>
 * Created by Coral
 */
public class RpcClientTransactionHandlerShort implements RpcClientTransactionHandler {
	private RpcEndpoint ep;
	private String service;
	private String method;

	public RpcClientTransactionHandlerShort(RpcEndpoint ep, String service, String method) {
		this.ep = ep;
		this.service = service;
		this.method = method;
	}

	public RpcClientTransaction createTransaction() {
		RpcConnection conn = ep.getClientChannel().createConnection(ep);
		RpcClientMethodCache cache = RpcClientMethodManager.INSTANCE.getMethodCache(ep, service, method);

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
