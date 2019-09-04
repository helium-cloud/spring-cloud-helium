/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-12-29
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.duplex;

import org.helium.rpc.channel.RpcConnectionReal;
import org.helium.rpc.channel.RpcConnectionStatus;
import org.helium.rpc.channel.RpcEndpoint;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.rpc.client.RpcInvocationHandler;
import org.helium.rpc.client.RpcMethodStub;
import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcService;
import org.helium.rpc.server.RpcServiceDispatcher;
import org.helium.threading.Future;
import org.helium.util.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Rpc的双工连接客户端, 手动处理断线重连的逻辑
 * <p>
 * Created by Coral
 */
public class RpcDuplexClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcDuplexClient.class);

	public static final int CONNECT_TIMEOUT = 60000;

	private RpcEndpoint serverEp;
	private RpcConnectionReal connection;
	private RpcServiceDispatcher dispatcher;

	/**
	 * 创建一个双工连接客户端
	 *
	 * @param serverEp 服务器端地址
	 */
	public RpcDuplexClient(RpcEndpoint serverEp) {
		this.serverEp = serverEp;
		dispatcher = new RpcServiceDispatcher();
	}

	/**
	 * 获取服务器端地址
	 *
	 * @return
	 */
	public RpcEndpoint getServerEndpoint() {
		return serverEp;
	}

	/**
	 * 获取连接
	 *
	 * @return
	 */
	public RpcConnectionReal getConnection() {
		return connection;
	}

	/**
	 * 是否连接上了
	 *
	 * @return
	 */
	public boolean isConnected() {
		return connection != null ? connection.getStatus() == RpcConnectionStatus.CONNECTED : false;
	}

	/**
	 * 给服务器提供能回调的接口服务
	 *
	 * @param service
	 */
	public void registerCallbackService(Object service) {
		dispatcher.addService(service);
	}

	/**
	 * 获取一个调用服务器的代理
	 *
	 * @param service
	 * @return
	 */
	public RpcMethodStub getMethodStub(String service, String method) {
		RpcClientTransactionHandlerDuplex handler = new RpcClientTransactionHandlerDuplex(this, service, method);
		return new RpcMethodStub(handler);
	}

	/**
	 * 获取一个调用服务器的透明代理
	 *
	 * @param <I>
	 * @param intf
	 * @return
	 */
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
			if (!method.getReturnType().equals(Void.class) && method.getReturnType() != void.class) {
				stub.setResultsClass(method.getReturnType());
			}
			stubs.put(method.getName(), stub);
		}
		//ClassLoader cl = Thread.currentThread().getContextClassLoader();

		ClassLoader cl = intf.getClassLoader();
		InvocationHandler handler = new RpcInvocationHandler(stubs);
		return (I) Proxy.newProxyInstance(cl, new Class<?>[]{intf}, handler);
	}

	/**
	 * 尝试连接
	 *
	 * @return
	 */
	public Future<Throwable> connect() {
		if (connection != null) {
			if (!connection.isClosed()) {
				throw new IllegalStateException("Connection is not state");
			}
		}

		connection = (RpcConnectionReal) serverEp.getClientChannel().createConnection(serverEp);
		connection.getTransactionCreated().addListener(new EventHandler<RpcServerTransaction>() {
			@Override
			public void run(Object sender, RpcServerTransaction args) {
				dispatcher.processTransaction(args);
			}
		});
		LOGGER.info(">>> connection:" + connection);
		return connection.connect();
	}

	public void connectSync() throws Exception {
		Future<Throwable> future = connect();
		future.await();
		if (future.getValue() != null) {
			throw new Exception("Connect Failed!", future.getValue());
		}
	}

	public void close() {
		if (connection != null) {
			connection.close(null);
		}
	}

	public void setExecutor(Executor executor) {
		dispatcher.setExecutor(executor);
	}
}
