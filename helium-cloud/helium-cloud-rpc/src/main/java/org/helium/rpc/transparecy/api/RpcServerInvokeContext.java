package org.helium.rpc.transparecy.api;

import org.helium.rpc.channel.RpcReturnCode;
import org.helium.rpc.channel.RpcServerTransaction;

/**
 * RPC服务端调用上下文，每一次RPC调用在服务器端都会生成一个该上下文实例，用来存储和获取调用细节
 * <p>
 * Created by Coral on 2015/5/12.
 */
public interface RpcServerInvokeContext {

	/**
	 * 获取本次调用的参数数组
	 *
	 * @return
	 */
	Object[] getInvokeArgs();

	/**
	 * 获取本次调用的目标RPC方法名
	 *
	 * @return RPC方法名
	 */
	String getToMethod();

	/**
	 * 获取本次调用的目标RPC服务名
	 *
	 * @return RPC服务名
	 */
	String getToService();

	/**
	 * 正常结束调用，并将调用结果返回给客户端
	 *
	 * @param result 调用结果
	 */
	void end(Object result);

	/**
	 * 终止调用，并将错误码和错误内容返回给客户端
	 *
	 * @param returnCode 返回码
	 * @param error      错误对象
	 */
	void end(RpcReturnCode returnCode, Throwable error);

	/**
	 * 获取 RPC Server传输事务对象 {@link RpcServerTransaction}
	 *
	 * @return RPC Server传输事务对象
	 */
	RpcServerTransaction getTx();
}
