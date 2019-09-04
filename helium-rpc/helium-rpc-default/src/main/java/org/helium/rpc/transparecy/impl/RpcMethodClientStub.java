package org.helium.rpc.transparecy.impl;

import org.helium.rpc.channel.*;
import org.helium.rpc.client.RpcClientTransactionHandler;
import org.helium.rpc.client.RpcClientTransactionHandlerDirect;
import org.helium.rpc.client.RpcClientTransactionHandlerNLB;
import org.helium.rpc.client.RpcClientTransactionHandlerShort;
import org.helium.rpc.transparecy.api.annotation.Rpc;

import java.lang.reflect.Method;

/**
 * Created by Coral on 2015/5/14.
 */
public class RpcMethodClientStub {

	private String rpcMethodName;
	private String rpcServiceName;
	private Class<?> resultClass;
	private Class<?>[] parameterTypes;
	private RpcClientTransactionHandler handler;
	private String[] parameterInfo;
	private String codecName;


	private String getRpcServiceName(Method method, Rpc methodRpcAnno, Rpc serviceRpcAnno) {
		String result = null;
		if (methodRpcAnno != null) {
			result = methodRpcAnno.service();
		}
		if ((result == null || result.isEmpty()) && serviceRpcAnno != null) {
			result = serviceRpcAnno.service();
		}
		if (result == null || result.isEmpty()) {
			result = method.getDeclaringClass().getSimpleName();
		}
		return result;
	}

	private String getRpcMethodName(Method method, Rpc methodRpcAnno) {
		String result = null;
		if (methodRpcAnno != null) {
			result = methodRpcAnno.method();
		}
		if (result == null || result.isEmpty()) {
			result = method.getName();
		}
		return result;
	}

	public RpcMethodClientStub(Method method, RpcEndpoint endpoint, String codecName) {
		this.codecName = codecName;
		Rpc methodRpcAnno = method.getAnnotation(Rpc.class);
		Rpc serviceRpcAnno = method.getDeclaringClass().getAnnotation(Rpc.class);
		rpcServiceName = getRpcServiceName(method, methodRpcAnno, serviceRpcAnno);
		rpcMethodName = getRpcMethodName(method, methodRpcAnno);
		resultClass = method.getReturnType();
		parameterTypes = method.getParameterTypes();
		parameterInfo = new String[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterInfo[i] = parameterTypes[i].getName();
		}
		RpcClientChannel channel = endpoint.getClientChannel();
		RpcChannelSettings settings = channel.getSettings();
		if (settings.getSupportFlags().has(RpcChannelSupportFlag.CONNECTION)) {
			if (endpoint.getParameter("NLB") != null) {
				handler = new RpcClientTransactionHandlerNLB(endpoint, rpcServiceName, rpcMethodName);
			} else {
				handler = new RpcClientTransactionHandlerDirect(endpoint, rpcServiceName, rpcMethodName, null);
			}
		} else {
			handler = new RpcClientTransactionHandlerShort(endpoint, rpcServiceName, rpcMethodName);
		}
	}

	public RpcFuture invoke(Object[] args) {
		RpcClientTransaction tx = handler.createTransaction();
		tx.getRequest().setCodecName(codecName);
		tx.setArgs(parameterInfo);
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				tx.getRequest().putExtension(i, args[i], codecName);
			}
		}
		return tx.begin();
	}

	public Class<?> getResultClass() {
		return resultClass;
	}
}
