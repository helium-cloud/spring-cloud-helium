/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-5-29
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package test.org.helium.rpc.duplex;

import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcService;

/**
 * {在这里补充类的功能说明}
 *
 * Created by Coral
 */
@RpcService("RpcSampleAgentService")
public interface RpcSampleAgentService {

    @RpcMethod("Register")
    void register(String string);

    @RpcMethod("TestCallback")
    void testCallback();
}
