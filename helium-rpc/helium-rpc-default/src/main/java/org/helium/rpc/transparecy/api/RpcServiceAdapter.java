package org.helium.rpc.transparecy.api;

/**
 * RPC服务适配器，每一个对外发布的RPC服务均对应一个该接口的实现
 * <p>
 * Created by Coral on 2015/5/12.
 */
public interface RpcServiceAdapter {

	/**
	 * 获取RPC服务名
	 *
	 * @return RPC服务名
	 */
	String getRpcServiceName();

	/**
	 * 根据一个RPC调用上下文 {@link RpcServerInvokeContext} 获取一个方法调用器 {@link MethodInvoker}，如果没有匹配到任何方法，则返回 null
	 *
	 * @param context RPC调用上下文 {@link RpcServerInvokeContext}
	 * @return 一个 {@link MethodInvoker} 实例或 null
	 */
	MethodInvoker getMethodInvoker(RpcServerInvokeContext context);

	/**
	 * 获取本适配器绑定的ServiceBean（处理实际业务逻辑的 javabean）
	 *
	 * @return ServiceBean
	 */
	Object getServiceBean();
}
