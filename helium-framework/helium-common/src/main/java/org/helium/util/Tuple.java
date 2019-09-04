/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2010-12-10
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 
 * <b>描述: </b>简单的包含两个参数的泛型模板类，不能用于序列化
 * <p>
 * <b>功能: </b>二元组
 * <p>
 * <b>用法: </b>二元组的正常用法
 * <p>
 *
 * Created by Coral
 *
 * @param <T1>
 * @param <T2>
 */
public class Tuple<T1, T2>
{
	private T1 v1;
	private T2 v2;
	
	/**
	 * @return the v1
	 */
	public T1 getV1()
	{
		return v1;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * {在这里补充功能说明}
	 * @return
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((v1 == null) ? 0 : v1.hashCode());
		result = prime * result + ((v2 == null) ? 0 : v2.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * {在这里补充功能说明}
	 * @param obj
	 * @return
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tuple<?, ?> other = (Tuple<?, ?>) obj;
		if (v1 == null) {
			if (other.v1 != null)
				return false;
		} else if (!v1.equals(other.v1))
			return false;
		if (v2 == null) {
			if (other.v2 != null)
				return false;
		} else if (!v2.equals(other.v2))
			return false;
		return true;
	}

	/**
	 * @param v1 the v1 to set
	 */
	public void setV1(T1 v1)
	{
		this.v1 = v1;
	}

	/**
	 * @return the v2
	 */
	public T2 getV2()
	{
		return v2;
	}

	/**
	 * @param v2 the v2 to set
	 */
	public void setV2(T2 v2)
	{
		this.v2 = v2;
	}

	public Tuple(T1 v1, T2 v2) 
	{
		this.v1 = v1;
		this.v2 = v2;
	}
}
