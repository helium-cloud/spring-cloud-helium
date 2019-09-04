package test.org.helium.perfmon;

import com.feinno.superpojo.type.TimeSpan;
import org.helium.perfmon.AbstractCounterEntity;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.observation.*;
import org.helium.perfmon.observation.ObserverInspector.ReportCallback;

import java.util.Random;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class CounterTest {
	public static void main(String[] args) {
		new CounterTest().test();
	}

	public void test() {
		final BeanPerformanceCounters appCounter = PerformanceCounterFactory.getCounters(BeanPerformanceCounters.class, "");
		final SampleCounter counter = PerformanceCounterFactory.getCounters(SampleCounter.class, "");
		counter.getNumber().increase();
		counter.getNumber().decrease();

		Thread tr = new Thread(new Runnable() {
			@Override
			public void run() {
				Random rand = new Random();
				while (true) {
					try {
						Thread.sleep(0);
						long l = 0;
						for (int i = 0; i < 1 * 1; i++) {
							l = l | System.nanoTime();
						}
						counter.getThroughput().increaseBy(1000 + rand.nextInt(1000) & l & 0x0000ffff);
						counter.getRatio().increaseRatio(l % 2 == 0);
						appCounter.setTx(counter.getRatio());
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		});

		tr.start();

		// counter.increase(2000);
		// counter.increase(100);
		AbstractCounterEntity entity = (AbstractCounterEntity) counter.getRatio();
		ObserverReportSnapshot last = entity.getObserverSnapshot();

		Observable ob = ObserverManager.getObserverItem("sample");
		ObserverManager.addInspector(ob, ObserverReportMode.ALL, new TimeSpan(5000), new ReportCallback() {
			@Override
			public boolean handle(ObserverReport report)

			{
				System.out.println(report.encodeToJson());
				return true;
			}
		});
		ObserverManager.addInspector(ObserverManager.getObserverItem("apps"), ObserverReportMode.ALL, new TimeSpan(5000), new ReportCallback() {
			@Override
			public boolean handle(ObserverReport report) {
				System.out.println(report.encodeToJson());
				return true;
			}
		});

		// new MonitorHttpServer(8089);

		int i = 0;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			ObserverReportSnapshot now = entity.getObserverSnapshot();
			ObserverReportUnit output = now.computeReport(last);
			System.out.println(output.toString());
			i++;
			if (i == 8) {
				break;
			}
		}
	}
}