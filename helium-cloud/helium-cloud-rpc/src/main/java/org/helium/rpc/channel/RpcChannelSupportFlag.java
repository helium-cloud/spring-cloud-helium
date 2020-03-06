/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 4, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.type.EnumInteger;

/**
 * {在这里补充类的功能说明}
 * <p>
 * Created by Coral
 */
public enum RpcChannelSupportFlag implements EnumInteger {
	NONE(0),
	CONNECTION(1),
	DUPLEX_CONNECTION(2),
	;
	private int value;

	RpcChannelSupportFlag(int value) {
		this.value = value;
	}

	public int intValue() {
		return value;
	}
}
