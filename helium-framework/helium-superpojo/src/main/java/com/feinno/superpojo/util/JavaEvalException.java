package com.feinno.superpojo.util;

/**
 * 
 * <b>描述: </b>动态编译{@link JavaEval}时产生的异常
 * <p>
 * <b>功能: </b>用以描述动态编译时产生的异常
 * <p>
 * <b>用法: </b>正常异常的捕获方法
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class JavaEvalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6123559450199713071L;

	public JavaEvalException() {

	}

	public JavaEvalException(String msg) {
		super(msg);
	}
}
