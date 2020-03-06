package org.helium.rpc.api.proxy;

/**
 * 客户端使用Rpc的工厂类, 全静态方法
 *
 * Created by Coral
 */
public interface ProxyFactory {

	ProxyFactory getInstance();

	/**
	 * getTransparentProxy
	 * @param url
	 * @param serviceInterface
	 * @param <E>
	 * @return
	 */
	<E> E getTransparentProxy(String url, Class<E> serviceInterface);

	/**
	 * 获取透明Rpc代理
	 * helium://group.MessageReceiver
	 *
	 * @param url
	 * @param <E>
	 * @return
	 */
	<E> E getTransparentProxy(String url, Class<E> serviceInterface, int timeout);
	
}
