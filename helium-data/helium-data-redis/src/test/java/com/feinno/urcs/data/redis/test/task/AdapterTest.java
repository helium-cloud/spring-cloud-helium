//package com.feinno.urcs.data.redis.test.task;
//
//
//import org.helium.rpc.RpcEndpointFactory;
//import org.helium.rpc.client.RpcProxyFactory;
//
//public class AdapterTest {
//    static AdapterService adapterService = null;
//
//    public static void main(String[] args) {
//        init();
//        for (int i = 0; i < 5; i ++) {
//            Thread thread = new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = 0; i < 100000; i++) {
//                        test( i);
//                    }
//                }
//            });
//            thread.start();
//        }
//
//    }
//
//    public static void init() {
//		adapterService = RpcProxyFactory.getTransparentProxy("simple.AdapterService",
//				AdapterService.class, () -> RpcEndpointFactory.parse("tcp://10.10.12.75:7024;protocol=rpc"));
//    }
//
//    public static void test(int i) {
//		AdapterTaskArgs messageArgs = new AdapterTaskArgs();
//		messageArgs.setMobile("13601030000" + i);
//		messageArgs.setType("cloud" + i);
//		messageArgs.setPriority(i);
//       	adapterService.adapter(messageArgs);
//    }
//}
