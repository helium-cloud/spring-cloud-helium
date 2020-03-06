/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 1, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.inproc;

import org.helium.rpc.channel.RpcBinaryIdentity;
import org.helium.rpc.channel.RpcConnectionShortClient;
import org.helium.rpc.channel.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 进程内Rpc短连接客户端
 * <p>
 * Created by Coral
 */
public class RpcInprocConnectionClient extends RpcConnectionShortClient {
	private RpcInprocConnectionServer server;

	private Logger LOGGER = LoggerFactory.getLogger(RpcInprocConnectionClient.class);

	public RpcInprocConnectionClient() {
		super(RpcInprocEndpoint.INSTANCE);
		server = new RpcInprocConnectionServer(this);
	}

	@Override
	public void doSendRequest(RpcRequest request) {
		LOGGER.info("RpcInprocConnectionClient send request.");
		// 为了配合HotServer，进程内调用时，首先需要把RpcRequest序列化一次
		try {
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			request.writeToStream(outStream);

			ByteArrayInputStream inputStream = new ByteArrayInputStream(outStream.toByteArray());
			RpcBinaryIdentity idt = RpcBinaryIdentity.fromStream(inputStream);
			request = RpcRequest.fromBuffer(inputStream, idt);
		} catch (IOException e) {
			LOGGER.warn("Conversion inproc rpcRequest failed", e);
		}
		server.requestReceived(request);
	}
}
