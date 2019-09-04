package com.feinno.superpojo.generator;

/**
 * 
 * <b>描述: </b>序列化辅助类代码生成时异常,当代码生成过程中出现问题时，此异常被抛出
 * <p>
 * <b>功能: </b>用于标识出序列化辅助类代码生成时的异常
 * <p>
 * <b>用法: </b>外部调用者在接受到此类异常时，需要及时处理，因为此类异常代表着一个类型的Java类无法被成功的序列化或反序列化
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class CodeGeneratorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7681643427601666941L;

	/**
	 * 默认构造方法
	 */
	public CodeGeneratorException() {

	}

	/**
	 * 构造方法
	 * 
	 * @param message
	 */
	public CodeGeneratorException(String message) {
		super(message);
	}

	/**
	 * 构造方法
	 * 
	 * @param message
	 * @param cause
	 */
	public CodeGeneratorException(String message, Throwable cause) {
		super(message, cause);
	}
}
