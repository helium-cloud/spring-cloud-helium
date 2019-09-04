/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;


import org.helium.util.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rpc服务器信道, 通过继承此类来实现具体的通信代码
 * <p>
 * Created by Coral
 */
public abstract class RpcServerChannel{
	private static Logger LOGGER = LoggerFactory.getLogger(RpcServerChannel.class);

	private Object sync;
	private boolean started;
	private RpcEndpoint serverEp;
	private RpcChannelSettings settings;
	private Event<RpcServerTransaction> transactionCreated;
	private Event<RpcConnection> connectionCreated;
	private Event<RpcConnection> connectionDestoryed;

	protected RpcServerChannel(RpcChannelSettings settings, RpcEndpoint serverEp) {
		this.settings = settings;
		this.serverEp = serverEp;
		this.started = false;
		this.sync = new Object();

		transactionCreated = new Event<RpcServerTransaction>(this);
		connectionCreated = new Event<RpcConnection>(this);
		connectionDestoryed = new Event<RpcConnection>(this);
	}

	/**
	 * 获取服务器端的监听地址
	 *
	 * @return
	 */
	public RpcEndpoint getServerEndpoint() {
		return serverEp;
	}

	/**
	 * 是否启动
	 *
	 * @return
	 */
	public boolean isStarted() {
		return started;
	}

	/**
	 * 获取事务创建的Event
	 *
	 * @return
	 */
	public Event<RpcServerTransaction> getTransactionCreated() {
		return transactionCreated;
	}

	/**
	 * 获取连接创建的Event
	 *
	 * @return
	 */
	public Event<RpcConnection> getConnectionCreated() {
		return connectionCreated;
	}

	/**
	 * 获取连接销毁的Event
	 *
	 * @return
	 */
	public Event<RpcConnection> getConnectionDestoryed() {
		return connectionDestoryed;
	}

	/**
	 * 获取当时通道设置
	 *
	 * @return
	 */
	public RpcChannelSettings getSettings() {
		return settings;
	}

	/**
	 * 启动Rpc服务器端通道的端口监听
	 *
	 * @throws RpcException
	 */
	public void start() throws Exception {
		if (!started) {
			synchronized (sync) {
				if (!started) {
					try {
						doStart();
						started = true;
						LOGGER.info("RpcServerChannel started in {}", serverEp);
					} catch (Exception ex) {
						LOGGER.error("RpcServerChannel start failed in {} \r\n{}", serverEp, ex);
						throw new Exception("Channel started failed", ex);
					}
				}
			}
		}
	}

	/**
	 * 停止Rpc服务器端通道的端口监听
	 *
	 * @throws RpcException
	 */
	public void stop() {
		if (started) {
			synchronized (sync) {
				if (started) {
					try {
						doStop();
						started = false;
						LOGGER.info("RpcServerChannel stopped in {}", serverEp);
					} catch (Exception ex) {
						LOGGER.error("RpcServerChannel stop failed in {} \r\n {}", serverEp, ex);
					}
				}
			}
		}
	}

	/**
	 * 启动通道的监听
	 *
	 * @throws Exception
	 */
	protected abstract void doStart() throws Exception;

	/**
	 * 停止通道的监听
	 *
	 * @throws Exception
	 */
	protected abstract void doStop() throws Exception;
}
