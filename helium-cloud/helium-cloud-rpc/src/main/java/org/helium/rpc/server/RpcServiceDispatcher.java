/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-26
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.server;

import org.helium.rpc.channel.RpcResponse;
import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcServerMethodHandler;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.threading.ExecutorBusyException;
import org.helium.threading.ExecutorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;

/**
 * Rpc服务器端功能分发
 * <p>
 * Created by Coral@feinno.com
 */
public class RpcServiceDispatcher {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceDispatcher.class);
	private static final Executor DEFAULT_EXECUTOR = ExecutorFactory.newFixedExecutor("rpc", 100, 100 * 100);

	private Map<String, RpcServiceBase> services;
	private Executor executor;

	/**
	 * 设置线程池
	 *
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * 获取线程池
	 *
	 * @return
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 * constructor
	 */
	public RpcServiceDispatcher() {
		services = new HashMap<String, RpcServiceBase>();
		executor = DEFAULT_EXECUTOR;
	}

	/**
	 * 添加一个服务, 允许两种对象<br>
	 * 1. 从RpcServiceBase派生<br>
	 * 2. 实现由@RpcService标记的interface<br>
	 * TODO: RpcService的结构不太好，需要重构
	 *
	 * @param service
	 */
	public void addService(Object service) {
		synchronized (this) {
			if (service instanceof RpcServiceBase) {
				RpcServiceBase s = (RpcServiceBase) service;
				services.put(s.getName(), s);
			} else {
				RpcTransparentServiceDecorator decorator = RpcTransparentServiceDecorator.create(service);
				services.put(decorator.getName(), decorator);
			}
		}
	}

	/**
	 * 移除一个服务，如果移除成功
	 *
	 * @param serviceName
	 * @return
	 */
	public boolean removeService(String serviceName) {
		synchronized (this) {
			return (services.remove(serviceName) != null);
		}
	}

	/**
	 * 获取已添加的服务
	 *
	 * @param service
	 * @return
	 */
	public RpcServiceBase getService(String service) {
		synchronized (this) {
			return services.get(service);
		}
	}

	/**
	 * 获取运行时的服务名称
	 *
	 * @return
	 */
	public String[] getRunningService() {
		synchronized (this) {
			Set<String> names = services.keySet();
			return names.toArray(new String[names.size()]);
		}
	}

	/**
	 * Rpc事件处理总入口, 此方法在I/O线程中执行, 并转发给应用线程池
	 *
	 * @param tx
	 */
	public void processTransaction(RpcServerTransaction tx) {
		RpcServerMethodHandler handler = tx.getMethodCache().getHandler();
		final RpcServerContext ctx = new RpcServerContext(tx);

		if (handler == null) {
			String serviceName = ctx.getToService();
			RpcServiceBase service = getService(serviceName);
			if (service == null) {
				ctx.end(RpcReturnCode.SERVICE_NOT_FOUND, null);
				return;
			}

			handler = (RpcServerMethodHandler) service.getMethodHanlder(ctx.getToMethod());
			if (handler != null) {
				if (handler.getExecutor() == null) {
					handler.setExecutor(this.getExecutor());
				}
				tx.getMethodCache().setHandler(handler);
			} else {
				if (service.isFixed()) {
					ctx.endWithResponse(RpcResponse.createError(RpcReturnCode.METHOD_NOT_FOUND, null, null));
					return;
				} else {
					final RpcServiceBase s2 = service;
					handler = new RpcServerMethodHandler(ctx.getToService(), ctx.getToMethod()) {
						@Override
						public void run(RpcServerContext ctx) {
							s2.process(ctx);
						}
					};
					handler.setExecutor(this.getExecutor());
				}
			}
		}

		final RpcServerMethodHandler h2 = handler;
		try {
			h2.execute(ctx);
		} catch (ExecutorBusyException ex) {
			ctx.end(RpcReturnCode.SERVER_BUSY, null);
			LOGGER.error("executor.execute busy {}", ex);
		} catch (Exception ex) {
			ctx.end(RpcReturnCode.SERVER_ERROR, ex);
			LOGGER.error("executor.execute failed {}", ex);
		}
	}
}
