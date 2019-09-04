package com.feinno.superpojo;

/**
 * <b>描述: </b>对Java原生数据类型的序列化提供支持的{@link ProtoEntity}类的子类
 * <p>
 * <b>功能: </b>在这个类的帮助下，可以使Java原生数据类型(String、List、Map、Date等)能够直接序列化，
 * 而无需外层再包装上ProtoEntity
 * <p>
 * <b>用法：</b>无需序列化调用者关注此类的具体用法，它是由序列化组件在序列化原生类型时自动生成该类的子类及调用，因此对外部是不可见的
 * 
 * @author Lv.Mingwei
 * 
 */
public abstract class NativeSuperPojo<T> extends SuperPojo {

	/**
	 * 或得一个数据
	 * 
	 * @return
	 */
	public abstract T getData();

	/**
	 * 设置一个数据
	 * 
	 * @param t
	 */
	public abstract void setData(T t);

	/**
	 * 返回一个新的实例
	 * 
	 * @return
	 */
	public abstract NativeSuperPojo<T> newInstance();
}
