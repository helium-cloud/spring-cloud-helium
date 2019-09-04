/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-6-9
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package zconfig.configuration.args;

/**
 * 配置更新的回调类
 * 和Action<E>相比，允许异常抛出
 * 
 * @author 高磊 gaolei@feinno.com
 */
public interface ConfigUpdateAction<E>
{
	void run(E e) throws Exception;
}
