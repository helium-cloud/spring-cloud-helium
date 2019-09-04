/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2012-3-2
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.threading.Future;

import java.util.concurrent.TimeoutException;

/**
 * Rpc调用结果返回，可用作异步处理
 * <p>
 * Created by Coral
 *
 * @see org.helium.threading.Future
 */
public class RpcFuture extends Future<RpcResults> {
	RpcFuture() {
	}


	/**
	 * 获取返回码
	 *
	 * @return
	 */
	public RpcReturnCode getReturnCode() {
		if (isDone()) {
			try {
				return getValue().getReturnCode();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new IllegalStateException("future not done!");
		}
	}

	/**
	 * 获取错误
	 *
	 * @return
	 */
	public RpcException getError() {
		if (isDone()) {
			try {
				return getValue().getError();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			throw new IllegalStateException("future not done!");
		}
	}

	/**
	 * 获取返回结果, 需要先检查getError, 如果未完成会抛出IllegalStateException,
	 *
	 * @param clazz 反序列化的结果类型
	 * @return
	 * @throws IllegalStateException
	 */
	public <E> E getResult(Class<E> clazz) throws RpcException {
		if (isDone()) {
			return syncGet(clazz);
		} else {
			throw new IllegalStateException("future not done!");
		}
	}

	/**
	 * 同步获取结果, 超时时间设置为
	 *
	 * @param clazz
	 * @return
	 * @throws RpcException
	 */
	public <E> E syncGet(Class<E> clazz) throws RpcException {
		RpcResults r;
		try {
			r = this.getValue();
		} catch (TimeoutException e) {
			throw new RpcException("Rpc invoke timeout.", e);
		} catch (Exception e) {
			throw new RpcException("Rpc invoke fault", e);
		}
		if (r.getError() != null) {
			throw r.getError();
		} else {
			return r.getValue(clazz);
		}
	}
}