package test.org.helium.rpc.future;

import org.helium.rpc.channel.tcp.RpcTcpServerChannel;
import org.helium.rpc.server.RpcServiceBootstrap;
import org.helium.threading.ExecutorFactory;

public class FutureServer {

    public FutureServer() throws Exception {
        // 注册服务端通道
        RpcServiceBootstrap.INSTANCE.registerChannel(new RpcTcpServerChannel(8001));
        RpcServiceBootstrap.INSTANCE.registerService(new FutureService());
        RpcServiceBootstrap.INSTANCE.setExecutor(ExecutorFactory.newFixedExecutor("mock", 32, 32 * 1000));

//        SimpleLoggerFactory.INSTANCE.setInfoEnable(false);
        System.out.println("Test server started. [V39]");
        RpcServiceBootstrap.INSTANCE.start();
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
		new FutureServer();
    }

}
