package org.helium.rpc.server;

import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcServerMethodHandler;
import org.helium.threading.Future;
import org.helium.threading.FutureListener;
import org.helium.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * RPC 服务中心
 * <p>
 * 该接口提供服务注册和获取远程RPC服务调用映射的能力,作为RPC的核心操作入口提供给用户
 * <p>
 * Created by Coral on 2015/5/12.
 */
public class RpcTransparentService extends RpcServiceBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTransparentService.class);

	private Executor executor;
	private Object serviceObject;
	private Logger logger;

	/**
	 * 注册RPC服务
	 *
	 * @param serviceName       注册的服务名
	 * @param serviceObject     提供服务处理能力的javabean实例
	 * @param serviceInterfaces 一组供发布的方法接口，ServiceBean必须实现这些接口，如果传 null 则发布serviceBean实现了的所有接口
	 */
	public RpcTransparentService(String serviceName, Object serviceObject, Executor executor, Class<?>[] serviceInterfaces) {
		super(serviceName, true);
		this.executor = executor;
		this.serviceObject = serviceObject;
		analyzeMethods(serviceName, serviceInterfaces);
		logger = LoggerFactory.getLogger(serviceObject.getClass().getName());
	}

	private void analyzeMethods(String serviceName, Class<?>[] serviceInterfaces) {
		if (serviceInterfaces == null) {
			throw new IllegalArgumentException("serviceInterfaces == null");
		}

		Map<String, Method> methods = new HashMap<>();
		for (Class<?> clazz : serviceInterfaces) {
			for (Method method : clazz.getMethods()) {
				if (methods.get(method.getName()) == null) {
					methods.put(method.getName(), method);
				} else {
					LOGGER.error("duplicated method in class={} method={}", serviceObject.getClass(), method.getName());
				}
			}
		}

		methods.forEach((k, v) -> {
			RpcServerMethodHandler h = createMethodHandler(serviceName, k, v);
			h.setExecutor(executor);
			addMethodHandler(k, h);
		});
	}

	private RpcServerMethodHandler createMethodHandler(String serviceName, String methodName, Method method) {
		Class<?> returnType = method.getReturnType();

		if (Future.class.isAssignableFrom(returnType)) {
			return new AsyncMethodHandler(serviceName, methodName, serviceObject, method);
		} else {
			return new MethodHandler(serviceName, methodName, serviceObject, method);
		}
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}

	private static class MethodHandler extends RpcServerMethodHandler {
		public MethodHandler(String serviceName, String methodName, Object serviceObject, Method method) {
			super(serviceName, methodName);
			this.serviceObject = serviceObject;
			this.method = method;
			argsType = method.getParameterTypes();
			logger = LoggerFactory.getLogger(serviceObject.getClass().getName());
		}

		Object serviceObject;
		Method method;
		Class<?>[] argsType;
		Logger logger;

		@Override
		public void run(RpcServerContext ctx) {
			Object[] args = null;
			try {
				args = getInvokeArgs(ctx);
				Object r = method.invoke(serviceObject, args);
				ctx.end(r);
			} catch (Exception ex) {
				logger.error("{}({}) invoke failed {}", method, formatArgs(args), ex);
				ctx.end(RpcReturnCode.SERVER_ERROR, ex);
			}
		}

		private String formatArgs(Object[] args) {
			if (args == null || args.length == 0) {
				return "";
			}
			StringBuilder str = new StringBuilder();
			for (int i = 0; i < args.length; i++) {
				if (i != 0) {
					str.append(",");
				}
				if (args[i] == null) {
					str.append("null");
				} else {
					str.append(args[i].toString());
				}
			}
			return str.toString();
		}

		Object[] getInvokeArgs(RpcServerContext ctx) {
			Object[] args = new Object[argsType.length];
			for (int i = 0; i < argsType.length; i++) {
				args[i] = ctx.getExtension(i, argsType[i]);
			}
			return args;
		}
	}

	private static class AsyncMethodHandler extends MethodHandler {
		public AsyncMethodHandler(String serviceName, String methodName, Object serviceObject, Method method) {
			super(serviceName, methodName, serviceObject, method);
		}

		@Override
		public void run(RpcServerContext ctx) {
			try {
				Object[] args = getInvokeArgs(ctx);
				Future future = (Future) method.invoke(serviceObject, args);
				future.addListener(new FutureListener() {
					@Override
					public void run(Result result) {
						if (result.getError() != null) {
							ctx.end(result.getError());
						} else {
							ctx.end(result.getValue());
						}
					}
				});
			} catch (Exception ex) {
				ctx.end(ex);
			}
		}
	}
}
