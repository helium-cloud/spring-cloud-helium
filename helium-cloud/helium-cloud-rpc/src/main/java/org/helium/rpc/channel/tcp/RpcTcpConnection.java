/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import org.helium.perfmon.Stopwatch;
import org.helium.rpc.channel.*;
import org.helium.threading.Future;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 基于TCP的RPC Socket连接
 *
 * @author Coral
 */
public final class RpcTcpConnection extends RpcConnectionReal {

	private static final int MAX_SEND_PENDINGS = 8 * 1024;
	/**
	 * 链接最长的存活时间
	 */
	private static final int MAX_LIFE_TIME = 180 * 1000;
	/**
	 * 链接从创建到回收的时间
	 */
	private static final int MAX_RECYCL_TIME = 300 * 1000;
	private static final long MAX_IDLE_NANOS = (long) 1E9 * 180;

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpConnection.class);

	private Channel channel;
	private ClientBootstrap bootstrap;
	private RpcTcpEndpoint remoteEndpoint;
	private AtomicInteger sendPendings;
	private Stopwatch keepalive;

	/**
	 * 从客户端创建连接
	 *
	 * @param ep
	 * @param bootstrap
	 */
	public RpcTcpConnection(RpcTcpEndpoint ep, ClientBootstrap bootstrap) {
		super(ep, true);
		this.bootstrap = bootstrap;
		this.remoteEndpoint = ep;
		sendPendings = new AtomicInteger();
		keepalive = new Stopwatch();
		LOGGER.info("### New Connection from client {} {}", this, ep);
	}

	/**
	 * 从服务器端创建连接
	 *
	 * @param channel
	 */
	public RpcTcpConnection(Channel channel) {
		super(new RpcTcpEndpoint(channel.getRemoteAddress()), false);
		this.channel = channel;
		this.remoteEndpoint = new RpcTcpEndpoint(channel.getRemoteAddress());
		this.setLocalEndpoint(new RpcTcpEndpoint(channel.getLocalAddress()));
		sendPendings = new AtomicInteger();
		keepalive = new Stopwatch();
		setStatus(RpcConnectionStatus.CONNECTED);
		LOGGER.info("### New Connection from server {} " + channel.getLocalAddress() + " -> " + channel.getRemoteAddress(), this);
	}

	@Override
	public Future<Throwable> connect() {
		final Future<Throwable> ret = new Future<Throwable>();
		RpcTcpClientHandler.COUNTER.getConnectPendings().increase();
		setStatus(RpcConnectionStatus.CONNECTING);
		ChannelFuture c = bootstrap.connect(getRemoteEndpoint().getSocketAddress());
		c.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				try {
					RpcTcpClientHandler.COUNTER.getConnectPendings().decrease();
					if (future.isSuccess()) {
						// onConnected();
						RpcTcpConnection.this.setChannel(future.getChannel());
						RpcTcpConnection.this.setStatus(RpcConnectionStatus.CONNECTED);
						future.getChannel().setAttachment(RpcTcpConnection.this);
						ret.complete(null);
					} else {
						RpcTcpConnection.this.setStatus(RpcConnectionStatus.FAILED);
						ret.complete(future.getCause());
					}
				} catch (Exception ex) {
					LOGGER.error("operationComplete failed {}", ex);
				}
			}
		});
		return ret;
	}

	protected void setChannel(Channel channel) {
		this.channel = channel;
	}

	public boolean isOpen() {
		return channel.isOpen();
	}

	public boolean isIdle() {
		return keepalive.getNanos() > MAX_IDLE_NANOS;
	}

	public boolean isRecycled() {
		if (getConnectedTime() != null) {
			return System.currentTimeMillis() - getConnectedTime().getTime() > MAX_RECYCL_TIME;
		} else {
			return false;
		}
	}

	public boolean isTimeOver() {
		if (getConnectedTime() != null) {
			return System.currentTimeMillis() - getConnectedTime().getTime() > MAX_LIFE_TIME;
		} else {
			return false;
		}
	}

	public boolean isConnected() {
		return channel.isConnected();
	}

	public RpcTcpEndpoint getRemoteEndpoint() {
		return this.remoteEndpoint;
	}

	@Override
	protected void checkConnection() {
		if (isIdle()) {
			disconnect(null);
		}
	}

	@Override
	public void doSendRequest(RpcRequest request) {
		LOGGER.debug("doSendRequest {} to {}", request.getHeader().getToService(), remoteEndpoint);

		if (getStatus() == RpcConnectionStatus.CONNECTED) {
			int pendings = sendPendings.incrementAndGet();
			RpcTcpClientHandler.COUNTER.getSendPendings().increase();
			if (pendings > MAX_SEND_PENDINGS) {
				sendPendings.decrementAndGet();
				RpcTcpClientHandler.COUNTER.getSendPendings().decrease();
				throw new RpcRuntimeException(RpcReturnCode.SEND_PENDING, "pending:" + pendings, null);
			}

			final int seq;
			final String codecName;
			ChannelFuture future;
			try {
				codecName = request.getCodecName();
				seq = request.getHeader().getSequence();
				future = channel.write(request);
				RpcTcpClientHandler.COUNTER.getRequest().increaseBy(request.getPacketSize());
			} catch (Exception ex) {
				sendPendings.decrementAndGet();
				RpcTcpClientHandler.COUNTER.getSendPendings().decrease();
				LOGGER.error("channel.write() failed {}", ex);
				throw new RuntimeException("channel.write() failed", ex);
			}

			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					LOGGER.info("message send");
					sendPendings.decrementAndGet();
					RpcTcpClientHandler.COUNTER.getSendPendings().decrease();
					if (future.isCancelled()) {
						RpcResponse response = RpcResponse.createError(RpcReturnCode.SEND_FAILED, future.getCause(), codecName);
						response.getHeader().setSequence(seq);
						responseReceived(response);
						return;
					}

					if (!future.isSuccess()) {
						RpcResponse response = RpcResponse.createError(RpcReturnCode.SEND_FAILED, future.getCause(), codecName);
						response.getHeader().setSequence(seq);
						responseReceived(response);
						RpcTcpConnection.this.disconnect(future.getCause());
						return;
					}
				}
			});

			LOGGER.debug("RpcTcpSocketConnection after channel.write(message)");
		} else {
			throw new RuntimeException("connection lost.");
		}
	}

	@Override
	public void doSendResponse(RpcResponse response) {
		LOGGER.trace("sendResponse: {}", response);

		if (channel.isConnected()) {
			ChannelFuture future = channel.write(response);
			RpcTcpClientHandler.COUNTER.getResponse().increaseBy(response.getPacketSize());

			future.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {
						LOGGER.error("send response failed: {}", future.getCause());
					} else {
						LOGGER.trace("response send");
					}
				}
			});
			LOGGER.trace("RpcTcpSocketConnection after channel.write(message)");
		} else {
			LOGGER.error("connection lost when sendResponse, idleTime: {}ms", this.keepalive.getMillseconds());
			throw new RuntimeException("connection lost.");
		}
	}

	void disconnect(Throwable cause) {
		RpcConnectionStatus status = this.getStatus();
		try {
			setStatus(RpcConnectionStatus.DISCONNECTED);
			this.close(cause);
			// channel.disconnect();
		} catch (Exception ex) {
			LOGGER.error(String.format("Invoke disconnect failed. Connection[%s] disconnect failed, %s  ", status,
					remoteEndpoint.toString()), ex);
		}
	}

	public void close(Throwable error) {
		try {
			super.close(error);
			channel.disconnect();
		} catch (Exception ex) {
			LOGGER.error(String.format("Invoke close failed. Connection[%s] disconnect failed, %s  ", this.getStatus(),
					remoteEndpoint.toString()), ex);
		}
	}

	public void keepalive() {
		keepalive.update();
	}

	@Override
	public String toString() {
		return channel != null ? channel.toString() : super.toString();
	}
}