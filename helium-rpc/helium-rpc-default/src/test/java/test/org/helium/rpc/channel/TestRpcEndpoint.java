package test.org.helium.rpc.channel;

import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.channel.tcp.RpcTcpEndpoint;
import org.junit.Assert;
import org.junit.Test;

public class TestRpcEndpoint {
    @Test
    public void testRpcTcpEnpdoint() {
        RpcTcpEndpoint ep;
        ep = (RpcTcpEndpoint) RpcEndpointFactory.parse("tcp://192.168.1.1:1234");
        Assert.assertEquals("/192.168.1.1", ep.getAddress().getAddress().toString());
        Assert.assertEquals(1234, ep.getAddress().getPort());

        ep = (RpcTcpEndpoint) RpcEndpointFactory.parse("tcp://192.168.1.1:1234;");
        Assert.assertEquals("/192.168.1.1", ep.getAddress().getAddress().toString());
        Assert.assertEquals(1234, ep.getAddress().getPort());

        ep = (RpcTcpEndpoint) RpcEndpointFactory.parse("tcp://192.168.1.1:1234/");
        Assert.assertEquals("/192.168.1.1", ep.getAddress().getAddress().toString());
        Assert.assertEquals(1234, ep.getAddress().getPort());

    }

    @Test
    public void testRpcTcpEnpdoint2() {
        RpcTcpEndpoint ep;
        ep = RpcTcpEndpoint.parse("tcp://192.168.1.1:1234");
        Assert.assertEquals("/192.168.1.1", ep.getAddress().getAddress().toString());
        Assert.assertEquals(1234, ep.getAddress().getPort());

        ep = RpcTcpEndpoint.parse("tcp://192.168.1.1:1234/");
        Assert.assertEquals("/192.168.1.1", ep.getAddress().getAddress().toString());
        Assert.assertEquals(1234, ep.getAddress().getPort());

        ep = RpcTcpEndpoint.parse("tcp://192.168.1.1:1234;");
        Assert.assertEquals("/192.168.1.1", ep.getAddress().getAddress().toString());
        Assert.assertEquals(1234, ep.getAddress().getPort());

    }
}
