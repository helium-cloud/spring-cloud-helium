package org.helium.rpc.transparecy.api;

import org.helium.rpc.channel.RpcEndpoint;

/**
 * RPC 服务中心
 * <p>
 * 该接口提供服务注册和获取远程RPC服务调用映射的能力,作为RPC的核心操作入口提供给用户
 * <p>
 * Created by Coral on 2015/5/12.
 */
public interface RpcServiceCenter {

	/**
	 * 注册RPC服务
	 *
	 * @param serviceBean 提供服务处理能力的javabean实例
	 * @param interfaces  一组供发布的方法接口，ServiceBean必须实现这些接口，如果传 null 则发布serviceBean实现了的所有接口
	 */
	void registerService(Object serviceBean, Class<?>[] interfaces);

	/**
	 * 注销RPC服务
	 *
	 * @param serviceBean 要注销的ServiceBean
	 */
	void unregisterService(Object serviceBean);

	/**
	 * 注销RPC服务
	 *
	 * @param serviceBeanClass 要注销的ServiceBean的类
	 */
	void unregisterService(Class<?> serviceBeanClass);

	/**
	 * 获取一个RPC服务的调用映射
	 *
	 * @param type     映射的接口
	 * @param endpoint RPC服务的ep
	 * @param <T>      映射的接口类型
	 * @return 映射实例
	 */
	<T> T getServiceMapper(Class<T> type, RpcEndpoint endpoint);

}
