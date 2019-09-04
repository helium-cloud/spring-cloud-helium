package org.helium.perfmon;

import org.helium.perfmon.observation.ObservableUnit;
import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.perfmon.observation.ObserverReportSnapshot;
import org.helium.perfmon.observation.ObserverReportUnit;

import java.util.ArrayList;
import java.util.List;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class CounterCategoryInstance implements ObservableUnit {
	private int count;
	private Object referredObj;
	private AbstractCounterEntity[] entitys;
	private String instanceName;

	public Object getReferredObject() {
		return this.referredObj;
	}

	@Override
	public String getInstanceName() {
		return instanceName;
	}

	CounterCategoryInstance(AbstractCounterEntity[] entitys, Object obj, String instanceName) {
		this.entitys = entitys;
		this.referredObj = obj;
		this.count = entitys.length;
		this.instanceName = instanceName;
	}

	@Override
	public ObserverReportSnapshot getObserverSnapshot() {
		Snapshot ret = new Snapshot(this, count);
		for (int i = 0; i < count; i++) {
			ObserverReportSnapshot snap = entitys[i].getObserverSnapshot();
			ret.snapshots[i] = snap;
		}
		return ret;
	}

	private static class Snapshot extends ObserverReportSnapshot {
		private CounterCategoryInstance parent;
		private ObserverReportSnapshot[] snapshots;

		private Snapshot(CounterCategoryInstance parent, int count) {
			this.parent = parent;
			snapshots = new ObserverReportSnapshot[count];
		}

		@Override
		public ObserverReportUnit computeReport(ObserverReportSnapshot last) {
			ReportUnit ret = new ReportUnit(parent, snapshots.length);
			Snapshot rval = (Snapshot) last;
			for (int i = 0; i < snapshots.length; i++) {
				ret.reports[i] = this.snapshots[i].computeReport(rval.snapshots[i]);
			}
			return ret;
		}
	}

	private static class ReportUnit implements ObserverReportUnit {
		private CounterCategoryInstance parent;
		private ObserverReportUnit[] reports;

		public ReportUnit(CounterCategoryInstance parent, int count) {
			this.parent = parent;
			reports = new ObserverReportUnit[count];
		}

		@Override
		public void outputReport(ObserverReportRow row) {
			for (ObserverReportUnit r : reports) {
				r.outputReport(row);
			}
		}

		@Override
		public ObserverReportUnit summaryAll(List<ObserverReportUnit> summary) {
			ReportUnit ret = new ReportUnit(parent, reports.length);
			for (int i = 0; i < reports.length; i++) {
				List<ObserverReportUnit> l = new ArrayList<ObserverReportUnit>();
				for (ObserverReportUnit u : summary) {
					ReportUnit r = (ReportUnit) u;
					l.add(r.reports[i]);
				}
				ObserverReportUnit unit = parent.entitys[i].getEmptyReport();
				if (l.size() > 0) {
					ret.reports[i] = unit.summaryAll(l);
				} else {
					ret.reports[i] = unit;
				}
			}
			return ret;
		}
	}
}
