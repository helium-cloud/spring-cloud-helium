/*
 * FAE, Feinno App Engine
 *  
 * Create by lichunlei 2010-11-26
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package zconfig.configuration.args;

/**
 * 当声明一个ConfigTable的时候, 用
 * 
 * @author lichunlei
 */
public abstract class ConfigTableItem
{
	/**
	 * 
	 * 当需要在获取玩配置后进行Item的某些后置操作时, 重载这个方法
	 */
	public void afterLoad() throws Exception
	{
		//
		// Do Nothing in BaseClass
	}
}
