package org.helium.exception;

public class HeliumException extends Exception{
	private int code;
	public HeliumException(int code, String message) {
		super(message);
		this.code = code;
	}
	public String toString(){
		String s = getClass().getName();
		String message = getLocalizedMessage();
		String desc = (message != null) ? (s + ": " + message) : s;
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("code:").append(code);
		stringBuilder.append(",message:").append(message);
		return stringBuilder.toString();
	}
}
