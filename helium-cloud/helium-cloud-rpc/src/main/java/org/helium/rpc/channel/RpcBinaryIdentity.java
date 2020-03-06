/*
 * FAE, Feinno App Engine
 *
 * Create by Coral 2011-1-19
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.util.NumberUtils;
import org.helium.util.io.StreamHelper;

import java.io.IOException;
import java.io.InputStream;

/**
 * 基于TCP或UDS传输的RPC报文头，适用于二进制传输的场景
 *
 * @author Coral
 */
public final class RpcBinaryIdentity {
	public static final int IDENTITY_SIZE = 12;
	public static final int REQUEST_MARK = 0x0BADBEE0;
	public static final int RESPONSE_MARK = 0x0BADBEE1;

	private int packetMark;
	private int packetLength;
	private short headerSize;
	private short packetOption;

	public int getPacketMark() {
		return packetMark;
	}

	public void setPacketMark(int packetMark) {
		this.packetMark = packetMark;
	}

	public int getPacketLength() {
		return packetLength;
	}

	public void setPacketLength(int packetLength) {
		this.packetLength = packetLength;
	}

	public short getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(short headerSize) {
		this.headerSize = headerSize;
	}

	public short getPacketOption() {
		return packetOption;
	}

	public void setPacketOption(short packetOption) {
		this.packetOption = packetOption;
	}

	public byte[] toBuffer() {
		byte[] buffer = new byte[IDENTITY_SIZE];
		NumberUtils.fillByteBufferWithInt32(packetMark, buffer, 0);
		NumberUtils.fillByteBufferWithInt32(packetLength, buffer, 4);
		NumberUtils.fillByteBufferWithInt16(headerSize, buffer, 8);
		NumberUtils.fillByteBufferWithInt16(packetOption, buffer, 10);
		return buffer;
	}

	public static RpcBinaryIdentity fromStream(InputStream in) throws IOException {
		byte[] buffer = new byte[RpcBinaryIdentity.IDENTITY_SIZE];
		StreamHelper.safeRead(in, buffer, 0, RpcBinaryIdentity.IDENTITY_SIZE);
		return RpcBinaryIdentity.fromBuffer(buffer);
	}

	public static RpcBinaryIdentity fromBuffer(byte[] buffer) {
		RpcBinaryIdentity idt = new RpcBinaryIdentity();

		idt.setPacketMark(NumberUtils.byteArrayToInt32(buffer, 0));
		idt.setPacketLength(NumberUtils.byteArrayToInt32(buffer, 4));
		idt.setHeaderSize(NumberUtils.byteArrayToInt16(buffer, 8));
		idt.setPacketOption(NumberUtils.byteArrayToInt16(buffer, 10));

		return idt;
	}
}
