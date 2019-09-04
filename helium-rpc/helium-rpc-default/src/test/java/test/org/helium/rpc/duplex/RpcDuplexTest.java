/*
 * FAE, Feinno App Engine
 *  
 * Create by Coral 2011-2-17
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package test.org.helium.rpc.duplex;

import org.helium.rpc.channel.tcp.RpcTcpEndpoint;
import org.helium.rpc.channel.tcp.RpcTcpServerChannel;
import org.helium.rpc.duplex.RpcDuplexClient;
import org.helium.rpc.duplex.RpcDuplexServer;
import org.helium.threading.ExecutorFactory;
import org.helium.threading.Future;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TestMockClient
 *
 * @author Coral
 */
public class RpcDuplexTest {
    private static final int SERVER_PORT = 7037;
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcDuplexTest.class);

    @Before
    public void testBefore() throws Exception {
        // start service
        RpcTcpServerChannel channel = new RpcTcpServerChannel(SERVER_PORT);
        RpcDuplexServer server = new RpcDuplexServer(channel);
        server.setExecutor(ExecutorFactory.newFixedExecutor("Test", 10, 1024));
        server.registerService(RpcSampleAgentServiceImpl.INSTANCE);
        channel.start();
    }

    @Test
    public void testRegisterAndCallback() throws Exception {
        RpcDuplexClient client = new RpcDuplexClient(RpcTcpEndpoint.parse("tcp://127.0.0.1:" + SERVER_PORT));
        client.setExecutor(ExecutorFactory.getExecutor("Test"));
        final Future<String> future = new Future<String>();
        client.registerCallbackService(new RpcSampleAgentCallbackService() {
            @Override
            public String test(String args) {
                future.complete(args);
                return args;
            }
        });
        RpcSampleAgentService service = client.getService(RpcSampleAgentService.class);

        LOGGER.info("connection sync");
        client.connectSync();
        LOGGER.info("connected");
        service.register("CLIENT1");
        service.testCallback();

        String result = future.getValue();
        Assert.assertEquals("CLIENT1", result);
    }
}
