package org.helium.test.rpc.future;

import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcService;

@RpcService("FutureService")
public interface IFutureService {

    @RpcMethod("method1")
    String method1(String args);

    @RpcMethod("exceptionMethod")
    String[] exceptionMethod(String[] args);

    @RpcMethod("timeOutMethod")
    String[] timeOutMethod(String[] args);

}
