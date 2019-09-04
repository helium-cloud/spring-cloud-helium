/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2012-6-4
 * 
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package test.org.helium.rpc.sample;

import org.helium.rpc.channel.RpcServerChannel;
import org.helium.rpc.channel.inproc.RpcInprocServerChannel;
import org.helium.rpc.channel.tcp.RpcTcpServerChannel;
import org.helium.rpc.server.RpcServiceBootstrap;
import org.helium.threading.ExecutorFactory;

/**
 * {在这里补充类的功能说明}
 *
 * Created by Coral
 */
public class RpcSampleServiceStarter{

    public static void main(String[] args) throws Exception {
    	int port= 7001;
    	String serviceName = "RpcSampleService";
		boolean isLinux = System.getProperty("os.name").toLowerCase().contains("linux");
		RpcServerChannel channel = RpcServiceBootstrap.INSTANCE.getServerChannel("tcp", Integer.toString(port));
		if (channel == null) {
			channel = new RpcTcpServerChannel(port);    // 如何保证端口不重复是个问题
			RpcServiceBootstrap.INSTANCE.registerChannel(channel);
			RpcSampleService rpcSampleService = new RpcSampleServiceImpl();
			RpcServiceBootstrap.INSTANCE.registerTransparentService(serviceName, rpcSampleService, ExecutorFactory.newFixedExecutor(serviceName, 10, 1000), RpcSampleService.class);
		}

		if (RpcServiceBootstrap.INSTANCE.getServerChannel("inproc", "") == null) {
			RpcServiceBootstrap.INSTANCE.registerChannel(RpcInprocServerChannel.INSTANCE);
		}


		RpcServiceBootstrap.INSTANCE.start();

        while (true) {
            Thread.sleep(1000);
        }
    }

}


