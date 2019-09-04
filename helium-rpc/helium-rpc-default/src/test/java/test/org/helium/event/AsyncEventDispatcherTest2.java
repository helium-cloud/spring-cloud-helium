package test.org.helium.event;

import org.helium.rpc.event.AbstractUpcEventListener;
import org.helium.rpc.event.UpcEvent;
import org.helium.rpc.event.UpcEventDispatcher;
import org.helium.threading.ExecutorFactory;

import java.util.concurrent.Executor;

/**
 * @author coral
 * @version 创建时间：2015年1月12日 类说明
 */
public class AsyncEventDispatcherTest2 {
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

	public static void main(String[] args) {
		Executor puExecutor = ExecutorFactory.newFixedExecutor("pu", fast, 100);
		final UpcEventDispatcher<SimpleEvent> dispatcher = new UpcEventDispatcher<SimpleEvent>("Test", 100, 10000, true);
		dispatcher.start();

		for (int i = 0; i < 10; i++) {
			int n = 10;
			while (n-- > 0)
				dispatcher.addEventListener(Integer.toString(i), new AbstractUpcEventListener<SimpleEvent>() {

					@Override
					public void execute(SimpleEvent e) {
//						System.out.println((System.nanoTime() - e.value) / 1000000.0);
					}
				});
		}

		for (int i = 0; i < 10; i++) {
			final String string = Integer.toString(i);
			puExecutor.execute(new Runnable() {

				@Override
				public void run() {
					while (true) {
						dispatcher.fire(new SimpleEvent(string, System.nanoTime()));
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
