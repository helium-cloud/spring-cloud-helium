/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-19
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel.tcp;

import org.helium.rpc.channel.RpcBinaryIdentity;
import org.helium.rpc.channel.RpcMessage;
import org.helium.rpc.channel.RpcRequest;
import org.helium.rpc.channel.RpcResponse;
import org.helium.util.io.BinaryBackedInputStream;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * 基于TCP传输的RPC消息解码器
 *
 * @author Coral
 */
public class RpcTcpMessageDecoder extends FrameDecoder {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcTcpMessageDecoder.class);

	private final int maxBodySize;

	public RpcTcpMessageDecoder(int maxBodySize) {
		this.maxBodySize = maxBodySize;
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {

		try {
			//
			// 先读取消息头
			buffer.markReaderIndex();
			int readableBytes = buffer.readableBytes();
			LOGGER.trace("got some data, size [{}]", buffer.readableBytes());

			//
			// 如果未读取到消息头长度, 则重置标记, 直到读取到消息头为止
			if (readableBytes < RpcBinaryIdentity.IDENTITY_SIZE) {
				buffer.resetReaderIndex();
				return null;
			}

			//
			// 解析消息头
			byte[] idtBuffer = new byte[RpcBinaryIdentity.IDENTITY_SIZE];
			buffer.readBytes(idtBuffer);
			RpcBinaryIdentity idt = RpcBinaryIdentity.fromBuffer(idtBuffer);
			int mark = idt.getPacketMark();

			//
			// 如果消息头非法，直接关闭连接
			if (mark != RpcBinaryIdentity.REQUEST_MARK && mark != RpcBinaryIdentity.RESPONSE_MARK) {
				LOGGER.error("Unrecognized packet mark [{}], closing (netty)channel.", mark);
				channel.close();
				return null;
			}

			int packetLength = idt.getPacketLength();
			int nextToRead = packetLength - RpcBinaryIdentity.IDENTITY_SIZE;

			LOGGER.trace("packet length is {}", packetLength);

			//
			// 检测包体长度是否合法
			if (packetLength > maxBodySize) {
				LOGGER.warn("Body size too large [{}], closing (netty)channel.", packetLength);
				channel.close();
				return null;
			}

			//
			// 如果未能读够包体长度, 就重来
			if (buffer.readableBytes() < nextToRead) {
				buffer.resetReaderIndex();
				return null;
			}

			//
			// 拷贝至新建缓冲区 (OPT:如果直接在ChannelBuffer上直接读取可避免一次copy过程)
			ByteBuffer packetBuffer = ByteBuffer.allocate(packetLength - RpcBinaryIdentity.IDENTITY_SIZE);
			buffer.readBytes(packetBuffer);
			packetBuffer.flip();

			BinaryBackedInputStream in = new BinaryBackedInputStream(packetBuffer);
			RpcMessage message;
			if (idt.getPacketMark() == RpcBinaryIdentity.REQUEST_MARK) {
				message = RpcRequest.fromBuffer(in, idt);
			} else {
				message = RpcResponse.fromBuffer(in, idt);
			}
			message.setPacketSize(packetLength);
			return message;
		} catch (Exception ex) {
			LOGGER.error("messageDecode failed: {} connect reset", ex);
			channel.close();
			throw ex;
		}
	}
}
