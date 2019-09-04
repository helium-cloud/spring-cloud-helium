/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-5
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.type.EnumInteger;

/**
 * 标记RPC消息的支持选项, 在此版本内并未实现
 * <p>
 * Created by Coral
 */
public enum RpcMessageOption implements EnumInteger {
	NONE(0),
	// AUTOPROXY(1),
	;

	RpcMessageOption(int value) {
		this.value = value;
	}

	private int value;

	public int intValue() {
		return value;
	}
}
