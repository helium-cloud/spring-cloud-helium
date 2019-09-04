/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 4, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import com.feinno.superpojo.type.DateTime;
import org.helium.threading.Future;
import org.helium.threading.FutureListener;
import org.helium.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Rpc长连接, 连接在可以双向发送请求, 并可再连接上进行协商以进一步降低通信损耗
 * <p>
 * Created by Coral
 */
public abstract class RpcConnectionReal extends RpcConnection {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcConnectionReal.class);

	private DateTime connectedTime;
	private RpcConnectionStatus status;
	private Queue<RpcRequest> pendings;    // for client

// TODO: 协商机制
//	private IntegerDictionary<RpcServerMethodCache> serverCaches;
//	private IntegerDictionary<RpcClientMethodCache> clientCaches;

	protected RpcConnectionReal(RpcEndpoint remoteEp, boolean upstream) {
		super(remoteEp, upstream);
		status = RpcConnectionStatus.INITIAL;
		pendings = new ConcurrentLinkedQueue<RpcRequest>();
	}

	/**
	 * 连接状态
	 *
	 * @return
	 */
	public RpcConnectionStatus getStatus() {
		return status;
	}

	/**
	 * 处理状态转移, 并触发响应事件
	 */
	protected void setStatus(RpcConnectionStatus status) {
		this.status = status;
		if (status == RpcConnectionStatus.CONNECTED) {
			connectedTime = DateTime.now();
		}
	}

	public void setToIdle() {
		setStatus(RpcConnectionStatus.IDLE);
	}

	/**
	 * 连接是否可用
	 *
	 * @return
	 */
	public boolean isUseable() {
		checkConnection();
		return status == RpcConnectionStatus.CONNECTED ||
				status == RpcConnectionStatus.CONNECTING;
	}

	/**
	 * 连接创建的时间
	 *
	 * @return
	 */
	public DateTime getConnectedTime() {
		return connectedTime;
	}

	/**
	 * TODO: 用于连接协商的代码, NextVersion
	 *
	 * @param cache
	 */
	public void putServerMethodCache(RpcServerMethodCache cache) {
		throw new UnsupportedOperationException("没实现呢");
	}

	/**
	 * TODO: 用于连接协商的代码, NextVersion
	 *
	 * @param cache
	 */
	public void putClientMethodCache(RpcClientMethodCache cache) {
		throw new UnsupportedOperationException("没实现呢");
	}

	/**
	 * 客户端自动连接,
	 */
	public void autoConnect() {
		final RpcEndpoint remoteEndpoint = this.getRemoteEndpoint();
		LOGGER.info("auto connect to {}", remoteEndpoint);
		setStatus(RpcConnectionStatus.CONNECTING);
		Future<Throwable> future = connect();

		future.addListener(new FutureListener<Throwable>() {

			@Override
			public void run(Result<Throwable> result) {

				Throwable args = result.getValue();
				RpcRequest request = pendings.poll();

				if (args == null) {
					LOGGER.info("auto connect to {} succeed.", remoteEndpoint);
					setStatus(RpcConnectionStatus.CONNECTED);
					while (request != null) {
						String codecName = request.getCodecName();
						try {
							doSendRequest(request);
						} catch (Exception ex) {
							LOGGER.error("sendRequest failed: {}", ex);
							int seq = request.getHeader().getSequence();
							RpcClientTransaction tx = RpcClientTransactionManager.INSTANCE.removeTransaction(seq);
							tx.setResponse(RpcResponse.createError(RpcReturnCode.CONNECTION_FAILED, ex, codecName));
						}
						request = pendings.poll();

					}
					pendings = null;
				} else {
					// CONNECTION_FAILED
					LOGGER.error("auto connect to {} failed", remoteEndpoint);
					setStatus(RpcConnectionStatus.FAILED);
					while (request != null) {
						String codecName = request.getCodecName();
						int seq = request.getHeader().getSequence();
						RpcClientTransaction tx = RpcClientTransactionManager.INSTANCE.removeTransaction(seq);
						tx.setResponse(RpcResponse.createError(RpcReturnCode.CONNECTION_FAILED, args, codecName));
						request = pendings.poll();
					}
				}
			}
		});
	}

	@Override
	public final void sendRequest(RpcClientTransaction tx, RpcRequest request) {
		int seq = RpcClientTransactionManager.INSTANCE.addTransaction(tx);
		String codecName = request.getCodecName();
		// 如果Transaction创建失败，则返回异常信息
		if (seq < 0) {
			tx.setResponse(RpcResponse.createError(RpcReturnCode.TRANSACTION_TO_MANY, null, codecName));
			return;
		}

		tx.getRequest().getHeader().setSequence(seq);
		// 如果当前状态是正在链接，则将任务放入待发送列表后离开
		if (status == RpcConnectionStatus.CONNECTING) {
			pendings.add(request);
			return;
		}
		// 发送请求，如果遇到异常，则移除事物，并返回发送失败
		try {
			doSendRequest(request);
		} catch (Exception ex) {
			LOGGER.error("sendRequest failed {}", ex);
			RpcClientTransactionManager.INSTANCE.removeTransaction(seq);
			tx.setResponse(RpcResponse.createError(RpcReturnCode.SEND_FAILED, ex, codecName));
		}
	}

	@Override
	public final void sendResponse(RpcServerTransaction tx, RpcResponse response) {
//		if (tx.isNegociating()) {
//			RpcServerMethodCache cache = tx.getMethodCache();
//			response.getHeader().setFromId(cache.getFromId());
//			response.getHeader().setToId(cache.getToId());
//		}
		doSendResponse(response);
	}

	/**
	 * 接受到请求时的事件处理, 默认的事务处理是交给统一的eventHandler
	 *
	 * @param request
	 */
	@Override
	public final void requestReceived(RpcRequest request) {
		RpcServerTransaction tx = new RpcServerTransaction(this, request);
		RpcRequestHeader header = tx.getRequest().getHeader();

		// TODO: 在1.5.1版本调试协商机制
//		int toId = header.getToId();
//		RpcServerMethodCache cache = null;
//		if (toId > 0 && serverCaches != null) {
//			cache = serverCaches.get(toId);
//			
//			int fromId = header.getFromId();
//			if (fromId > 0 && fromId != cache.getFromId()) {
//				cache = null;
//			}
//		} else {
//			cache = RpcServerMethodManager.INSTANCE.getMethodCache(
//					header.getFromComputer(), 
//					header.getFromService(),
//					header.getToService(), 
//					header.getToMethod());
//		}

		RpcServerMethodCache cache = RpcServerMethodManager.INSTANCE.getMethodCache(
				header.getFromComputer(),
				header.getFromService(),
				header.getToService(),
				header.getToMethod());

		tx.setMethodCache(cache);
		this.getTransactionCreated().fireEvent(tx);
	}

	/**
	 * 接受到应答时到事件处理
	 *
	 * @param response
	 */
	@Override
	public final void responseReceived(RpcResponse response) {
		int seq = response.getHeader().getSequence();
		RpcClientTransaction tx = RpcClientTransactionManager.INSTANCE.removeTransaction(seq);
		if (tx != null) {
			tx.setResponse(response);
		} else {
			LOGGER.error("missing transaction:" + seq);
		}
	}

	/**
	 * 连接到服务器
	 */
	public abstract Future<Throwable> connect();

	/**
	 * 事实发送请求
	 *
	 * @param request
	 */
	protected abstract void doSendRequest(RpcRequest request);

	/**
	 * 事实发送应答
	 *
	 * @param response
	 */
	protected abstract void doSendResponse(RpcResponse response);

	/**
	 * 检查连接
	 */
	protected void checkConnection() {
	}
}