/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.type.Flags;

/**
 * Rpc通道类的配置
 * <p>
 * Created by Coral
 */
public final class RpcChannelSettings {
	private String protocol;
	private Flags<RpcChannelSupportFlag> supportFlags;
	private int maxBodySize;

	public RpcChannelSettings(String protocol) {
		this.protocol = protocol;
	}

	public RpcChannelSettings(String protocol, Flags<RpcChannelSupportFlag> flags, int maxBodySize) {
		this.protocol = protocol;
		this.supportFlags = flags;
		this.maxBodySize = maxBodySize;
	}

	public Flags<RpcChannelSupportFlag> getSupportFlags() {
		return supportFlags;
	}

	public int getMaxBodySize() {
		return maxBodySize;
	}

	public void setMaxBodySize(int maxBodySize) {
		this.maxBodySize = maxBodySize;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
}
