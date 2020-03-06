/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 7, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import org.helium.rpc.channel.RpcConnection;
import org.helium.rpc.channel.RpcServerChannel;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.threading.ThreadFactorys;
import org.helium.util.EventHandler;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.ChannelGroupFuture;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * RpcOverTcp的服务器端信道, 可用于Simplex及Duplex领域
 * <p>
 * Created by Coral
 */
public class RpcTcpServerChannel extends RpcServerChannel {

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpServerChannel.class);

	private ChannelGroup group = new DefaultChannelGroup();
	private ServerBootstrap bootstrap;
	private Set<RpcTcpConnection> connections;
	private Thread thread;

	/**
	 * 监听Port端口
	 *
	 * @param port
	 */
	public RpcTcpServerChannel(int port) {
		this(new RpcTcpEndpoint("0.0.0.0", port));
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				checkConnections();
			}
		});
		connections = new HashSet<RpcTcpConnection>();

		thread.setName("rpc-tcp-checkConnections");
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * 监听固定的endpoint, 当只绑定一个地址的时候使用
	 *
	 * @param serverEp
	 */
	public RpcTcpServerChannel(RpcTcpEndpoint serverEp) {
		super(RpcTcpClientChannel.SETTINGS, serverEp);

		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(ThreadFactorys.forIO("boss-s")),
				Executors.newCachedThreadPool(ThreadFactorys.forIO("worker-s"))
		)
		);

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				ChannelPipeline p = pipeline();
				p.addLast("protobufDecoder", new RpcTcpMessageDecoder(getSettings().getMaxBodySize()));
				p.addLast("protobufEncoder", new RpcTcpMessageEncoder());
				p.addLast("handler", new RpcTcpServerHandler(RpcTcpServerChannel.this));
				return p;
			}
		});

		bootstrap.setOption("child.tcpNoDelay", true);
		bootstrap.setOption("child.keepAlive", false);

		// TODO: ugly
		this.getConnectionCreated().addListener(new EventHandler<RpcConnection>() {
			@Override
			public void run(Object sender, RpcConnection args) {
				synchronized (connections) {
					connections.add((RpcTcpConnection) args);
				}
				args.getTransactionCreated().addListener(new EventHandler<RpcServerTransaction>() {
					@Override
					public void run(Object sender, RpcServerTransaction args) {
						RpcTcpServerChannel.this.getTransactionCreated().fireEvent(args);
					}
				});
			}
		});

		this.getConnectionDestoryed().addListener(new EventHandler<RpcConnection>() {
			@Override
			public void run(Object sender, RpcConnection e) {
				e.getDisconnected().fireEvent(null);
			}
		});
	}

	@Override
	public void doStart() throws Exception {
		Channel channel = bootstrap.bind(((RpcTcpEndpoint) this.getServerEndpoint()).getSocketAddress());
		group.add(channel);
	}

	@Override
	public void doStop() throws Exception {
		ChannelGroupFuture future = group.close();
		future.await(5000);
		bootstrap.shutdown();
	}

	private void checkConnections() {
		while (true) {
			try {
				Thread.sleep(1000);
				List<RpcTcpConnection> list = new ArrayList<RpcTcpConnection>();
				synchronized (connections) {
					for (RpcTcpConnection conn : connections) {
						if (conn.isIdle()) {
							list.add(conn);
						}
						if (conn.isClosed()) {
							list.add(conn);
						}
					}
					for (RpcConnection conn : list) {
						connections.remove(conn);
					}
				}

				for (RpcConnection conn : list) {
					try {
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