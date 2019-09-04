package org.helium.sample.adapter.adapter;

import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.sample.adapter.common.MessageArgs;

public class AdapterTest {
    static AdapterService adapterService = null;

    public static void main(String[] args) {
        init();
        test();
    }

    public static void init() {
		adapterService = RpcProxyFactory.getTransparentProxy("simple.AdapterService1",
				AdapterService.class, () -> RpcEndpointFactory.parse("tcp://10.10.12.75:7024;protocol=rpc"));
    }

    public static void test() {
		MessageArgs messageArgs = new MessageArgs();
		messageArgs.setMobile("13601030000");
		messageArgs.setType("cloud");
		messageArgs.setPriority(-1);
       	adapterService.adapter(messageArgs);
    }
}
