/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 2, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.client;

import org.helium.rpc.channel.RpcClientTransaction;

/**
 * 客户端事务处理器, 如下的客户端程序可能从此派生,
 * <p>
 * <li>Direct (直连, 在保持单连接的基础上发送请求)</li>
 * <li>Duplex (双共连接) </li>
 * <li>NLB（负载请求，建立3条连接，每3分钟释放一次)</li>
 * <li>Short（短连接）</li>
 * </p>
 * 从设计思路上考虑, 用于将RpcMethodStub发出的请求分发到不同的处理器上进行处理,转接协议选择及连接的复杂性
 * <p>
 * Created by Coral
 */
public interface RpcClientTransactionHandler {
	/**
	 * 创建一个事务, 根据不同的场景, 处理不同的事务
	 *
	 * @return
	 */
	RpcClientTransaction createTransaction();
}
