/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.server;

import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcServerMethodHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Rpc服务端基础类, 所有的rpc服务器端功能都从此派生
 * 固定方法必须在构造函数中进行添加,
 * <p>
 * Created by Coral
 */
public abstract class RpcServiceBase {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceBase.class);

	private String name;
	private boolean fixed;
	private Executor executor;
	private Map<String, RpcServerMethodHandler> handlers;

	/**
	 * 获取Rpc服务的名字
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 设置特定的线程池
	 *
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * 获取执行的线程池
	 *
	 * @return
	 */
	public Executor getExecutor() {
		return executor;
	}

	/**
	 * Rpc方法是否是固定的,非固定方法的话需要根据process方法进行自行分发
	 *
	 * @return
	 */
	public boolean isFixed() {
		return this.fixed;
	}

	/**
	 * 构造函数, 需要显式提供服务名
	 *
	 * @param name 服务名
	 */
	protected RpcServiceBase(String name) {
		this(name, true);
	}

	/**
	 * @param name
	 * @param fixed 是否为固定方法
	 */
	protected RpcServiceBase(String name, boolean fixed) {
		this.name = name;
		this.fixed = fixed;
		handlers = new HashMap<String, RpcServerMethodHandler>();

		if (fixed) {
			addAnnotatedMethods(this, this);
		}
	}

	protected void addMethodHandler(String name, RpcServerMethodHandler handler) {
		if (handlers.get(name) == null) {
			handlers.put(name, handler);
		} else {
			throw new IllegalArgumentException("key duplicated:" + name);
		}
	}

	/**
	 * 获取一个方法调用的MethodHandler
	 *
	 * @param method
	 * @return
	 */
	public RpcServerMethodHandler getMethodHanlder(String method) {
		RpcServerMethodHandler handler = handlers.get(method);
		if (handler != null && executor != null) {
			handler.setExecutor(executor);
		}
		return handler;
	}

	/**
	 * 基类提供的调用方法，禁用于处理当getMethodHandler()无法获取到合适的请求的时候
	 *
	 * @param ctx
	 */
	public void process(RpcServerContext ctx) {
		throw new UnsupportedOperationException("NotForCall");
	}

	protected static void addAnnotatedMethods(RpcServiceBase rpc, Object service) {
		for (Method method : service.getClass().getMethods()) {
			RpcMethod anno = method.getAnnotation(RpcMethod.class);
			if (anno == null) {
				continue;
			}

			//
			// 必须型如 void xxx(RpcServerContext ctx);
			Class<?>[] params = method.getParameterTypes();
			if (params.length != 1 && params[0] != RpcServerContext.class && method.getReturnType() != null) {
				throw new IllegalArgumentException("invaild parameter or return type.");
			}

			//
			// 有annotation用标注的名字，否则用方法名
			String methodName = anno.value().equals("") ? method.getName() : anno.value();
			RpcServerMethodHandler mh = createHandler(rpc, method, anno);
			rpc.addMethodHandler(methodName, mh);

			LOGGER.info("auto add method {} to {}", methodName, service.getClass().getName());
		}
	}

	protected static RpcServerMethodHandler createHandler(final Object owner, final Method method, RpcMethod anno) {
		RpcServerMethodHandler mh = new RpcServerMethodHandler("", "") {
			@Override
			public void run(RpcServerContext ctx) {
				try {
					method.invoke(owner, ctx);
				} catch (Exception e) {
					LOGGER.error("RpcMethod catch exception {}", e);
					ctx.end(RpcReturnCode.SERVER_ERROR, e);
				}
			}
		};
		return mh;
	}
}
