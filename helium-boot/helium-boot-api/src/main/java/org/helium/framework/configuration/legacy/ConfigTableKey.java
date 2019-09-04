/*
 * FAE, Feinno App Engine
 *  
 * Create by lichunlei 2010-11-30
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.framework.configuration.legacy;

/**
 * 当ConfigTable使用混合主键是必须使用本类
 * 
 * @author lichunlei
 */
public abstract class ConfigTableKey 
{
	/*
	 * @see java.lang.Object#hashCode()
	 */
	/**
	 * 要求子类必须实现这个方法
	 * @return
	 */
	@Override
	public abstract int hashCode();
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	/**
	 * 要求子类必须实现这个方法
	 * @param obj
	 * @return
	 */
	@Override
	public abstract boolean equals(Object obj);}
