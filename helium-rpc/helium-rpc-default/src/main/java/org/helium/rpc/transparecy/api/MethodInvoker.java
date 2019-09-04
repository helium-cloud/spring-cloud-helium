package org.helium.rpc.transparecy.api;

import java.util.concurrent.Executor;

/**
 * 方法调用器，该接口用来实现对实际业务方法的调用，为保证性能，该接口的实现应被缓存
 * <p>
 * Created by Coral on 2015/5/12.
 */
public interface MethodInvoker {

	/**
	 * 获取线程池
	 *
	 * @return 线程池
	 */
	Executor getExecutor();

	/**
	 * 设置线程池
	 *
	 * @param executor 线程池
	 */
	void setExecutor(Executor executor);

	/**
	 * 执行调用
	 *
	 * @param ctx RPC 调用服务上下文
	 */
	void execute(RpcServerInvokeContext ctx);

	/**
	 * 获取RPC方法调用的参数类型数组
	 *
	 * @return 参数类型数组
	 */
	Class<?>[] getParameterTypes();

}
