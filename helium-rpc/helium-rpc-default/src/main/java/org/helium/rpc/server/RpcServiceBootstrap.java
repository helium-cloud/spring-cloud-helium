/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.server;


import org.helium.rpc.api.proxy.ServiceStarter;
import org.helium.rpc.channel.RpcEndpoint;
import org.helium.rpc.channel.RpcException;
import org.helium.rpc.channel.RpcServerChannel;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.rpc.server.builtin.RpcBuiltinService;
import org.helium.util.EventHandler;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Rpc服务注册及引导启动类
 * <pre>
 * <code>
 *
 * </code>
 * </pre>
 * <p>
 * Created by Coral
 */
public class RpcServiceBootstrap implements ServiceStarter {
	public static RpcServiceBootstrap INSTANCE = new RpcServiceBootstrap();
	private Object syncRoot = new Object();
	private List<RpcServerChannel> channels = new ArrayList<RpcServerChannel>();
	private RpcServiceDispatcher dispatcher = new RpcServiceDispatcher();
	private Logger LOGGER = LoggerFactory.getLogger(RpcServiceBootstrap.class);

	public RpcServiceBootstrap() {
		//默认提供获取此服务的内部信息
		dispatcher.addService(new RpcBuiltinService());
	}

	/**
	 * 注册服务器端Channel
	 *
	 * @param channel
	 */
	public void registerChannel(RpcServerChannel channel) {
		synchronized (syncRoot) {
			channels.add(channel);
		}
		channel.getTransactionCreated().addListener(new EventHandler<RpcServerTransaction>() {
			@Override
			public void run(Object sender, RpcServerTransaction args) {
				dispatcher.processTransaction(args);
			}
		});
	}

	@Override
	public ServiceStarter getInstance() {
		return INSTANCE;
	}

	/**
	 * 设置线程池
	 *
	 * @param executor
	 */
	public void setExecutor(Executor executor) {
		dispatcher.setExecutor(executor);
	}

	/**
	 * 注册Rpc服务
	 *
	 * @param service
	 */
	public void registerService(Object service) {
		dispatcher.addService(service);
	}

	/**
	 * 注册透明Rpc服务
	 *
	 * @param serviceName
	 * @param serviceObject
	 * @param serviceInterfaces
	 */
	public void registerTransparentService(String serviceName, Object serviceObject, Executor executor, Class<?>... serviceInterfaces) {
		dispatcher.addService(new RpcTransparentService(serviceName, serviceObject, executor, serviceInterfaces));
	}

	/**
	 * 获取一个已经注册了的Rpc服务
	 *
	 * @param service
	 * @return
	 */
	public RpcServiceBase getService(String service) {
		return dispatcher.getService(service);
	}

	public boolean unregisterService(String serviceName) {
		return dispatcher.removeService(serviceName);

	}

	/**
	 * 获取运行时的全部服务名称
	 *
	 * @return
	 */
	public String[] getRunningService() {
		return dispatcher.getRunningService();
	}

	/**
	 * 启动所有Channel
	 *
	 * @throws RpcException
	 */
	public void start() throws Exception {
		for (RpcServerChannel channel : channels) {
			try {
				channel.start();
				LOGGER.info("start channel ok {}", channel.getServerEndpoint());
			} catch (Exception ex) {
				LOGGER.error("start channel failed {} ", ex);
			}
		}
	}

	/**
	 * 停止所有Channel
	 *
	 * @throws RpcException
	 */
	public void stop() throws Exception {
		for (RpcServerChannel channel : channels) {
			channel.stop();
		}
	}

	/**
	 * 获取一个已经注册了的RpcServerChannel
	 *
	 * @param protocol
	 * @param urlMark
	 * @return
	 */
	public RpcServerChannel getServerChannel(String protocol, String urlMark) {
		synchronized (syncRoot) {
			for (RpcServerChannel channel : channels) {
				RpcEndpoint ep = channel.getServerEndpoint();
				if (ep.getProtocol().equals(protocol)) {
					if (!StringUtils.isNullOrEmpty(urlMark) && ep.toString().contains(urlMark)) {
						return channel;
					}
				}
			}
		}
		return null;
	}
}
