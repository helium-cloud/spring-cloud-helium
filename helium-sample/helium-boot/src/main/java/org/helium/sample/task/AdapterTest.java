package org.helium.sample.task;

import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.sample.adapter.common.MessageArgs;

public class AdapterTest {
    static AdapterService adapterService = null;

    public static void main(String[] args) {
		init();
		for (int i = 0; i < 30; i ++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < 10; j++) {
						test(j );
					}
				}
			});
			thread.start();
		}
    }

    public static void init() {
		adapterService = RpcProxyFactory.getTransparentProxy("simple.AdapterService",
				AdapterService.class, () -> RpcEndpointFactory.parse("tcp://10.10.12.75:7024;protocol=rpc"));
    }

    public static void test(int i) {
		MessageArgs messageArgs = new MessageArgs();
		messageArgs.setMobile("13601030000" + i);
		messageArgs.setType("cloud");
		messageArgs.setPriority(-1);
       	adapterService.adapter(messageArgs);
    }
}
