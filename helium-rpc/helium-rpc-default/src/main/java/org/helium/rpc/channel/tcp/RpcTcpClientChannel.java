/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 4, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import com.feinno.superpojo.type.Flags;
import org.helium.rpc.channel.*;
import org.helium.threading.ThreadFactorys;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * 基于TCP协议实现的客户端协议栈
 * <p>
 * Created by Coral
 */
public class RpcTcpClientChannel extends RpcClientChannel {

	public static final int MAX_BODY_SIZE = 64 * 1024 * 1024;
	public static final Flags<RpcChannelSupportFlag> FLAGS = Flags.of(RpcChannelSupportFlag.CONNECTION, RpcChannelSupportFlag.DUPLEX_CONNECTION);
	public static final RpcChannelSettings SETTINGS = new RpcChannelSettings("tcp", FLAGS, MAX_BODY_SIZE);
	public static final RpcClientChannel INSTANCE = new RpcTcpClientChannel();

	private Thread thread;
	private ClientBootstrap bootstrap;
	private ConcurrentHashMap<RpcEndpoint, RpcTcpConnection> recycling;
	private Map<RpcEndpoint, RpcTcpConnection> connections;
	private Map<RpcEndpoint, RpcConnectionGroup> connectionGroups;

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpClientChannel.class);

	private RpcTcpClientChannel() {
		super(SETTINGS);

		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
				Executors.newCachedThreadPool(ThreadFactorys.forIO("boss-c")),
				Executors.newCachedThreadPool(ThreadFactorys.forIO("worker-c"))
		)
		);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline p = pipeline();
				p.addLast("protobufDecoder", new RpcTcpMessageDecoder(MAX_BODY_SIZE));
				p.addLast("protobufEncoder", new RpcTcpMessageEncoder());
				p.addLast("handler", new RpcTcpClientHandler());
				return p;
			}
		});
		recycling = new ConcurrentHashMap<RpcEndpoint, RpcTcpConnection>();
		connections = new ConcurrentHashMap<RpcEndpoint, RpcTcpConnection>();
		connectionGroups = new ConcurrentHashMap<RpcEndpoint, RpcConnectionGroup>();

		//线程先不要启动，因为检查线程有可能有隐患
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				checkConnections();
			}
		});
		thread.setName("rpc-tcp-client-checkConnections");
		thread.setDaemon(true);
		thread.start();
	}

	@Override
	public RpcConnection createConnection(RpcEndpoint ep) {
		return new RpcTcpConnection((RpcTcpEndpoint) ep, bootstrap);
	}

	@Override
	public RpcConnection getConnection(RpcEndpoint ep) {
		synchronized (connections) {
			RpcTcpConnection conn = connections.get(ep);
			if (conn == null || !conn.isUseable()) {
				conn = (RpcTcpConnection) createConnection(ep);
				conn.autoConnect();
				connections.put(ep, conn);
			}
			return conn;
		}
	}

	@Override
	public RpcConnectionGroup getConnectionGroup(RpcEndpoint ep, int count) {
		synchronized (connectionGroups) {
			RpcConnectionGroup group = connectionGroups.get(ep);
			if (group == null) {
				group = new RpcConnectionGroup(ep, count);
				connectionGroups.put(ep, group);
			}
			return group;
		}
	}

	/**
	 * 检查链接是否超时的方法 <br>
	 * 1. timeout 建立链接后超过180s进入此状态 <br>
	 * 2. recycling 超时后再过120s进入此状态 <br>
	 * 3. check IDLE and close 此时回收链接 <br>
	 */
	private void checkConnections() {
		while (true) {
			try {
				Thread.sleep(1000);
				// 本次需要立即关闭的链接
				ConcurrentHashMap<RpcEndpoint, RpcTcpConnection> canClose = new ConcurrentHashMap<RpcEndpoint, RpcTcpConnection>();
				// 仅移除，放入带回收列表中的链接
				ConcurrentHashMap<RpcEndpoint, RpcTcpConnection> canRemove = new ConcurrentHashMap<RpcEndpoint, RpcTcpConnection>();
				synchronized (connections) {
					for (RpcEndpoint rpcEndpoint : connections.keySet()) {
						RpcTcpConnection conn = connections.get(rpcEndpoint);
						if (conn.isClosed() || conn.isIdle()) {
							// 如果链接上180s没有任何消息，那么被放置到立即关闭列表中(此事件几乎不可能发生...)
							// 如果链接被关闭，那么也放置到立即列表
							canClose.put(rpcEndpoint, conn);
							continue;
						} else if (conn.isTimeOver()) {
							// 将超过180秒的链接放入待回收列表中，防止再有请求使用此链接(意为每条链接的寿命只有180s)
							canRemove.put(rpcEndpoint, conn);
							continue;
						}
					}
					// 移除需要关闭的链接
					for (RpcEndpoint rpcEndpoint : canClose.keySet()) {
						connections.remove(rpcEndpoint);
					}
					// 移除放入待回收列表中得链接
					for (RpcEndpoint rpcEndpoint : canRemove.keySet()) {
						RpcTcpConnection conn = connections.remove(rpcEndpoint);
						recycling.put(rpcEndpoint, conn);
					}
				}
				// 关闭掉本次需要立即移除的链接
				for (RpcTcpConnection conn : canClose.values()) {
					try {
						LOGGER.info("Disconnect {}", conn);
						conn.close(null);
					} catch (Exception ex) {
						LOGGER.error("releaseIdleConnection failed {}", ex);
					}
				}

				// 将待回收列表中达到回收时间得链接放入关闭列表
				ConcurrentHashMap<RpcEndpoint, RpcTcpConnection> recycled = new ConcurrentHashMap<RpcEndpoint, RpcTcpConnection>();
				for (RpcEndpoint rpcEndpoint : recycling.keySet()) {
					RpcTcpConnection conn = recycling.get(rpcEndpoint);
					if (conn.isClosed() || conn.isIdle() || conn.isRecycled()) {
						recycled.put(rpcEndpoint, conn);
					}
				}
				// 将回收列表中的链接关闭
				for (RpcEndpoint rpcEndpoint : recycled.keySet()) {
					try {
						RpcTcpConnection conn = recycling.remove(rpcEndpoint);
						LOGGER.info("Disconnect {}", conn);
						conn.close(null);
					} catch (Exception ex) {
						LOGGER.error("releaseIdleConnection failed {}", ex);
					}
				}
			} catch (Exception ex) {
				LOGGER.error("checkConnections failed {}", ex);
			}
		}
	}
}
