/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-4
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.type.EnumInteger;

import java.util.HashMap;
import java.util.Map;

/**
 * Rpc应答码
 * <p>
 * 所有大于0的错误由对端服务器返回, 在协议中约定, 小于0的错误在客户端产生
 * </p>
 * <p>
 * Created by Coral
 */
public enum RpcReturnCode implements EnumInteger {
	/**
	 * 未知
	 */
	UNKOWN(0),

	/**
	 * 成功
	 */
	OK(200),

	/**
	 * 服务器端反序列化请求参数时发生错误
	 */
	REQUEST_FORMAT_FAILED(400),

	/**
	 * 服务不存在
	 */
	SERVICE_NOT_FOUND(404),

	/**
	 * 方法不存在
	 */
	METHOD_NOT_FOUND(405),

	/**
	 * 会话不存在, 在转发过程中出现
	 */
	SESSION_NOT_FOUND(481),

	/**
	 * 服务器内部错误
	 */
	SERVER_ERROR(500),

	/**
	 * 服务器忙
	 */
	SERVER_BUSY(503),

	/**
	 * 服务器端检查失败, 调用方状态或请求参数不合法
	 */
	SERVER_ASSERT_FAILED(505),

	/**
	 * 服务器转发失败, 由转发服务返回
	 */
	SERVER_TRANSFER_ERROR(504),

	/**
	 * 当发送请求时, 建立连接失败
	 */
	CONNECTION_FAILED(-1),

	/**
	 * 发送失败, 短连接发送或建立连接后发送失败
	 */
	SEND_FAILED(-2),

	/**
	 * 单条连接上请求报文堆积过多
	 */
	SEND_PENDING(-3),

	/**
	 * 客户端请求超时
	 */
	TRANSACTION_TIMEOUT(-4),

	/**
	 * 客户端未完成事务过多
	 */
	TRANSACTION_TO_MANY(-5),

	/**
	 * 连接失败, 请求发送后在未接受到应答前, 连接断开
	 */
	CONNECTION_BROKEN(-6),

	;
	private int value;

	RpcReturnCode(int value) {
		this.value = value;
		AllValues.table.put(Integer.valueOf(value), this);
	}

	public int intValue() {
		return value;
	}

	public static RpcReturnCode valueOf(int value) {
		if (value == 200) {
			return RpcReturnCode.OK;
		} else {
			RpcReturnCode code = AllValues.table.get(Integer.valueOf(value));
			if (code == null) {
				return UNKOWN;
			} else {
				return code;
			}
		}
	}

	private static class AllValues {
		static Map<Integer, RpcReturnCode> table = new HashMap<Integer, RpcReturnCode>();
	}
}
