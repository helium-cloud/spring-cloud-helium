package com.feinno.superpojo;

/**
 * 序列化POJO对象的辅助类的工厂类
 * 
 * @author lvmingwei
 * 
 * @param <T>
 */
public interface BuilderFactory {

	/**
	 * 创建一个序列化辅助类的实例对象
	 * 
	 * @param t
	 * @return
	 */
	public <T extends Object> Builder<T> newBuilder(T t);

}
