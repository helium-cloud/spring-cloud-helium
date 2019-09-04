package org.helium.rpc.transparecy.impl;

import org.helium.rpc.channel.RpcEndpoint;
import org.helium.rpc.channel.RpcException;
import org.helium.rpc.channel.RpcFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 2015/5/13.
 */
public class RpcServiceMapperHandler implements InvocationHandler {

	final static Logger logger = LoggerFactory.getLogger(RpcServiceMapperHandler.class);

	private Map<Method, RpcMethodClientStub> stubs;
	private RpcEndpoint endpoint;
	private String codecName;

	public RpcServiceMapperHandler(RpcEndpoint endpoint, String clientCodecName) {
		this.codecName = clientCodecName;
		this.stubs = new HashMap<>();
		this.endpoint = endpoint;
	}

	private synchronized RpcMethodClientStub getRpcMethodClientStub(Method method) {
		RpcMethodClientStub result = stubs.get(method);
		if (result == null) {
			result = new RpcMethodClientStub(method, endpoint, codecName);
			stubs.put(method, result);
		}
		return result;
	}

	@Override
	public Object invoke(Object o, Method method, Object[] args) throws Throwable {
		RpcMethodClientStub stub = getRpcMethodClientStub(method);
		RpcFuture future = stub.invoke(args);

		Class<?> resultClass = stub.getResultClass();
		if (resultClass == null) {
			future.await();
			return null;
		} else if (RpcFuture.class == resultClass) {
			return future;
		} else {
			try {
				return future.syncGet(stub.getResultClass());
			} catch (RpcException e) {
				Throwable error = e.getCause();
				if (error != null) {
					appendMessage(error, e.getMessage());
					throw error;
				} else {
					throw e;
				}
			}
		}
	}

	private static Field messageRef;

	static {
		try {
			messageRef = Throwable.class.getDeclaredField("detailMessage");
			messageRef.setAccessible(true);
		} catch (NoSuchFieldException e) {
			logger.error("init Throwable detailMessage reflect field fault.", e);
		}
	}

	static void appendMessage(Throwable error, String message) {
		if (error == null || message == null) return;
		try {
			StringBuilder sb = new StringBuilder();
			String sourceMessage = (String) messageRef.get(error);
			if (sourceMessage != null) {
				sb.append(sourceMessage).append(" >>> ");
			}
			sb.append("( ").append(message).append(" )");
			messageRef.set(error, sb.toString());
		} catch (IllegalAccessException e) {
			logger.error("init Throwable detailMessage reflect field fault.", e);
		}
	}
}
