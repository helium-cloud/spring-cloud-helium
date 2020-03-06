package org.helium.rpc.transparecy.impl;

import org.helium.rpc.channel.*;
import org.helium.rpc.transparecy.api.MethodInvoker;
import org.helium.rpc.transparecy.api.RpcServerInvokeContext;
import org.helium.rpc.transparecy.api.RpcServiceAdapter;
import org.helium.rpc.transparecy.api.RpcServiceCenter;
import org.helium.threading.ExecutorBusyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * Created by Coral on 2015/5/12.
 */
public class RpcServiceCenterImpl implements RpcServiceCenter {

	final static Logger logger = LoggerFactory.getLogger(RpcServiceCenterImpl.class);

	RpcServerChannel channel;
	Executor executor;
	Map<String, RpcServiceAdapter> services;
	String clientCodecName;

	/**
	 * 实例化一个不提供对外服务，只能作为RPC客户端使用的 RpcServiceCenter
	 */
	public RpcServiceCenterImpl() {
		this(null, null);
	}

	/**
	 * 实话一个可以对外提供 RPC 服务的 RpcServiceCenter
	 *
	 * @param channel  RPC服务通道
	 * @param executor 处理RPC请求的线程池
	 */

	public RpcServiceCenterImpl(RpcServerChannel channel, Executor executor) {
		this.services = Collections.synchronizedMap(new HashMap<>());
		this.executor = executor;
		this.channel = channel;
		if (channel != null) {
			channel.getTransactionCreated().addListener((sender, rpcServerTransaction) -> {
				invoke(rpcServerTransaction);
			});
		}
	}

	public void setClientCodecName(String clientCodecName) {
		this.clientCodecName = clientCodecName;
	}

	private void invoke(RpcServerTransaction tx) {
		final RpcServerInvokeContext ctx = new RpcServerInvokerContextImpl(tx);
		try {
			getInvokeHandler(tx, ctx).execute(ctx);
		} catch (ExecutorBusyException ex) {
			ctx.end(RpcReturnCode.SERVER_BUSY, null);
			logger.error("executor.execute busy", ex);
		} catch (RpcRuntimeException ex) {
			ctx.end(ex.getReturnCode(), ex.getCause());
			logger.error("executor.execute failed", ex);
		} catch (Exception ex) {
			ctx.end(RpcReturnCode.SERVER_ERROR, ex);
			logger.error("executor.execute failed", ex);
		}

	}

	private MethodInvoker getInvokeHandler(RpcServerTransaction tx, RpcServerInvokeContext ctx) {
		MethodInvoker handler = (MethodInvoker) tx.getMethodCache().getHandler();
		if (handler == null) {
			// 路由到 Service
			String serviceName = ctx.getToService();
			RpcServiceAdapter service = services.get(serviceName);
			if (service == null) {
				throw new RpcRuntimeException(RpcReturnCode.SERVICE_NOT_FOUND, String.format("Can't found request service '%s'", serviceName));
			}

			// 获取 MethodHandler
			String methodName = ctx.getToMethod();
			handler = service.getMethodInvoker((RpcServerInvokeContext) ctx);
			if (handler == null) {
				throw new RpcRuntimeException(RpcReturnCode.METHOD_NOT_FOUND, String.format("Can't found request method '%s#%s'", serviceName, methodName));
			}

			// 设置进程池并将 MethodHandler 保存到 tx 中
			if (handler.getExecutor() == null) {
				handler.setExecutor(this.executor);
			}
			tx.getMethodCache().setHandler((RpcServerMethodHandler) handler);
		}
		return handler;
	}

	public void start() {
		try {
			channel.start();
			logger.info("RPC channel started. ({}) ", channel.getServerEndpoint());
		} catch (Exception e) {
			logger.error("start channel failed : {} ", e);
		}
	}

	public void stop() {
		channel.stop();
	}

	@Override
	public void registerService(Object serviceBean, Class<?>[] interfaces) {
		if (interfaces == null) {
			interfaces = serviceBean.getClass().getInterfaces();
		}
		for (Class<?> clazz : interfaces) {
			if (!clazz.isInterface()) {
				logger.error("{} is not a interface", clazz);
				break;
			}
			// TODO 这里有点过于简单了？如果有多个版本同时运行都要注册怎么破？
			final String serviceName = clazz.getSimpleName();
			if (services.containsKey(serviceName)) {
				throw new IllegalArgumentException(String.format("Service '%s' is already registered.", serviceBean));
			}
			logger.debug("Register RPC service '{}' with '{}'", serviceName, serviceBean);
			final Map<String, MethodInvoker> invokerMap = new HashMap<>();
			for (Method method : clazz.getMethods()) {
				String methodName = method.getName();
				if (invokerMap.containsKey(methodName)) {
					throw new IllegalArgumentException(String.format("Not support method overloading. (method=%s)", methodName));
				}
				logger.debug("RPC service method '{}#{}' invoke adapter initialized", serviceName, methodName);
				invokerMap.put(methodName, new MethodInvokerImpl(serviceName, method, serviceBean));
			}
			services.put(serviceName, new RpcServiceAdapter() {
				@Override
				public String getRpcServiceName() {
					return serviceName;
				}

				@Override
				public MethodInvoker getMethodInvoker(RpcServerInvokeContext ctx) {
					MethodInvoker invoker = invokerMap.get(ctx.getToMethod());
					if (invoker != null && invoker.getExecutor() == null) {
						invoker.setExecutor(executor);
					}
					return invoker;
				}

				@Override
				public Object getServiceBean() {
					return serviceBean;
				}
			});
			logger.debug("RPC service '{}' with '{}' registered", serviceName, serviceBean);
		}
	}

	@Override
	public void unregisterService(Object serviceBean) {
		logger.debug("Unregister service by service bean '{}'", serviceBean);
		for (Map.Entry<String, RpcServiceAdapter> entry : services.entrySet()) {
			if (entry.getValue().getServiceBean().equals(serviceBean)) {
				services.remove(entry.getKey());
				logger.debug("RPC service '{}' with service bean '{}' unregistered", entry.getKey(), entry.getValue().getServiceBean());
			}
		}
	}

	@Override
	public void unregisterService(Class<?> serviceBeanClass) {
		logger.debug("Unregister service by service bean class '{}'", serviceBeanClass);
		for (Map.Entry<String, RpcServiceAdapter> entry : services.entrySet()) {
			if (serviceBeanClass.isInstance(entry.getValue().getServiceBean())) {
				services.remove(entry.getKey());
				logger.debug("RPC service '{}' with service bean '{}' unregistered", entry.getKey(), entry.getValue().getServiceBean());
			}
		}
	}

	@Override
	public <T> T getServiceMapper(Class<T> type, RpcEndpoint endpoint) {
		ClassLoader cl = type.getClassLoader();
		InvocationHandler handler = new RpcServiceMapperHandler(endpoint, clientCodecName);
		//noinspection unchecked
		return (T) Proxy.newProxyInstance(cl, new Class<?>[]{type}, handler);
	}

}
