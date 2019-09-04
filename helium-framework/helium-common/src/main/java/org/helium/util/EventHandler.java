/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Apr 7, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.util;

/**
 * 事件处理器 
 * 
 * Created by Coral
 */
public interface EventHandler<E>
{
	void run(Object sender, E e);
}
