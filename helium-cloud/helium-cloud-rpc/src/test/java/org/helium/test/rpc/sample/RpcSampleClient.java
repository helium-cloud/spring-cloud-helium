package org.helium.test.rpc.sample;

import com.alibaba.fastjson.JSONObject;
import org.helium.rpc.client.RpcProxyFactory;

public class RpcSampleClient {

    public static void main(String[] args) {
		RpcSampleService proxy = RpcProxyFactory.getTransparentProxy("tcp://127.0.0.1:7001/RpcSampleService", RpcSampleService.class);
        RpcSampleService.HelloArgs helloArgs = new RpcSampleService.HelloArgs();
		helloArgs.setStr("hello li");
		helloArgs.setBegin(0);
		helloArgs.setLen(5);
		for (int i =0; i < 10; i++) {
			RpcSampleService.HelloResult helloResult = proxy.hello(helloArgs);
			System.out.println(JSONObject.toJSONString(helloResult));
		}

    }

}
