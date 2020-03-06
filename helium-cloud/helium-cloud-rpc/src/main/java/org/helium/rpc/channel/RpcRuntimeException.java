/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 15, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

/**
 * 用于辅助捕获Rpc收发过程中的内部错误，不直接抛给调用端
 * <p>
 * Created by Coral
 */
public class RpcRuntimeException extends RuntimeException {
	private RpcReturnCode code;

	public RpcReturnCode getReturnCode() {
		return this.code;
	}

	public RpcRuntimeException(RpcReturnCode code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public RpcRuntimeException(RpcReturnCode code, String message) {
		super(message, null);
		this.code = code;
	}

	public RpcRuntimeException(Throwable cause) {
		super("server error", cause);
		this.code = RpcReturnCode.SERVER_ERROR;
	}

	public RpcRuntimeException(String message) {
		super(message, null);
		this.code = RpcReturnCode.SERVER_ERROR;
	}

	private static final long serialVersionUID = -3643116975428637829L;
}
