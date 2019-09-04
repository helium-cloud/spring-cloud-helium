/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.util.Event;

import java.io.IOException;

/**
 * RpcConnection表示一条Rpc连接<br>
 * 用于进行连接复用, 或在Connection上实现回调
 *
 * <p>
 * 从1.5版本开始，RpcConnection代表一条真正有状态的物理连接或短连接, 与之前的的定义不同,
 * 短连接channel会建立RpcShortConnection
 * </p>
 *
 * @auther gaolei@feinno.com
 */
public abstract class RpcConnection {
	private boolean upstream;
	private boolean closed;

	private RpcEndpoint remoteEp;
	private RpcEndpoint localEp;

	private Event<RpcServerTransaction> transactionCreated;
	private Event<Throwable> disconnected;
	private Object attachment;

	protected RpcConnection(RpcEndpoint remoteEp, boolean upstream) {
		this.remoteEp = remoteEp;
		this.upstream = upstream;

		transactionCreated = new Event<RpcServerTransaction>(this);
		disconnected = new Event<Throwable>(this);
		closed = false;
	}

	/**
	 * 获取远端地址
	 *
	 * @return
	 */
	public RpcEndpoint getRemoteEndpoint() {
		return remoteEp;
	}

	/**
	 * 获取本地地址
	 *
	 * @return
	 */
	public RpcEndpoint getLocalEndpoint() {
		return localEp;
	}


	/**
	 * 是否为上行连接
	 *
	 * @return true:server端链接；false:client端链接
	 */
	public boolean isUpstream() {
		return upstream;
	}

	/**
	 * 设置本地ep
	 *
	 * @param ep
	 */
	public void setLocalEndpoint(RpcEndpoint ep) {
		this.localEp = ep;
	}

	/**
	 * 事务创建事件
	 *
	 * @return
	 */
	public Event<RpcServerTransaction> getTransactionCreated() {
		return transactionCreated;
	}

	/**
	 * 断开连接事件
	 *
	 * @return
	 */
	public Event<Throwable> getDisconnected() {
		return disconnected;
	}

	/**
	 * 获取连接上的附件
	 *
	 * @return
	 */
	public Object getAttachment() {
		return attachment;
	}

	/**
	 * 设置连接上的附件
	 *
	 * @param attachment
	 */
	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	/**
	 * 建立一个Transaction
	 *
	 * @return
	 */
	public RpcClientTransaction createTransaction() {
		return new RpcClientTransaction(this);
	}

	/**
	 * 连接是否关闭
	 *
	 * @return
	 */
	public boolean isClosed() {
		return closed;
	}

	/**
	 * 关闭连接
	 *
	 * @param error
	 */
	public void close(Throwable error) {
		closed = true;
		disconnected.fireEvent(error);
	}

	/**
	 * 在连接上发送请求
	 *
	 * @param request
	 */
	public abstract void sendRequest(RpcClientTransaction tx, RpcRequest request) throws IOException;

	/**
	 * 在连接上发送应答
	 *
	 * @param response
	 */
	public abstract void sendResponse(RpcServerTransaction tx, RpcResponse response) throws IOException;

	/**
	 * 接受到请求时的事件处理
	 *
	 * @param request
	 */
	public abstract void requestReceived(RpcRequest request);

	/**
	 * 接受到应答时到事件处理
	 *
	 * @param response
	 */
	public abstract void responseReceived(RpcResponse response);
}
