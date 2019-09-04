/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 7, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.perfmon.SmartCounter;
import org.helium.serialization.Codec;

/**
 * Rpc服务器端方法缓冲
 * <p>
 * Created by Coral
 */
public class RpcServerMethodCache {
	private RpcServerMethodKey key;

	private int toId;
	private int fromId;
	private SmartCounter counter;
	private RpcServerMethodHandler handler;

	public RpcServerMethodCache(RpcServerMethodKey key) {
		this.key = key;
	}

	public RpcServerMethodKey getKey() {
		return key;
	}

	public void setKey(RpcServerMethodKey key) {
		this.key = key;
	}

	public SmartCounter getCounter() {
		return counter;
	}

	public void setCounter(SmartCounter counter) {
		this.counter = counter;
	}

	public Codec getArgsCodec() {
		if (handler == null) {
			return null;
		} else {
			return handler.getArgsCodec();
		}
	}

	public Codec getResultsCodec() {
		if (handler == null) {
			return null;
		} else {
			return handler.getResultsCodec();
		}
	}

	public int getToId() {
		return toId;
	}

	public void setToId(int id) {
		this.toId = id;
	}

	public int getFromId() {
		return this.fromId;
	}

	public void setFromId(int id) {
		this.fromId = id;
	}

	public RpcServerMethodHandler getHandler() {
		return handler;
	}

	public void setHandler(RpcServerMethodHandler handler) {
		this.handler = handler;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("method cache (").append(getKey().toString()).append(')');
		return sb.toString();
	}
}
