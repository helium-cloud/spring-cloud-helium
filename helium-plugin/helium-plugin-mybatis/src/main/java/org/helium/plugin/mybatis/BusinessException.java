package org.helium.plugin.mybatis;

import java.util.Objects;

/**
 * 用于前端提示信息异常
 */
public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BusinessException(String message) {
		super(message);
	}

	public BusinessException(Throwable e) {
		super(e);
	}

	public BusinessException(String message, Throwable e) {
		super(message, e);
	}

	public BusinessException(String message, Object... args) {
		super(prepareMessage(message, args));
	}

	private static String prepareMessage(String message, Object... args) {
		if (Objects.isNull(args) || args.length == 0) {
			return message;
		}
		int i = 0;
		while (message.contains("{}") && i < args.length) {
			message = message.replaceFirst("\\{\\}", String.valueOf(args[i]));
			i++;
		}
		return message;
	}

}
