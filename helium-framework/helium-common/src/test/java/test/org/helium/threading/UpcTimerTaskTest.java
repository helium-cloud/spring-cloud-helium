package test.org.helium.threading;

import org.helium.threading.Timeout;
import org.helium.threading.TimeoutListener;
import org.helium.threading.UpcTimerTask;

/**
 * @author coral
 * @version 创建时间：2015年1月9日 类说明
 */
public class UpcTimerTaskTest {
	public static void main(String[] args) throws InterruptedException {
		UpcTimerTask task = new UpcTimerTask(2 * 1000, new TimeoutListener() {
			@Override
			public void onTimeout(Timeout timeout) {
				System.out.println("11111111");
			}
		});
		// task.setExecutor(null);
		task.start();

		// Thread.sleep(2*1000);

		// task.updateTime(1*1000);

		 task.cancel();
	}
}
