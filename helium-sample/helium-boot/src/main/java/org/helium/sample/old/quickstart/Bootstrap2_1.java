package org.helium.sample.bootstrap.quickstart;

import org.helium.rpc.client.RpcProxyFactory;
import org.helium.framework.spi.Bootstrap;

/**
 * Quickstart教程启动器，参照2.1章节
 * Created by Coral on 6/15/17.
 */
public class Bootstrap2_1 {
	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-sample");
		Bootstrap.INSTANCE.addPath("helium-sample/build/resources/main/config"); // 将config目录加载到配置路径中
		Bootstrap.INSTANCE.initialize("bootstrap-2-1.xml", true, false);
		testRpc();
	    Bootstrap.INSTANCE.run();
	}
	
	public static void testRpc() throws Exception {
		SampleService service = RpcProxyFactory.getTransparentProxy("tcp://127.0.0.1:7023/quickstart.SampleService", SampleService.class);
		SampleUser user = service.getUser(1);
		System.out.println(user.toJsonObject().toString());
	}
}
