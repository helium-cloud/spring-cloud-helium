package test.org.helium.rpc.demo;

import com.alibaba.fastjson.JSONObject;
import org.helium.rpc.client.RpcProxyFactory;
import test.org.helium.rpc.sample.RpcSampleService;
import test.org.helium.rpc.sample.RpcSampleService.HelloArgs;
import test.org.helium.rpc.sample.RpcSampleService.HelloResult;

public class RpcDemoClient {

    public static void main(String[] args) {
		DemoService proxy = RpcProxyFactory.getTransparentProxy("tcp://127.0.0.1:80/org.apache.dubbo.demo.DemoService", DemoService.class);
		for (int i =0; i < 10; i++) {
			String helloResult = proxy.sayHello("");
			System.out.println(helloResult);
		}

    }

}
