/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.client;

import org.helium.rpc.channel.RpcException;
import org.helium.rpc.channel.RpcFuture;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;


/**
 * 实现RPC客户端透明代理
 *
 * @author Coral
 */
public class RpcInvocationHandler implements InvocationHandler {
	private Map<String, RpcMethodStub> stubs;

	public RpcInvocationHandler(Map<String, RpcMethodStub> stubs) {
		this.stubs = stubs;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws RpcException {
		RpcMethodStub stub = stubs.get(method.getName());
		RpcFuture future;
		if (args != null) {
			future = stub.invoke(args[0]);
		} else {
			future = stub.invoke(null);
		}

		if (stub.getResultsClass() == null) {
			future.await();
			return null;
		} else {
			return future.syncGet(stub.getResultsClass());
		}
	}
}
