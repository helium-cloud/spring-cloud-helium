package org.helium.rpc.transparecy.impl;

import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcRuntimeException;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.transparecy.api.MethodInvoker;
import org.helium.rpc.transparecy.api.RpcServerInvokeContext;

/**
 * Created by Coral on 2015/5/13.
 */
public class RpcServerInvokerContextImpl extends RpcServerContext implements RpcServerInvokeContext {

	public RpcServerInvokerContextImpl(RpcServerTransaction tx) {
		super(tx);
	}

	@Override
	public Object[] getInvokeArgs() {
		MethodInvoker handler = (MethodInvoker) getTx().getMethodCache().getHandler();
		Class<?>[] parameterTypes = handler.getParameterTypes();
		if (parameterTypes == null || parameterTypes.length == 0) {
			return null;
		}
		Object[] result = new Object[parameterTypes.length];
		for (int i = 0; i < parameterTypes.length; i++) {
			try {
				result[i] = getExtension(i, parameterTypes[i]);
			} catch (Exception e) {
				throw new RpcRuntimeException(RpcReturnCode.SERVER_ERROR, "Decode invoke parameters error", e);
			}
		}
		return result;
	}

	@Override
	public void end(Object result) {
		super.end(result);
	}

}
