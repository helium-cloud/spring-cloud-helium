/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-3-23
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.superpojo.utils;

/**
 * <b>描述: </b>依托泛型实现的用于输出任意类型的参数
 * <p>
 * <b>功能: </b>用于输出类型的参数
 * <p>
 * <b>用法: </b>正常类型使用
 * <p>
 *
 * @param <E>
 * @author 高磊 gaolei@feinno.com
 */
public class Outter<E> {
	private E value;

	public Outter() {
	}

	public Outter(E value) {
		this.value = value;
	}

	public E value() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;
	}
}
