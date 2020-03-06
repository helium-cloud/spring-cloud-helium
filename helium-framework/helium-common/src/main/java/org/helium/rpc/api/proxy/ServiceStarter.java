package org.helium.rpc.api.proxy;

import java.util.concurrent.Executor;

/**
 * Rpc服务注册及引导启动类
 * <pre>
 * <code>
 *
 * </code>
 * </pre>
 * <p>
 * Created by Coral
 */
public interface ServiceStarter {

	ServiceStarter getInstance();

	/**
	 * 设置线程池
	 *
	 * @param executor
	 */
	void setExecutor(Executor executor);
	/**
	 * 注册Rpc服务
	 *
	 * @param service
	 */
	void registerService(Object service);

	/**
	 * 注册透明Rpc服务
	 *
	 * @param serviceName
	 * @param serviceObject
	 * @param serviceInterfaces
	 */
	void registerTransparentService(String serviceName, Object serviceObject, Executor executor, Class<?>... serviceInterfaces);



	boolean unregisterService(String serviceName);

	/**
	 * 获取运行时的全部服务名称
	 *
	 * @return
	 */
	String[] getRunningService();

	/**
	 * 启动所有Channel
	 *
	 * @throws Exception
	 */
	void start() throws Exception;

	/**
	 * 停止所有Channel
	 *
	 * @throws Exception
	 */
	void stop() throws Exception;

}
