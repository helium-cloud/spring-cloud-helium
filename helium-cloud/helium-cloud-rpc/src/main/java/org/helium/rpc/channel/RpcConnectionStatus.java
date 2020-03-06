/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 4, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

/**
 * Rpc真实连接的连接状态
 * <p>
 * Created by Coral
 */
public enum RpcConnectionStatus {
	/**
	 * 创建时的状态
	 */
	INITIAL,
	/**
	 * 正在连接
	 */
	CONNECTING,
	/**
	 * 已经连接上
	 */
	CONNECTED,
	/**
	 * 连接失败
	 */
	FAILED,
	/**
	 * 断开
	 */
	DISCONNECTED,
	/**
	 * 手工设置为空闲状态
	 */
	IDLE,
}
