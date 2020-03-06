/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import java.io.IOException;

/**
 * 获取Rpc返回结果的实体类
 * <p>
 * Created by Coral
 */
public final class RpcResults {
	private RpcException error;
	private RpcResponse response;

	public RpcResults(RpcResponse response, RpcException error) {
		this.response = response;
		this.error = error;
	}

	/**
	 * 获取返回码
	 *
	 * @return
	 */
	public RpcReturnCode getReturnCode() {
		return response.getReturnCode();
	}

	public RpcException getError() {
		return error;
	}

	public <E> E getValue(Class<E> clazz) {
		if (Void.class.equals(clazz)) {
			return null;
		}
		RpcBody body = response.getBody();
		return decodeBody(body, clazz);
	}

	public byte[] getRawBuffer() {
		RpcBody body = response.getBody();
		if (body == null) {
			return null;
		} else {
			return body.getBuffer();
		}
	}

	public <E> E getExtension(int id, Class<E> clazz) {
		return response.getExtension(id, clazz);
	}

	public byte[] getRawExtension(int id) {
		return response.getRawExtension(id);
	}

	public RpcResponse getResponse() {
		return this.response;
	}

	private <E> E decodeBody(RpcBody body, Class<E> clazz) {
		if (body != null) {
			try {
				return (E) body.decode(clazz);
			} catch (IOException e) {
				throw new IllegalArgumentException("decode failed:" + clazz.getName(), e);
			}
		} else {
			return null;
		}
	}
}
