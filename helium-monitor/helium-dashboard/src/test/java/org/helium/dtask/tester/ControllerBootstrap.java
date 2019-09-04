package org.helium.dtask.tester;

import com.feinno.superpojo.type.DateTime;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.spi.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 3/19/16.
 */
@ServiceImplementation
public class ControllerBootstrap implements TaskCollectService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ControllerBootstrap.class);

	public static void main(String[] args) throws Exception {
		Bootstrap.INSTANCE.addPath("helium-dashboard/build/resources/test");
		Bootstrap.INSTANCE.initialize("bootstrap-2163-controller.xml", true, false);

		LOGGER.warn("Test begin after 6 seconds...");
		Thread.sleep(6000);

		ControllerBootstrap self = (ControllerBootstrap)Bootstrap.INSTANCE.getService(TaskCollectService.class);
		self.runTest();
	}

	@ServiceSetter
	private TaskLaunchService service;

	private int caseIndex;
	private int completes = 0;
	private int errors = 0;
	private Map<Integer, SampleDedicatedTaskArgs> cases = new HashMap<>();

	static final int BATCH = 2000;
	static final int TAGS = 37;

	public void runTest() {
		int count = 0;

		while (true) {
			try {
				if (count > 0) {
					//
					// 每秒测试100次
					if (count % 100 == 0) {
						Thread.sleep(1000);
						LOGGER.warn("running: cases={}", caseIndex);
					}

					//
					// 每一分钟休息一下, 打印报表
					if (count % BATCH == 0) {
						Thread.sleep(5000);
						printReport();
						Thread.sleep(3000);
						LOGGER.error(">>> NEXT BATCH");
						clearReport();
					}
				}
				count++;
				SampleDedicatedTaskArgs args = generateArgs();
				service.fireTask(args);
			} catch (Exception ex) {
				errors++;
				LOGGER.error("runTest failed: {}", ex);
			}
		}
	}

	public synchronized SampleDedicatedTaskArgs generateArgs() {
		caseIndex++;
		SampleDedicatedTaskArgs args = new SampleDedicatedTaskArgs();
		args.setCaseIndex(caseIndex);
		String tag = "AE" + Integer.toString(caseIndex % TAGS);
		args.setTag(tag);
		if ((caseIndex % BATCH) > (BATCH / 2)) {
			args.setCallPutTask((caseIndex % TAGS) % 5 == 0);
		}
		args.setCallbackUrl("tcp://127.0.0.1:6700/test.CollectService");

		args.beginTime = DateTime.now();
		cases.put(caseIndex, args);
		return args;
	}

	public synchronized void printReport() {
		Stat[] stats = new Stat[TAGS];
		for (int i = 0; i < TAGS; i++) {
			stats[i] = new Stat();
		}
		cases.forEach((k, v) -> {
			Stat stat = stats[k % TAGS];
			if (stat == null) {
				LOGGER.error("Missing tags data: tag=" + k);
				return;
			}
			stat.hitService(v.getServicePid());
			stat.hitTask(v.getTaskPid());
		});

		LOGGER.error(">>> REPORT for cases: {}/{} errors={}", completes, BATCH, errors);
		LOGGER.error("tag\tservice\ttask");
		int total = 0;
		for (int i = 0; i < TAGS; i++) {
			LOGGER.error("{}\t{}\t{}", i, stats[i].getServiceHits(), stats[i].getTaskHits());
			total += stats[i].getHits();
		}
		LOGGER.error("TOTAL\t{}\t{}\t", total);
	}

	public synchronized void clearReport() {
		completes = 0;
		cases.clear();
	}

	@Override
	public synchronized void taskComplete(SampleDedicatedTaskArgs args) {
		completes++;
		SampleDedicatedTaskArgs a2 = cases.get(args.getCaseIndex());
		if (a2 == null) {
			LOGGER.error("case missing index=" + args.getCaseIndex());
			return;
		}
		a2.setCaseIndex(args.getCaseIndex());
		a2.setTaskPid(args.getTaskPid());
		a2.setServicePid(args.getServicePid());
		a2.endTime = DateTime.now();
	}

	@Override
	public synchronized void taskClosed(String tag) {
		LOGGER.warn("taskClosed: tag={}", tag);
//		SampleDedicatedTaskArgs a2 = cases.get(tag);
//		if (a2 == null) {
//			LOGGER.error("tag missing:" + tag);
//			return;
//		}
//		a2.closed = true;
	}

	static class Stat {
		Map<Integer, Integer> serviceHits = new HashMap<>();
		Map<Integer, Integer> taskHits = new HashMap<>();

		void hitService(int pid) {
			if (serviceHits.get(pid) == null) {
				serviceHits.put(pid, 1);
			} else {
				serviceHits.put(pid, serviceHits.get(pid) + 1);
			}
		}
		void hitTask(int pid) {
			if (taskHits.get(pid) == null) {
				taskHits.put(pid, 1);
			} else {
				taskHits.put(pid, taskHits.get(pid) + 1);
			}
		}

		public String getServiceHits() {
			StringBuilder s = new StringBuilder();
			serviceHits.forEach((k, v) -> {
				s.append("" + k + ":" + v + ",");
			});
			return s.toString();
		}

		public String getTaskHits() {
			StringBuilder s = new StringBuilder();
			taskHits.forEach((k, v) -> {
				s.append("" + k + ":" + v + ",");
			});
			return s.toString();
		}

		public int getHits() {
			final int[] n = {0};
			taskHits.forEach((k, v) -> {
				n[0] += v;
			});
			return n[0];
		}
	}
}
