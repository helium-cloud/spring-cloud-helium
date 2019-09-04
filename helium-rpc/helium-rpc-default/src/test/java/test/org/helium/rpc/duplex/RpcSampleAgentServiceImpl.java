package test.org.helium.rpc.duplex;
/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-5-29
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */

import org.helium.rpc.duplex.RpcDuplexClientAgent;
import org.helium.rpc.server.RpcMethod;
import org.helium.rpc.server.RpcServerContext;
import org.helium.rpc.server.RpcServiceBase;

import java.util.Hashtable;
import java.util.Map;

/**
 * {在这里补充类的功能说明}
 *
 * Created by Coral
 */
public class RpcSampleAgentServiceImpl extends RpcServiceBase {
    public static final RpcSampleAgentServiceImpl INSTANCE = new RpcSampleAgentServiceImpl();

    private Map<String, RpcDuplexClientAgent> agents;

    private RpcSampleAgentServiceImpl() {
        super("RpcSampleAgentService");
        agents = new Hashtable<String, RpcDuplexClientAgent>();
    }

    @RpcMethod("Register")
    public void register(RpcServerContext ctx) {
        String args = ctx.getArgs(String.class);
        RpcDuplexClientAgent agent = new RpcDuplexClientAgent(ctx);
        agent.setContext("Name", args);
        agents.put(args, agent);
        ctx.getConnection().setAttachment(agent);
        ctx.end();
    }

    @RpcMethod("TestCallback")
    public void testCallback(RpcServerContext ctx) {
        RpcDuplexClientAgent agent = (RpcDuplexClientAgent) ctx.getConnection().getAttachment();
        RpcSampleAgentCallbackService service = agent.getService(RpcSampleAgentCallbackService.class);
        service.test((String) agent.getContext("Name"));
        ctx.end();
    }
}
