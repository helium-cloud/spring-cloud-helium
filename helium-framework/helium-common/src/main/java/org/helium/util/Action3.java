/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2010-11-24
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 
 * <b>描述: </b>具有三参的通用回调代理类
 * <p>
 * <b>功能: </b>具有三参的用于回调的通用接口类
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * class TestCallback {
 * 	static void test(int m,int n,int o,Action&lt;Integer&gt; action){
 *   `		int sum = m*n+o; //方法中进行信息加工
 *   		action.run(sum); //将加工后的信息或结果传递给回调方法
 *   	}
 * 
 * 	public static void main(String[] args) {
 * 		TestCallback.test(10, 20,30, new Action&lt;Integer&gt;() {
 * 			public void run(Integer sum) {
 * 				System.out.println(sum);
 * 			}
 * 		});
 * 	}
 * }
 * </pre>
 * <p>
 * 
 * Created by Coral
 * 
 * @param <T1>
 * @param <T2>
 * @param <T3>
 */
public interface Action3<T1, T2, T3>
{
	void run(T1 v1, T2 v2, T3 v3);
}
