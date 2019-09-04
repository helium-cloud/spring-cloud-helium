/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.inproc;

import org.helium.rpc.channel.RpcConnection;
import org.helium.rpc.channel.RpcServerChannel;
import org.helium.rpc.channel.RpcServerTransaction;
import org.helium.util.EventHandler;

/**
 * 进程内服务器调用
 * <p>
 * Created by Coral
 */
public class RpcInprocServerChannel extends RpcServerChannel {
	public static final RpcInprocServerChannel INSTANCE = new RpcInprocServerChannel();

	private RpcInprocServerChannel() {
		super(RpcInprocClientChannel.SETTINGS, RpcInprocEndpoint.INSTANCE);

		this.getConnectionCreated().addListener(new EventHandler<RpcConnection>() {
			@Override
			public void run(Object sender, RpcConnection args) {
				args.getTransactionCreated().addListener(new EventHandler<RpcServerTransaction>() {
					@Override
					public void run(Object sender, RpcServerTransaction args) {
						RpcInprocServerChannel.this.getTransactionCreated().fireEvent(args);
					}
				});
			}
		});
	}

	/*
	 * @see org.helium.rpc.channel.RpcServerChannel#doStart()
	 */

	/**
	 * 启动Channel, 没啥好启动的
	 *
	 * @throws Throwable
	 */
	@Override
	protected void doStart() throws Exception {
		// do nothing		
	}

	/*
	 * @see org.helium.rpc.channel.RpcServerChannel#doStop()
	 */

	/**
	 * 也没啥好停止的
	 *
	 * @throws Throwable
	 */
	@Override
	protected void doStop() throws Exception {
		// do nothing
	}
}
