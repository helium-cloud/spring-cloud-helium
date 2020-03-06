package org.helium.test.rpc.demo;

import org.helium.rpc.client.RpcProxyFactory;

public class RpcDemoClient {

    public static void main(String[] args) {
		DemoService proxy = RpcProxyFactory.getTransparentProxy("tcp://127.0.0.1:80/org.apache.dubbo.demo.DemoService", DemoService.class);
		for (int i =0; i < 10; i++) {
			String helloResult = proxy.sayHello("");
			System.out.println(helloResult);
		}

    }

}
