package org.helium.rpc.transparecy.impl;

import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcRuntimeException;
import org.helium.rpc.channel.RpcServerMethodHandler;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.transparecy.api.MethodInvoker;
import org.helium.rpc.transparecy.api.RpcServerInvokeContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Coral on 2015/5/12.
 */
public class MethodInvokerImpl extends RpcServerMethodHandler implements MethodInvoker {

	final static Logger logger = LoggerFactory.getLogger(MethodInvokerImpl.class);

	private Method method;
	private Object serviceBean;
	private Class<?>[] parameterTypes;
	private Class<?> resultType;

	public MethodInvokerImpl(String serviceName, Method method, Object serviceBean) {
		super(serviceName, method.getName());
		this.method = method;
		this.resultType = method.getReturnType();
		this.serviceBean = serviceBean;
		this.parameterTypes = method.getParameterTypes();
	}

	@Override
	public void run(RpcServerContext octx) {
		RpcServerInvokeContext ctx = (RpcServerInvokeContext) octx;
		Object result;
		try {
			Object[] args = ctx.getInvokeArgs();
			result = method.invoke(serviceBean, args);
//            setResultsCodec(Serializer.getCodec(resultType, ctx.getTx().getRequest().getHeader().getCodecName()));
			ctx.end(result);
		} catch (InvocationTargetException e) {
			ctx.end(RpcReturnCode.SERVER_ERROR, e.getTargetException());
		} catch (RpcRuntimeException e) {
			ctx.end(e.getReturnCode(), e.getCause());
		} catch (Exception e) {
			ctx.end(RpcReturnCode.SERVER_ERROR, e);
		}
	}

	@Override
	public void execute(RpcServerInvokeContext ctx) {
		super.execute((RpcServerContext) ctx);
	}

	@Override
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}
}
