/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2010-11-25
 *
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.perfmon.Stopwatch;
import org.helium.util.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * {在这里补充类的功能说明}
 * <p>
 * Created by Coral
 */
public final class RpcServerTransaction {
	private static final Logger LOGGER = LoggerFactory.getLogger(RpcServerTransaction.class);

	private boolean negociating;
	private RpcServerMethodCache methodCache;
	private RpcConnection connection;
	private RpcRequest request;
	private RpcResponse response;
	private Stopwatch watch;

	public RpcServerTransaction(RpcConnection connection, RpcRequest request) {
		this.connection = connection;
		this.request = request;

		TraceContext.Clear();
		String traceToken = request.getExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, String.class);
		if (traceToken != null) {
			TraceContext.Apply(traceToken);
			LOGGER.info("RPC CALL - " + traceToken);
		}
	}

	public void setMethodCache(RpcServerMethodCache cache) {
		this.methodCache = cache;
		beforeInvoke();
	}

	public RpcServerMethodCache getMethodCache() {
		return this.methodCache;
	}

	public RpcRequest getRequest() {
		return request;
	}

	public RpcResponse getResponse() {
		return response;
	}

	public RpcConnection getConnection() {
		return connection;
	}

	boolean isNegociating() {
		return negociating;
	}

	void setNegociating(boolean negociating) {
		this.negociating = negociating;
	}


	public void setResponse(RpcResponse response) {
		this.response = response;

		//response 设置扩展字段会导致client无法解包出错，
		//client端根据transaction中的request设置TraceContext
		//TraceContext tc = TraceContext.GetContext();
		//if (tc != null)
		//{
		//	ProtoString pb = new ProtoString(tc.toString());
		//	RpcBody rbTc = new RpcBody(pb, false);
		//	response.putExtension(TraceContext.EXTENSION_CONTEXT_TRACECTOKEN, rbTc);
		//}
		if (response.getReturnCode() == RpcReturnCode.OK) {
			if (methodCache.getResultsCodec() != null) {
				response.setBodyCodec(methodCache.getResultsCodec());
			} else {
				response.setCodecName(getRequest().getHeader().getCodecName());
			}
			watch.end();
		} else {
			RpcBody body = response.getBody();
			String code = response.getReturnCode().toString();
			if (body != null) {
				watch.fail(code + " " + body.decodeText());
			} else {
				watch.fail(code);
			}
		}
		try {
			response.getHeader().setSequence(request.getHeader().getSequence());
			connection.sendResponse(this, response);
		} catch (IOException ex) {
			LOGGER.error("send response failed", ex);
			throw new RuntimeException("send response failed", ex);
		}

		afterInvoke();
	}

	public void beforeInvoke() {
		watch = methodCache.getCounter().begin();

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("invoke {}", methodCache.toString());
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
	}

	private void afterInvoke() {
//		if (LOGGER.isInfoEnabled()) {
//		String msg = String.format("<%s.%s> on %s\r\n", 
//				request.getToService(), 
//				request.getToMethod(),
//				channel.getEndpoint());
//		LOGGER.info(msg);
//		if (LOGGER.isDebugEnabled()) {
//			
//			String abody;
//			String rbody;
//			if (request.getBody() != null) {
//				abody = ObjectDumper.dumpString(request.getBody().getValue(), "Args");
//			} else {
//				abody = "null";
//			}
//		
//			if (response.getReturnCode() == RpcReturnCode.OK) {
//				if (response.getBody() != null) {
//					rbody = ObjectDumper.dumpString(response.getBody().getValue(), "Results");
//				} else {
//					rbody = "Results = null";
//				}
//			} else {
//				if (response.getBody() != null) {
//					rbody = String.format("%s:%s", 
//							response.getReturnCode(),
//							response.getBody().getValue());
//				} else {
//					rbody = response.getReturnCode().toString();
//				}
//			}
//			LOGGER.debug("response debug info :\n {} \n {}", abody, rbody);
//		}
//	}
//	
	}

	public String getCodecName() {
		return getRequest().getHeader().getCodecName();
	}
}
