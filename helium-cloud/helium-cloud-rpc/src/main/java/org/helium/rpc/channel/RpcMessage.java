/*
 * FAE, Feinno App Engine
 *
 * Create by windcraft Aug 16, 2011
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.util.Outer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * <code>
 * class RpcRequest extends RpcMessage<RpcRequestHeader>
 * class RpcResponse extends RpcMessage<RpcResponseHeader>
 * </code>
 * <p>
 * Created by Coral
 */
public abstract class RpcMessage<E> {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcMessage.class);

	private boolean isRequest;
	private E header;
	private RpcBody body;
	private Map<Integer, RpcBody> extensions;
	private int packetSize;

	private String codecName;

	protected RpcMessage(boolean isRequest, E header) {
		this.isRequest = isRequest;
		this.header = header;
	}

	/**
	 * 获取编码名称
	 *
	 * @return
	 */
	public String getCodecName() {
		return codecName;
	}

	/**
	 * 设置编码名称，如果设置null则使用默认编码
	 *
	 * @param codecName 编码名称
	 */
	public void setCodecName(String codecName) {
		this.codecName = codecName;
	}

	/**
	 * 是否为请求消息
	 *
	 * @return
	 */
	public boolean isRequest() {
		return this.isRequest;
	}

	/**
	 * 是否为应答消息
	 *
	 * @return
	 */
	public boolean isResponse() {
		return !this.isRequest();
	}

	/**
	 * 获取包头
	 *
	 * @return
	 */
	public E getHeader() {
		return header;
	}

	/**
	 * 获取消息体
	 *
	 * @return
	 */
	public RpcBody getBody() {
		return body;
	}

	/**
	 * 设置消息体
	 *
	 * @param body
	 */
	public void setBody(RpcBody body) {
		if (body != null && body.getCodecName() == null) {
			body.setCodecName(getCodecName());
		}
		this.body = body;
	}

	/**
	 * 获取扩展字段
	 *
	 * @param id
	 * @param extClazz
	 * @return
	 */
	public <V> V getExtension(int id, Class<V> extClazz) {
		if (extensions == null) {
			return null;
		} else {
			RpcBody body = extensions.get(id);
			if (body == null) {
				return null;
			} else {
				try {
					body.setCodecName(getCodecName());
					return (V) body.decode(extClazz);
				} catch (IOException e) {
					LOGGER.error("decode extension failed {} {}", extClazz, e);
					throw new IllegalArgumentException("decode failed with:" + extClazz.getCanonicalName(), e);
				}
			}
		}
	}

	/**
	 * 获取原始扩展字段
	 *
	 * @param id
	 * @return
	 */
	public byte[] getRawExtension(int id) {
		if (extensions == null) {
			return null;
		} else {
			RpcBody body = extensions.get(id);
			if (body == null) {
				return null;
			} else {
				return body.getBuffer();
			}
		}
	}

	/**
	 * 设置扩展字段
	 *
	 * @param id
	 * @param args
	 */
//    public void putExtension(int id, Object args) {
//        if (extensions == null) {
//            extensions = new Hashtable<Integer, RpcBody>();
//        }
//
//        RpcBody body;
//        if (args instanceof RpcBody) {
//            body = (RpcBody) args;
//        } else {
//            body = new RpcBody(args);
//        }
//        extensions.put(id, body);
//    }

	/**
	 * 设置扩展字段
	 *
	 * @param id
	 * @param args
	 */
	public void putExtension(int id, Object args, String codecName) {
		if (args == null) {
			return;
		}

		if (extensions == null) {
			extensions = new Hashtable<Integer, RpcBody>();
		}

		RpcBody body;
		if (args instanceof RpcBody) {
			body = (RpcBody) args;
			if (body != null && body.getCodecName() == null) {
				body.setCodecName(codecName);
			}
		} else {
			body = new RpcBody(args, false, codecName, false);
		}
		extensions.put(id, body);
	}

	/**
	 * 设置原始扩展字段
	 *
	 * @param id
	 * @param buffer
	 */
	public void putRawExtension(int id, byte[] buffer) {
		if (extensions == null) {
			extensions = new Hashtable<Integer, RpcBody>();
		}

		RpcBody body = new RpcBody(buffer);
		extensions.put(id, body);
	}

	/**
	 * 获取全部扩展字段
	 *
	 * @return
	 */
	public Map<Integer, RpcBody> getExtensions() {
		return extensions;
	}

	/**
	 * 设置全部扩展字段
	 *
	 * @param exts
	 */
	public void setExtensions(Map<Integer, RpcBody> exts) {
		this.extensions = exts;
	}

	protected List<RpcBodyExtension> writeExtensions(Outer<Integer> packetLength, ByteArrayOutputStream out, String codecName) throws IOException {
		if (extensions == null)
			return null;

		List<RpcBodyExtension> list = new ArrayList<RpcBodyExtension>();
		for (Entry<Integer, RpcBody> e : extensions.entrySet()) {
			int position = out.size();
			if (e.getValue().getCodecName() == null) {
				e.getValue().setCodecName(codecName);
			}
			e.getValue().encode(out);
			int size = out.size() - position;
			packetLength.setValue(packetLength.value() + size);

			RpcBodyExtension ext = new RpcBodyExtension();
			ext.setId(e.getKey());
			ext.setLength(size);
			list.add(ext);
		}
		return list;
	}

	/**
	 * 获取包体大小, RpcMessage系列对象不处理packageSize, 仅为保存相关数据
	 *
	 * @return
	 */
	public int getPacketSize() {
		return packetSize;
	}

	/**
	 * 设置包体大小, RpcMessage系列对象不处理packageSize, 仅为保存相关数据
	 *
	 * @param size
	 */
	public void setPacketSize(int size) {
		this.packetSize = size;
	}
}
