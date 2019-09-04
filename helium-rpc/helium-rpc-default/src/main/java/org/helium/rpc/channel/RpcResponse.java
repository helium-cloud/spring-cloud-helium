/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.serialization.Codec;
import org.helium.serialization.Serializer;
import org.helium.util.Outer;
import org.helium.util.io.StreamHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


/**
 * Rpc应答实体类
 * <p>
 * Created by Coral
 */
public final class RpcResponse extends RpcMessage<RpcResponseHeader> {

	final static Logger logger = LoggerFactory.getLogger(RpcResponse.class);

	private static final Codec responseCodec = Serializer.getCodec(RpcResponseHeader.class);

	public static RpcResponse createResults(Object results, String codecName) {
		RpcResponse response = new RpcResponse();
		response.setCodecName(codecName);
		response.getHeader().setCodecName(codecName);
		response.setReturnCode(RpcReturnCode.OK);
		if (results != null) {
			response.setBody(new RpcBody(results, false, null, false));
		}
		return response;
	}

	public static RpcResponse createError(Throwable error, String codecName) {
		return createError(RpcReturnCode.SERVER_ERROR, error, codecName);
	}

	public static RpcResponse createError(RpcReturnCode code, Throwable error, String codecName) {
		RpcResponse response = new RpcResponse();
		response.setReturnCode(code);
		response.setCodecName(codecName);
		response.getHeader().setCodecName(codecName);
		if (error != null) {
			response.setBody(new RpcBody(error, true));
			Map<Integer, RpcBody> extensions = response.getExtensions();
			if (extensions == null) {
				extensions = new HashMap<>();
			}
			try (ByteArrayOutputStream baOut = new ByteArrayOutputStream(); ObjectOutputStream objOut = new ObjectOutputStream(baOut)) {
				objOut.writeObject(error);
				byte[] errorBytes = baOut.toByteArray();
				extensions.put(0, new RpcBody(errorBytes));
			} catch (IOException e) {
				logger.error("serialize exception fault. {}", e);
			}
			response.setExtensions(extensions);
		}
		return response;
	}

	private RpcReturnCode returnCode;
	private Codec bodyCodec;

	public RpcResponse() {
		super(false, new RpcResponseHeader());
	}

	private RpcResponse(RpcResponseHeader header) {
		super(false, header);
		returnCode = RpcReturnCode.valueOf(header.getResponseCode());
		setCodecName(header.getCodecName());
	}

	public RpcReturnCode getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(RpcReturnCode code) {
		returnCode = code;
		getHeader().setResponseCode(code.intValue());
	}

	public void setBodyCodec(Codec codec) {
		this.bodyCodec = codec;
	}

	public void writeToStream(OutputStream out) throws IOException {
		RpcBinaryIdentity idt = new RpcBinaryIdentity();
		idt.setPacketMark(RpcBinaryIdentity.RESPONSE_MARK);
		idt.setPacketOption((short) 0);

		int bodyLength = 0;
		int packetLength = 0;
		RpcBody body = getBody();
		ByteArrayOutputStream bodyOutput = new ByteArrayOutputStream();

		if (body != null) {
			try {
				if (bodyCodec != null) {
					body.encode(bodyCodec, bodyOutput);
				} else {
					body.encode(bodyOutput);
				}
			} catch (Exception e) {
				// 如果序列化失败，则将序列化失败的异常作为结果发回对端
				body = new RpcBody(e, true);
				body.encode(bodyOutput);
				setReturnCode(RpcReturnCode.SERVER_ERROR);
			}

			bodyLength = bodyOutput.size() + 1;
			packetLength += bodyOutput.size();
		}

		RpcResponseHeader h = getHeader();
		h.setBodyLength(bodyLength);

		Outer<Integer> len = new Outer<Integer>(Integer.valueOf(packetLength));
		h.setExtensions(writeExtensions(len, bodyOutput, h.getCodecName()));
		packetLength = len.value();

		ByteArrayOutputStream headerOutput = new ByteArrayOutputStream();
		responseCodec.encode(h, headerOutput);

		idt.setHeaderSize((short) headerOutput.size());
		packetLength += headerOutput.size() + RpcBinaryIdentity.IDENTITY_SIZE;
		idt.setPacketLength(packetLength);

		out.write(idt.toBuffer());
		out.write(headerOutput.toByteArray());
		out.write(bodyOutput.toByteArray());
	}

	public static RpcResponse fromBuffer(InputStream in, RpcBinaryIdentity idt) throws IOException {
		byte[] headerBuffer = new byte[idt.getHeaderSize()];
		StreamHelper.safeRead(in, headerBuffer, 0, idt.getHeaderSize());

		RpcResponseHeader h = responseCodec.decode(headerBuffer);
		RpcResponse response = new RpcResponse(h);

		boolean asError = response.returnCode != RpcReturnCode.OK;
		int bodySize = h.getBodyLength() - 1;

		if (bodySize > 0) {
			byte[] bodyBuffer = new byte[bodySize];
			StreamHelper.safeRead(in, bodyBuffer, 0, bodySize);
			RpcBody body = new RpcBody(bodyBuffer, asError);
			response.setBody(body);
		} else if (bodySize == 0) {
			RpcBody body = new RpcBody(RpcBody.EMPTY_BUFFER, asError);
			response.setBody(body);
		} else if (bodySize < 0) {
			response.setBody(null);
		}

		if (h.getExtensions() != null) {
			for (RpcBodyExtension ext : h.getExtensions()) {
				int id = ext.getId();
				byte[] extBuffer = new byte[ext.getLength()];
				StreamHelper.safeRead(in, extBuffer, 0, ext.getLength());
				response.putRawExtension(id, extBuffer);
			}
		}
		return response;
	}
}