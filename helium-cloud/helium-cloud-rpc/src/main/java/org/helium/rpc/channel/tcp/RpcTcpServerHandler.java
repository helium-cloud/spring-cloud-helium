/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.rpc.channel.RpcMessage;
import org.helium.rpc.channel.RpcRequest;
import org.helium.rpc.channel.RpcResponse;
import org.helium.rpc.channel.RpcServerChannel;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于TCP传输的RPC服务器端处理器
 *
 * @author Coral
 */
class RpcTcpServerHandler extends SimpleChannelHandler {

	static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpServerHandler.class);
	static final RpcTcpPerformanceCounter COUNTER = PerformanceCounterFactory.getCounters(RpcTcpPerformanceCounter.class, "server");

	private final RpcServerChannel serverChannel;

	public RpcTcpServerHandler(RpcServerChannel serverChannel) {
		this.serverChannel = serverChannel;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		LOGGER.info("message received", e.getMessage());
		RpcMessage message = (RpcMessage) e.getMessage();
		RpcTcpConnection connection = (RpcTcpConnection) e.getChannel().getAttachment();

		if (message.isRequest()) {
			COUNTER.getRequest().increaseBy(message.getPacketSize());
			connection.requestReceived((RpcRequest) message);
			connection.keepalive();
		} else {
			COUNTER.getResponse().increaseBy(message.getPacketSize());
			connection.responseReceived((RpcResponse) message);
			connection.keepalive();
		}
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		Channel c = e.getChannel();
		LOGGER.info("Channel connected:{}, {}", c.getLocalAddress(), c.getRemoteAddress());

		RpcTcpConnection connection = new RpcTcpConnection(c);
		c.setAttachment(connection);
		COUNTER.getConnections().increase();

		serverChannel.getConnectionCreated().fireEvent(connection);
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		RpcTcpConnection connection = (RpcTcpConnection) e.getChannel().getAttachment();
		e.getChannel().setAttachment(null);
		COUNTER.getConnections().decrease();

		serverChannel.getConnectionDestoryed().fireEvent(connection);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		COUNTER.getErrors().increase();
		LOGGER.warn("server handler got unhandled exception. ", e.getCause());
		LOGGER.info("local {}  remote {}.", e.getChannel().getLocalAddress(), e.getChannel().getRemoteAddress());
	}
}
