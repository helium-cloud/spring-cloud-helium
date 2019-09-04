/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-19
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.rpc.channel.RpcMessage;
import org.helium.rpc.channel.RpcRequest;
import org.helium.rpc.channel.RpcResponse;
import org.jboss.netty.channel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于TCP传输的RPC客户端处理器
 *
 * @author Coral
 */
public class RpcTcpClientHandler extends SimpleChannelHandler {

	static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpClientHandler.class);
	static final RpcTcpPerformanceCounter COUNTER = PerformanceCounterFactory.getCounters(RpcTcpPerformanceCounter.class, "client");

	public RpcTcpClientHandler() {
		super();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		try {
			RpcMessage message = (RpcMessage) e.getMessage();
			LOGGER.trace("message received:" + (message.isRequest() ? "request" : "response"));
			RpcTcpConnection connection = (RpcTcpConnection) e.getChannel().getAttachment();

			if (message.isResponse()) {
				// RESPONSE
				RpcResponse response = (RpcResponse) message;
				LOGGER.debug("<<< receiveResponse seq={} from: {}", response.getHeader().getSequence(), connection.getRemoteEndpoint());
				connection.responseReceived(response);
				connection.keepalive();
				COUNTER.getResponse().increaseBy(message.getPacketSize());
			} else {
				// REQUEST
				RpcRequest request = (RpcRequest) message;
				LOGGER.debug(">>> receiveRequest seq={} from:{}", request.getHeader().getSequence(), connection.getRemoteEndpoint());
				connection.requestReceived((RpcRequest) message);
				connection.keepalive();
				COUNTER.getRequest().increaseBy(message.getPacketSize());
			}
		} catch (Exception ex) {
			LOGGER.error("messageReceived catch Exception {}", ex);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		COUNTER.getErrors().increase();
		LOGGER.warn("client handler got unhandled exception. ", e.getCause());
		LOGGER.info("local {}  remote {}.", e.getChannel().getLocalAddress(), e.getChannel().getRemoteAddress());
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		Channel c = e.getChannel();
		LOGGER.trace("Channel connected:{}, {}", c.getLocalAddress(), c.getRemoteAddress());
		COUNTER.getConnections().increase();
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		Channel c = e.getChannel();
		LOGGER.info("Channel disconnected:{}, {}", c.getLocalAddress(), c.getRemoteAddress());
		RpcTcpConnection connection = (RpcTcpConnection) c.getAttachment();
		connection.disconnect(null);
		COUNTER.getConnections().decrease();
	}
}
