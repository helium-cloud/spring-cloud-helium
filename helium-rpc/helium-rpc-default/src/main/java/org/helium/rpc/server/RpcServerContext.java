/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.server;

import org.helium.rpc.channel.*;
import org.helium.serialization.Codec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Rpc服务器端的上下文对象，在请求到达服务器端时创建，在返回后释放
 * <p>
 * Created by Coral
 */
public class RpcServerContext {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerContext.class);

// TODO: 使用线程上下文方式获取RpcServerContext
//	public static RpcServerContext getCurrent()
//	{
//		RpcServerContext ctx = (RpcServerContext) ThreadContext.getCurrent().getNamedContext(ContextName.RPC);
//		if (ctx == null) {
//			throw new IllegalStateException(
//					"You *MUST* annotated your method with @RpcMethod(threadContext = true) to use this future");
//		}
//		return ctx;
//	}

	private AtomicBoolean done;
	private RpcServerTransaction tx;
	private RpcResponse respForExts;

	public RpcServerContext(RpcServerTransaction tx) {
		this.tx = tx;
		this.done = new AtomicBoolean(false);
		respForExts = null;
	}

	/**
	 * 获取RpcServerTransaction对象
	 *
	 * @return
	 */
	public RpcServerTransaction getTx() {
		return tx;
	}


	/**
	 * 获取客户端到请求参数
	 *
	 * @param <E>
	 * @param clazz
	 * @return
	 */
	public <E> E forceGetArgs(Class<E> clazz) {
		RpcBody body = tx.getRequest().getBody();
		if (body == null) {
			return null;
		} else {
			if (Void.class.equals(clazz)) {
				return null;
			}
			try {
				Codec codec = tx.getMethodCache().getArgsCodec();
				if (codec != null) {
					return (E) body.forceDecode(codec);
				} else {
					return (E) body.forceDecode(clazz);
				}
			} catch (IOException e) {
				String msg = String.format("class<%s> decode args failed", clazz.getName());
				LOGGER.error(msg);
				throw new IllegalArgumentException(msg, e);
			}
		}
	}

	/**
	 * 获取客户端到请求参数
	 *
	 * @param <E>
	 * @param clazz
	 * @return
	 */
	public <E> E getArgs(Class<E> clazz) {
		RpcBody body = tx.getRequest().getBody();
		if (body == null) {
			return null;
		} else {
			if (Void.class.equals(clazz)) {
				return null;
			}
			try {
				Codec codec = tx.getMethodCache().getArgsCodec();
				if (codec != null) {
					return (E) body.decode(codec);
				} else {
					return (E) body.decode(clazz);
				}
			} catch (IOException e) {
				String msg = String.format("class<%s> decode args failed", clazz.getName());
				LOGGER.error(msg);
				throw new IllegalArgumentException(msg, e);
			}
		}
	}

	/**
	 * 获取访问的服务名
	 *
	 * @return
	 */
	public String getToService() {
		return tx.getMethodCache().getKey().getService();
	}

	/**
	 * 获取访问的方法名
	 *
	 * @return
	 */
	public String getToMethod() {
		return tx.getMethodCache().getKey().getMethod();
	}

	/**
	 * 获取来源服务器
	 *
	 * @return
	 */
	public String getFromComputer() {
		return tx.getMethodCache().getKey().getFromComputer();
	}

	/**
	 * 获取来源服务
	 *
	 * @return
	 */
	public String getFromService() {
		return tx.getMethodCache().getKey().getFromService();
	}

	/**
	 * 获取上下文Uri, 一般用于Proxy路由或上下文辨识，增加Extension字段后，一般用格式化更好的Extension字段代替
	 *
	 * @return
	 */
	public String getContextUri() {
		return tx.getRequest().getHeader().getContextUri();
	}

	/**
	 * 获取本请求所在的连接
	 *
	 * @return
	 */
	public RpcConnection getConnection() {
		return tx.getConnection();
	}

	/**
	 * 获取原始到请求缓冲区
	 *
	 * @return
	 */
	public byte[] getRawBody() {
		RpcBody body = tx.getRequest().getBody();
		if (body == null) {
			return null;
		} else {
			return body.getBuffer();
		}
	}

	/**
	 * 获取客户端发送到Extension
	 *
	 * @param <E>
	 * @param clazz
	 * @return
	 */
	public <E> E getExtension(int id, Class<E> clazz) {
		return tx.getRequest().getExtension(id, clazz);
	}

	/**
	 * 获取原始的Extension缓冲区
	 *
	 * @param id
	 * @return
	 */
	public byte[] getRawExtension(int id) {
		return tx.getRequest().getRawExtension(id);
	}

	/**
	 * 设置返回的extension
	 *
	 * @param id
	 * @param ext
	 */
	public void putExtension(int id, Object ext) {
		if (respForExts == null) {
			respForExts = RpcResponse.createResults(null, getTx().getCodecName());
		}
		respForExts.putExtension(id, ext, getTx().getCodecName());
	}

	/**
	 * 设置返回的extension
	 *
	 * @param id
	 * @param buffer
	 */
	public void putRawExtension(int id, byte[] buffer) {
		if (respForExts == null) {
			respForExts = RpcResponse.createResults(null, getTx().getCodecName());
		}
		respForExts.putRawExtension(id, buffer);
	}

	/***
	 * 是否已经返回过结果，结果只允许返回一次
	 *
	 * @return
	 */
	public boolean isDone() {
		return done.get();
	}

	/**
	 * 将正常结果返回给客户端, 无包体
	 */
	public void end() {
		internalEnd(RpcReturnCode.OK, null, null);
	}

	/**
	 * 将正常结果返回给客户端
	 *
	 * @param results
	 */
	public <R> void end(R results) {
		internalEnd(RpcReturnCode.OK, results, null);
	}

	/**
	 * 将错误结果返回给客户端
	 *
	 * @param code  错误码
	 * @param error 错误
	 */
	public void end(RpcReturnCode code, Throwable error) {
		internalEnd(code, null, error);
	}

	/**
	 * 返回服务器内部错误
	 *
	 * @param error
	 */
	public void endWithError(Throwable error) {
		internalEnd(RpcReturnCode.SERVER_ERROR, null, error);
	}

	/**
	 * 直接把结果返回给客户端
	 *
	 * @param response
	 */
	public void endWithResponse(RpcResponse response) {
		if (done.compareAndSet(false, true)) {
			tx.setResponse(response);
		} else {
			LOGGER.error("ServerTransaction already returned tx:{}", tx);
			throw new IllegalStateException("ServerTransaction already returned");
		}
	}

	private <R> void internalEnd(RpcReturnCode code, R results, Throwable error) {
		if (done.compareAndSet(false, true)) {
			RpcResponse response;
			if (code == RpcReturnCode.OK) {
				response = RpcResponse.createResults(results, getTx().getCodecName());
			} else {
				response = RpcResponse.createError(code, error, getTx().getCodecName());
			}
			if (respForExts != null) {
				response.setExtensions(respForExts.getExtensions());
			}
			tx.setResponse(response);
		} else {
			LOGGER.error("ServerTransaction already returned tx:{}", tx);
			throw new IllegalStateException("ServerTransaction already returned");
		}
	}
}
