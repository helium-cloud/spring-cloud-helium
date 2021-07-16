package org.helium.perfmon.recoder;

import org.helium.superpojo.type.TimeSpan;
import org.helium.framework.entitys.PerfmonCountersConfiguration;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverManager;
import org.helium.perfmon.observation.ObserverReport;
import org.helium.perfmon.observation.ObserverReportMode;
import org.helium.threading.DelayRunner;
import org.helium.threading.SimpleQueuedWorker;
import org.helium.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.sql.SQLException;


/**
 *
 * <b>描述: </b>WorkerAgent负责和HACenter建立长连接, 并保持心跳, 自动重连
 * <p>
 * <b>功能: </b>
 * <p>
 * <b>用法: </b>
 * <p>
 *
 *
 */
public class PerfmonRecorder{
	/** 日志引用 */
	private static Logger LOGGER = LoggerFactory.getLogger(PerfmonRecorder.class);

	private static Marker MARKER = MarkerFactory.getMarker("PERFMON");

	/** 日期格式 */
	private String dateFormat = "yyyyMMdd";

	/** 表名 */
	private String tableNameFormat = "PERFMON_${COUNTER}_${DATE}";

	private PerfmonCountersConfiguration configuration=  new PerfmonCountersConfiguration();

	private SimpleQueuedWorker<Tuple<ICounterStorage, ObserverReport>> reportWorker;

	public PerfmonRecorder() {

		//
		// 因为存在不少计数器初始化比较晚的问题, 等系统完全初始化完成后再加载计数器
		int delaySeconds = PerfmonCountersConfiguration.DEFAULT_DELAY_SECONDS;

 		DelayRunner.run(1, () -> {
			reportWorker = new SimpleQueuedWorker<>("perfmonRecorder", t -> {
			    ICounterStorage recorder = t.getV1();
				recorder.saveReport(t.getV2());
		    });
			for (Observable observable: ObserverManager.getAllObserverItems()){
				ICounterStorage counterStorage = CounterStorageFactory.getStorage(observable, dateFormat, tableNameFormat);
				TimeSpan span = new TimeSpan(1000 * 10);
				ObserverManager.addInspector(observable, ObserverReportMode.ALL, span, counterStorage.getReportCallback(
						t -> reportWorker.enqueue(t)
				));
			}

		});
	}
}
