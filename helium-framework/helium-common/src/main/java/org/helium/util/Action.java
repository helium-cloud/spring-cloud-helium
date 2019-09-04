/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2010-11-23
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 
 * <b>描述: </b>通用回调代理类
 * <p>
 * <b>功能: </b>用于回调的通用接口类
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * class TestCallback {
 * 	static void test(String str,Action&lt;String&gt; action){
 *   `		str = &quot;say: &quot;+str;//方法中进行信息加工
 *   		action.run(str);//将加工后的信息或结果传递给回调方法
 *   	}
 * 
 * 	public static void main(String[] args) {
 * 		TestCallback.test(&quot;Feinno&quot;, new Action() {
 * 			public void run(String str) {
 * 				System.out.println(str);
 * 			}
 * 		});
 * 	}
 * }
 * </pre>
 * <p>
 * 
 * Created by Coral
 * 
 * @param <T>
 */
public interface Action<T> {
	void run(T a);
}
