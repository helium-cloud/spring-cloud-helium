/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 19, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.rpc.server.RpcServerContext;
import org.helium.serialization.Codec;
import org.helium.util.Event;

import java.util.concurrent.Executor;

/**
 * Rpc服务器端方法处理类, 唯一的绑定在service.method上
 * <p>
 * Created by Coral
 */
public abstract class RpcServerMethodHandler {
	private String service;
	private String method;
	private Codec argsCodec;
	private Codec resultsCodec;
	private Executor executor;
	private Event<RpcServerContext> beforeEvent;

	// private Event<RpcServerContext> afterEvent; TOOD: after不太好实现

	public RpcServerMethodHandler(String service, String method) {
		this.service = service;
		this.method = method;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	public Executor getExecutor() {
		return executor;
	}

	public String getService() {
		return service;
	}

	public String getMethod() {
		return method;
	}

	public Codec getArgsCodec() {
		return argsCodec;
	}

	public void setArgsCodec(Codec codec) {
		this.argsCodec = codec;
	}

	public Codec getResultsCodec() {
		return resultsCodec;
	}

	public void setResultsCodec(Codec codec) {
		this.resultsCodec = codec;
	}

	public Event<RpcServerContext> getBeforeEvent() {
		if (beforeEvent == null) {
			beforeEvent = new Event<RpcServerContext>(this);
		}
		return beforeEvent;
	}

//	TODO: 想想after放在哪个层面去实现
//	public Event<RpcServerContext> getAfterEvent()
//	{
//		if (afterEvent == null) {
//			afterEvent = new Event<RpcServerContext>(this);
//		}
//		return afterEvent;
//	}

	public void execute(final RpcServerContext ctx) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				if (beforeEvent != null) {
					beforeEvent.fireEvent(ctx);
				}
				RpcServerMethodHandler.this.run(ctx);
			}
		});
	}

	/**
	 * 子类的实现: 调用最终方法
	 *
	 * @param ctx
	 */
	public abstract void run(RpcServerContext ctx);
}
 