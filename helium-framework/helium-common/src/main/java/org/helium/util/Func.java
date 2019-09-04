/*
 * FAE, Feinno App Engine
 *  
 * Create by wanglihui 2010-11-25
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 都Java8了，使用java.lang.function包中的函数式接口声明代替把
 * @see java.util.function.Function
 * @author wanglihui
 * @param <T>
 * @param <E>
 */
@Deprecated
public interface Func<T, E> {
	public E exec(T obj);
}