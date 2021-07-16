package org.helium.perfmon.observation;

import org.helium.superpojo.type.DateTime;
import org.helium.superpojo.type.TimeSpan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>描述: </b><br>
 * 该类是一个对性能计数器(或其它监视器)监控到的数据进行数据采集的类，<br>
 * 数据获取的过程是首先调用该类的{@link ObserverInspector#run(DateTime)}方法，它会调用该类的
 * {@link ObserverInspector#getReport(DateTime)}方法来获取当前时刻当前性能计数器的快照统计表
 * {@link ObserverReport}，{@link ObserverReport}对象中就包含着当前时刻当前计数器所记录下来的性能数据 <br>
 * 该类一般由 {@link ObserverManager} 类使用，并由
 * {@link ObserverManager#addInspector(Observable, ObserverReportMode, TimeSpan, ReportCallback)}
 * 方法添加后，由其定时任务定时进行获取数据，数据的存储格式为{@link ObserverReport}
 * 
 * <pre>
 * &lt;Counters&gt; 
 *  &lt;Counter name="rpc-server:*:*(merged)" second="600"/&gt;
 * 	&lt;Counter name="logger:*" second="600"/&gt;
 * &lt;/Counters&gt;
 * 
 * - counter的输入是个table，不同列的table
 * Counter
 * 		category:name:instance
 * 		type
 * 			number
 * 			throughput
 * 			ratio
 * 			transaction
 * ObserverableItem （考虑统一的问题么）
 * 			logger
 * 			database
 * 
 * Display
 * 
 * rpc-service
 * 	
 * 	
 * 
 * rpc-server:FAE.Service.All 
 * 		4 * 100 (from)
 * 		Output汇总（inspector级别的汇总）自动汇总数据
 * 		
 * - ObserverItem的输出机制与C#类似，用标注生成不同的表结构
 * - 不同类型的计数器本质上属于不同的表结构
 * - 输出目前包含以下几种
 * 		- OutputStream输出：将来有可能支持本地工具集
 * 		- DBLogger的输出（查看工具）
 * 			Key
 * 			Table_{Scheme}_YYYYMMDD
 * 		- 监控上传
 * 			PBSerializer
 * 		- 以及HttpMonitor的输出
 * 			JSONSerializer
 *  			 		
 * - 可以在log层面维护MyISAM merge引擎, 或使用view进行
 * - 
 * 
 * 
 * Inspector_Key id name
 * 
 * Inspector_item time id
 * </pre>
 * <p>
 * <b>功能: </b>对性能计数器(或其它监视器)监控到的数据进行数据采集的类
 * <p>
 * <b>用法: </b>该类由{@link ObserverManager}负责实例的创建以及定时进行数据获取，如需手动创建，则使用如下方式：
 * 
 * <pre>
 * 获取一个监视器
 * Observable ob = ObserverManager.getObserverItem(name);
 * 创建针对指定监视器的数据采集实例对象
 * ObserverInspector inspector = new ObserverInspector(ob, ObserverReportMode.ALL, null, null);
 * 通过数据采集的实例对象获取采集到的数据报表
 * ObserverReport report = inspector.getReport(DateTime.now());
 * </pre>
 * 
 * 完整用法请参见{@link ObserverManager}的用法部分
 * <p>
 * 
 * Created by Coral
 * 
 */
public class ObserverInspector {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObserverInspector.class);

	public static interface ReportCallback {
		boolean handle(ObserverReport report);
	}

	private Observable ob;
	private ObserverReportMode mode;
	private TimeSpan span;
	private ReportCallback callback;

	private List<ObserverReportColumn> columns;
	private List<Item> lastItems;
	private DateTime nextTime;

	public Observable getObservable() {
		return ob;
	}

	public ObserverInspector(Observable observer, ObserverReportMode mode, TimeSpan span, ReportCallback callback) {
		this.ob = observer;
		this.mode = mode;
		this.span = span;
		this.callback = callback;
		this.columns = ob.getObserverColumns();

		if (span != null) {
			long n = DateTime.now().getTime() / span.getTotalMillseconds();
			nextTime = new DateTime(n * span.getTotalMillseconds());
		}
	}

	public ObserverReport getReport(DateTime now) {
		ObserverReport report = new ObserverReport(ob.getObserverName(), columns, now);

		if (lastItems == null) {
			lastItems = new ArrayList<Item>();
		} else {
			switch (mode) {
			case SUMMARY:
				List<ObserverReportUnit> units = new ArrayList<ObserverReportUnit>();
				for (Item i : lastItems) {
					ObserverReportSnapshot s = i.unit.getObserverSnapshot();
					ObserverReportUnit u = i.snapshot.computeReport(s);
					units.add(u);
				}
				if (units.size() > 0) {
					ObserverReportUnit sum = units.get(0).summaryAll(units);
					sum.outputReport(report.newRow("---"));
				}
			case ALL:
				for (Item item : lastItems) {
					ObserverReportSnapshot snapshotNow = item.unit.getObserverSnapshot();
					ObserverReportUnit u = snapshotNow.computeReport(item.snapshot);
					u.outputReport(report.newRow(item.unit.getInstanceName()));
				}
			default:
				break;
			}
		}

		lastItems.clear();
		for (ObservableUnit unit : ob.getObserverUnits()) {
			Item i = new Item();
			i.unit = unit;
			i.snapshot = unit.getObserverSnapshot();
			lastItems.add(i);
		}

		return report;
	}

	public boolean run(DateTime now) {
		nextTime = nextTime.add(span);
		ObserverReport report = getReport(now);

		try {
			LOGGER.debug("begin callback, {}", report);
			return callback.handle(report);
		} catch (Exception ex) {
			LOGGER.error("callback failed. {}", ex);
			return false;
		}
	}

	public boolean onTime(DateTime time) {
		return time.compareTo(nextTime) > 0;
	}

	private static class Item {
		private ObservableUnit unit;
		private ObserverReportSnapshot snapshot;
	}

}
