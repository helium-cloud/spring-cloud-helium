/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.perfmon.Stopwatch;
import org.helium.rpc.duplex.RpcDuplexCallbackEndpoint;
import org.helium.util.Event;
import org.helium.util.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Rpc客户端Transaction抽象对象<br>
 * <p/>
 * <p>
 * 1.5.0以来RpcClientTransaction不需要再由子类继承
 * </p>
 * <p>
 * Created by Coral
 */
public final class RpcClientTransaction {
	public static int TIMEOUT = 60 * 1000;

	private static final Logger LOGGER = LoggerFactory.getLogger(RpcClientTransaction.class);

	private RpcConnection connection;
	private RpcClientMethodCache methodCache;
	private RpcRequest request;
	private RpcResponse response;
	private AtomicReference<RpcFuture> future;

	private int timeout;
	private Stopwatch watch;
	private Event<RpcResponse> transactionEnded;

	RpcClientTransaction(RpcConnection connection) {
		this.connection = connection;
		this.request = new RpcRequest();
		this.future = new AtomicReference<RpcFuture>();
		transactionEnded = null;
		timeout = TIMEOUT;
	}

	/**
	 * 设置Rpc超时
	 *
	 * @param timeout 毫秒
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * 获取超时
	 *
	 * @return 毫秒
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * 判断是否超时
	 *
	 * @return
	 */
	public Boolean isTimeout() {
		return watch.getMillseconds() > timeout;
	}

	/**
	 * 获取发送此事务的连接
	 *
	 * @return
	 */
	public RpcConnection getConnection() {
		return connection;
	}

	/**
	 * 获取客户端调用方法缓冲对象
	 *
	 * @return
	 */
	public RpcClientMethodCache getMethodCache() {
		return methodCache;
	}

	/**
	 * 设置客户端调用方法缓冲对象
	 *
	 * @param cache
	 */
	public void setMethodCache(RpcClientMethodCache cache) {
		this.methodCache = cache;
	}

	/**
	 * 获取上下文Uri
	 *
	 * @return
	 */
	public String getContextUri() {
		return request.getHeader().getContextUri();
	}

	/**
	 * 设置上下文Uri
	 *
	 * @param contextUri
	 */
	public void setContextUri(String contextUri) {
		request.getHeader().setContextUri(contextUri);
	}

	/**
	 * 设置请求参数
	 *
	 * @param args
	 */
	public void setArgs(Object args) {
		if (args != null) {
			request.setBody(new RpcBody(args, false, null, false));
		}
	}

	/**
	 * 获取推荐的 codec name （从request header中来的）
	 *
	 * @return codec name
	 */
	public String getCodecName() {
		return getRequest().getHeader().getCodecName();
	}

	/**
	 * 设置Extension
	 *
	 * @param extId
	 * @param args
	 */
	public void putExtension(int extId, Object args) {
		request.putExtension(extId, args, getCodecName());
	}

	/**
	 * 开始事务
	 *
	 * @return
	 */
	public RpcFuture begin() {
		RpcFuture f = new RpcFuture();
		if (!future.compareAndSet(null, f)) {
			throw new IllegalStateException("already began");
		}
		TraceContext tc = TraceContext.GetContext();
		if (tc != null) {
			RpcBody rbTc = new RpcBody(tc.ToTransferString(), false);
			this.putExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, rbTc);
			LOGGER.info("RPC CALL - " + tc.ToTransferString());
		}
		beforeInvoke();
		try {
			f.setTimeout(timeout);
			f.setExecutor(RpcClientTransactionManager.CALLBACK_EXECUTOR);
			connection.sendRequest(this, request);
		} catch (Exception ex) {
			setResponse(RpcResponse.createError(RpcReturnCode.SEND_FAILED, ex, getCodecName()));
		}
		return f;
	}

	public Event<RpcResponse> getTransactionEnded() {
		if (transactionEnded == null) {
			transactionEnded = new Event<RpcResponse>(this);
		}
		return transactionEnded;
	}

	public RpcRequest getRequest() {
		return request;
	}

	public RpcResponse getResponse() {
		return response;
	}

	public void setResponse(RpcResponse response) {
		this.response = response;

		RpcException error = null;
		RpcResults results;
		if (response.getReturnCode() == RpcReturnCode.OK) {
			results = new RpcResults(response, null);
		} else {
			error = new RpcException(this);
			results = new RpcResults(response, error);
		}
		TraceContext.Clear();

		if (this.request != null) {
			String traceToken = this.request.getExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, String.class);
			if (traceToken != null) {
				//临时方案，加入response中没有扩展字段，根据request中的手动添加
				// TODO 这块儿扩展字段是怎么回事儿？
				if (response.getExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, String.class) == null) {
					response.putExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, traceToken, getCodecName());
				}
				TraceContext.ApplyResponse(traceToken);
			}
		} else if (response != null) {
			String traceToken = response.getExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, String.class);
			if (traceToken != null) {
				TraceContext.ApplyResponse(traceToken);
			}
		}
		future.get().complete(results);
		afterInvoke(error);
	}

	private void beforeInvoke() {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("invoking {}", methodCache.getKey().getServiceUrl());

			if (LOGGER.isDebugEnabled()) {
				String body;
				if (request.getBody() != null) {
					body = RpcMessageUtils.dumpRpcMessageBody(request, "Args");
				} else {
					body = "null";
				}
				LOGGER.debug("request body:\n{}", body);
			}
		}

		this.watch = methodCache.getCounter().begin();
	}

	private void afterInvoke(RpcException error) {
		if (error == null) {
			this.watch.end();
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("invoke OK: {}", methodCache.getKey().getServiceUrl());
			}

			if (LOGGER.isDebugEnabled()) {
				String abody;
				String rbody;
				if (request.getBody() != null) {
					abody = RpcMessageUtils.dumpRpcMessageBody(request, "Args");
				} else {
					abody = "null";
				}
				if (response.getBody() != null) {
					rbody = RpcMessageUtils.dumpRpcMessageBody(response, "Results");
				} else {
					rbody = "Results = null";
				}
				LOGGER.debug("invoke messages :\n {} \n {}", abody, rbody);
			}
		} else {
			this.watch.fail(error);
			String abody;
			String rbody;
			if (request.getBody() != null) {
				abody = RpcMessageUtils.dumpRpcMessageBody(request, "Args");
			} else {
				abody = "null";
			}
			if (response.getBody() != null) {
				rbody = RpcMessageUtils.dumpRpcMessageBody(response, "Results");
			} else {
				rbody = "Results = null";
			}
			LOGGER.error("{}.{} invoke failed {}", request.getHeader().getToService(), request.getHeader().getToMethod(), error);
			// LOGGER.error(String.format("invoke failed :\n %s \n %s \n", abody, rbody), error);
		}
	}

	public int getElapsedMilliseconds() {
		return (int) watch.getMillseconds();
	}

	public void copyContext(RpcServerTransaction tx) {
		RpcRequest request = tx.getRequest();
		RpcClientMethodCache cache = RpcClientMethodManager.INSTANCE.getMethodCache(RpcDuplexCallbackEndpoint.INSTANCE, request.getHeader().getToService(), request.getHeader().getToMethod());
		setMethodCache(cache);
		this.request = request;
	}
}
