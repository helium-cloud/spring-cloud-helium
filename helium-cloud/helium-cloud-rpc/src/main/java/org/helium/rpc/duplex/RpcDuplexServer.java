/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-12-29
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.duplex;

import org.helium.rpc.channel.RpcConnection;
import org.helium.rpc.channel.RpcServerChannel;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.rpc.server.RpcServiceDispatcher;
import org.helium.util.Event;
import org.helium.util.EventHandler;

import java.util.concurrent.Executor;

/**
 * rpc双工服务器端
 * <p>
 * Created by Coral
 */
public class RpcDuplexServer {
	private RpcServerChannel channel;
	private RpcServiceDispatcher dispatcher;

	public RpcDuplexServer(RpcServerChannel channel) {
		this.dispatcher = new RpcServiceDispatcher();
		this.channel = channel;

		channel.getTransactionCreated().addListener(new EventHandler<RpcServerTransaction>() {
			@Override
			public void run(Object sender, RpcServerTransaction args) {
				dispatcher.processTransaction(args);
			}
		});

		channel.getConnectionCreated().addListener(new EventHandler<RpcConnection>() {
			@Override
			public void run(Object sender, RpcConnection e) {
				// e.getTransactionCreated().addListener(new EventHandler<RpcServerTransaction>);
			}
		});
	}

	public Event<RpcConnection> getConnectionCreated() {
		return channel.getConnectionCreated();
	}

	public Event<RpcConnection> getConnectionDestroyed() {
		return channel.getConnectionCreated();
	}

	public RpcServerChannel getChannel() {
		return this.channel;
	}

	public void registerService(Object service) {
		dispatcher.addService(service);
	}

	public void setExecutor(Executor executor) {
		dispatcher.setExecutor(executor);
	}
}
