/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei Jun 23, 2012
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package test.org.helium.rpc.duplex;

import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcService;

/**
 * Created by Coral
 */
@RpcService("RpcSampleAgentCallbackService")
public interface RpcSampleAgentCallbackService {
    
    @RpcMethod("Test")
    String test(String args);
}
