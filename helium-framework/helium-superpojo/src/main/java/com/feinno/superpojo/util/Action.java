package com.feinno.superpojo.util;

/**
 * <b>描述: </b>序列化组件中使用，具有返回值的回调类，用以解决生成的序列化辅助代码中代码段的作用域问题.
 * <p>
 * <b>功能: </b>序列化组件中使用，具有返回值的回调类，用以解决生成的序列化辅助代码中代码段的作用域问题.
 * <p>
 * <b>用法: </b>可正常将其视为具有返回值的通用回调类
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 * @param <T>
 */
public abstract class Action<T> {

	public abstract T run();
}
