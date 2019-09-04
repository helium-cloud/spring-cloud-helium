package org.helium.sample.future;

import org.helium.rpc.RpcEndpointFactory;
import org.helium.rpc.client.RpcProxyFactory;
import org.helium.threading.Future;
import org.helium.threading.FutureListener;
import org.helium.util.Result;
import org.helium.sample.future.common.MessageRequest;
import org.helium.sample.future.common.MessageResponse;

public class FutureTest {
    static FutureService futureService = null;

    public static void main(String[] args) {
        init();
		for (int i =0; i < 5; i++) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int j = 0; j < 1000000; j++) {
						//testFuture(j);
//						try {
//							Thread.sleep(1);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
						//testFutureInner();
						testFutureNormal();
					}
				}
			});
			thread.start();
		}

    }

    public static void init() {
		futureService = RpcProxyFactory.getTransparentProxy("simple.FutureService",
				FutureService.class, () -> RpcEndpointFactory.parse("tcp://10.10.12.78:7024;protocol=rpc"));
    }

    //2w
    public static void testFuture(int i) {

		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setMobile("xxxx");
		messageRequest.setType("cloud" + i);
		messageRequest.setPriority(-1);
	 	Future<MessageResponse> messageResponse = futureService.adapterFuture(messageRequest);
	 	messageResponse.addListener(new FutureListener<MessageResponse>() {
			@Override
			public void run(Result<MessageResponse> result) {

			}
		});
    }


	public static void testFutureInner() {

		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setMobile("13601030000");
		messageRequest.setType("cloud");
		messageRequest.setPriority(-1);
		futureService.adapterInnerFuture(messageRequest);
	}


	public static void testFutureNormal() {

		MessageRequest messageRequest = new MessageRequest();
		messageRequest.setMobile("13601030000");
		messageRequest.setType("cloud");
		messageRequest.setPriority(-1);
		futureService.adapterNormal(messageRequest);
	}
}
