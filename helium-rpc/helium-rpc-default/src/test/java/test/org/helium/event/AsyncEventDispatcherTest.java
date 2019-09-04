package test.org.helium.event;

import org.helium.rpc.event.AbstractUpcEventListener;
import org.helium.rpc.event.UpcEvent;
import org.helium.rpc.event.UpcEventDispatcher;
import org.helium.threading.ExecutorFactory;

import java.util.Random;
import java.util.concurrent.Executor;

/**
 * @author coral
 * @version 创建时间：2015年1月9日 类说明
 */
public class AsyncEventDispatcherTest {
	private static int fast = 100;

	public static class SimpleEvent implements UpcEvent {
		public SimpleEvent(String key, long value) {
			this.key = key;
			this.value = value;
		}

		public String key;
		public long value;

		@Override
		public String getEventName() {
			return key;
		}
	}

	public static String ran() {
		return Integer.toString(new Random().nextInt(fast));
	}

	public static void main(String[] args) {
		Executor puExecutor = ExecutorFactory.newFixedExecutor("pu", fast, 100);
		Executor suExecutor = ExecutorFactory.newFixedExecutor("su", fast, 100);
		final UpcEventDispatcher<SimpleEvent> dispatcher = new UpcEventDispatcher<SimpleEvent>("Test", 10, 1000);
		dispatcher.start();

		for (int i = 0; i < fast; i++) {
			final int j = i;
			suExecutor.execute(new Runnable() {

				@Override
				public void run() {
					int i = 10000;
					while (i-- > 0) {
						dispatcher.addEventListener(Integer.toString(j), new AbstractUpcEventListener<SimpleEvent>() {

							@Override
							public void execute(SimpleEvent e) {
								System.out.println((System.nanoTime() - e.value)/1000);
							}
						});

						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}

		for (int i = 0; i < 10; i++) {
			puExecutor.execute(new Runnable() {

				@Override
				public void run() {
					while (true) {
						String ran = ran();
						dispatcher.fire(new SimpleEvent(ran, System.nanoTime()));
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			});
		}

	}
}
