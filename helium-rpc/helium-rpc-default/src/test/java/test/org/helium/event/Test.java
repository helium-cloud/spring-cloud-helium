package test.org.helium.event;

import org.helium.rpc.event.UpcEvent;
import org.helium.rpc.event.UpcEventDispatcher;
import org.helium.rpc.event.UpcEventListener;
import org.helium.rpc.event.UpcEventListenerFuture;

import java.io.IOException;

/**
 * @author coral
 * @version 创建时间：2015年1月27日 类说明
 */
public class Test implements UpcEvent {

	private String name;

	public Test(String t) {
		name = t;
	}

	@Override
	public String getEventName() {
		return name;
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		UpcEventDispatcher<Test> upc = new UpcEventDispatcher<Test>("Test",10, 100);
		upc.setExpireTime(5);
		upc.start();

		UpcEventListenerFuture<Test> future = upc.addEventListener("1", new UpcEventListener<Test>() {

			@Override
			public void onTimeout() {
				System.out.println("onTimeout");
			}

			@Override
			public void onCancel() {
				System.out.println("onCancel");
			}

			@Override
			public void execute(Test e) {
				System.out.println("execute");
			}
		});
		//1.
		// Thread.sleep(10000);
		//2.
		//Thread.sleep(1000);
		//3.
		Thread.sleep(1000);
		future.cancel();
//		Thread.sleep(10000);
//		
//		feinno.fire(new Test("1"));
//		System.out.println("is Send");

		System.in.read();
	}

}
