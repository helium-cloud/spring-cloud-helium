/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2012-5-29
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.duplex;

import org.helium.rpc.channel.RpcConnectionReal;
import org.helium.rpc.client.RpcClientTransactionHandler;
import org.helium.rpc.client.RpcInvocationHandler;
import org.helium.rpc.client.RpcMethodStub;
import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.server.RpcService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 双共连接中在服务器端保存双工Agent
 * <p>
 * Created by Coral
 */
public class RpcDuplexClientAgent {
	private RpcConnectionReal connection;
	private Map<String, Object> contexts;

	public RpcConnectionReal getConnection() {
		return connection;
	}

	public RpcDuplexClientAgent(RpcServerContext ctx) {
		connection = (RpcConnectionReal) ctx.getConnection();
	}

	public static RpcDuplexClientAgent getCurrent(RpcServerContext ctx) {
		return (RpcDuplexClientAgent) ctx.getConnection().getAttachment();
	}

	public <I> I getService(Class<I> intf) {
		RpcService sa = intf.getAnnotation(RpcService.class);
		if (sa == null) {
			throw new IllegalArgumentException("@RpcService not found in:" + intf);
		}
		String serviceName = sa.value();
		Map<String, RpcMethodStub> stubs = new HashMap<String, RpcMethodStub>();
		for (Method method : intf.getMethods()) {
			RpcMethod ma = method.getAnnotation(RpcMethod.class);
			String methodName = ma != null ? ma.value() : method.getName();
			RpcMethodStub stub = getMethodStub(serviceName, methodName);
			if (!void.class.equals(method.getReturnType())) {
				stub.setResultsClass(method.getReturnType());
			}
			stubs.put(method.getName(), stub);
		}
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		InvocationHandler handler = new RpcInvocationHandler(stubs);
		return (I) Proxy.newProxyInstance(cl, new Class<?>[]{intf}, handler);
	}

	public RpcMethodStub getMethodStub(String service, String method) {
		RpcClientTransactionHandler handler = new RpcClientTransactionHandlerDuplexCallback(connection, service, method);
		return new RpcMethodStub(handler);
	}

	public Object getContext(String key) {
		if (contexts == null) {
			return null;
		} else {
			return contexts.get(key);
		}
	}

	public void setContext(String key, Object value) {
		if (contexts == null) {
			contexts = new Hashtable<String, Object>();
		}
		contexts.put(key, value);
	}
}
 