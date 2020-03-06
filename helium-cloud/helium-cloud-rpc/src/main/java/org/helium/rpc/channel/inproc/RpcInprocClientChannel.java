/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.inproc;

import com.feinno.superpojo.type.Flags;
import org.helium.rpc.channel.*;

/**
 * {在这里补充类的功能说明}
 * <p>
 * Created by Coral
 */
public class RpcInprocClientChannel extends RpcClientChannel {
	public static final Flags<RpcChannelSupportFlag> SUPPORTS = Flags.of(RpcChannelSupportFlag.NONE);
	public static final int MAX_BODY_SIZE = 64 * 1024 * 1024;
	public static final RpcChannelSettings SETTINGS = new RpcChannelSettings("inproc", SUPPORTS, MAX_BODY_SIZE);
	public static final RpcInprocClientChannel INSTANCE = new RpcInprocClientChannel();

	/**
	 * {在这里补充功能说明}
	 */
	public RpcInprocClientChannel() {
		super(SETTINGS);
	}

	public RpcConnection createConnection(RpcEndpoint ep) {
		return new RpcInprocConnectionClient();
	}
}
