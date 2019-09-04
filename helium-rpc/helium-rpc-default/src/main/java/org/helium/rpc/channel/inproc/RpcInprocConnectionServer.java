/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 7, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.inproc;

import org.helium.rpc.channel.RpcBinaryIdentity;
import org.helium.rpc.channel.RpcConnectionShortServer;
import org.helium.rpc.channel.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * 进程内Rpc短连接服务器端
 * <p>
 * Created by Coral
 */
public class RpcInprocConnectionServer extends RpcConnectionShortServer {
	private RpcInprocConnectionClient client;

	private Logger LOGGER = LoggerFactory.getLogger(RpcInprocConnectionServer.class);

	protected RpcInprocConnectionServer(RpcInprocConnectionClient client) {
		super(RpcInprocServerChannel.INSTANCE, RpcInprocEndpoint.INSTANCE);
		this.client = client;
		RpcInprocServerChannel.INSTANCE.getConnectionCreated().fireEvent(this);
	}

	@Override
	public void doSendResponse(RpcResponse response) {
		LOGGER.info("RpcInprocConnectionClient send response.");
		// 为了配合HotServer，进程内调用时，首先需要把RpcResponse序列化一次
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			response.writeToStream(outStream);

			ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
			RpcBinaryIdentity idt = RpcBinaryIdentity.fromStream(inputStream);
			response = RpcResponse.fromBuffer(inputStream, idt);
		} catch (Exception e) {
			LOGGER.warn("Conversion inproc rpcResponse failed", e);
			response = RpcResponse.createError(e, null);
		}
		client.responseReceived(response);
	}
}
