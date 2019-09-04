package org.helium.stack.rpc;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.rpc.client.RpcMethodStub;

/**
 * Created by Coral on 6/15/15.
 */
@FieldLoaderType(loaderType = LegacyRpcClientLoader.class)
public interface LegacyRpcClient {
	/**
	 * 获取调用方法
	 * @param method
	 * @return
	 */
	RpcMethodStub getMethodStub(String method);
}
