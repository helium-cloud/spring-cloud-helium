package org.helium.util;

/**
 * Created by Coral on 10/7/15.
 */
public class ErrorListException extends RuntimeException {
	private ErrorList errors;

	public ErrorListException(String msg, ErrorList errors) {
		super(msg);
		this.errors = errors;
	}

	public ErrorList getErrors() {
		return this.errors;
	}
}
