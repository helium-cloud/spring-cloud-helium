package org.helium.util;

/**
 * 
 * 可供包装错误的结果类
 * 
 * Created by Coral
 *
 * @param <V>
 */
public class Result<V> {
	private V value;
	private Exception error;
	
	private Result(V value, Exception e) {
		this.value = value;
		this.error = e;
	}
	
	public V getValue() {
		return this.value;
	}
	
	public V get() throws Exception {
		if (this.error != null) {
			throw error;
		} else {
			return value;
		}
	}
	
	public Exception getError() {
		return this.error;
	}
	
	public boolean isError() {
		return this.error != null;
	}
	
	public static <V> Result<V> create(V value) {
		return new Result<V>(value, null);
	}
	
	public static <V> Result<V> createWithError(Exception e) {
		if (e == null) {
			throw new IllegalArgumentException("createWithError error can't be null");
		}
		return new Result<V>(null, e); 
	}

	@Override
	public String toString() {
		if (error != null) {
			return error.getMessage();
		} else {
			if (value == null) {
				return "null";
			} else {
				return value.toString();
			}
		}
	}
}
