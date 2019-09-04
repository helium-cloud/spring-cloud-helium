package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.perfmon.observation.ObserverReportColumnType;
import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.perfmon.observation.ObserverReportUnit;

import java.util.List;

/**
 * Created by Coral on 10/22/15.
 */
public class QpsCounterReportUnit implements ObserverReportUnit {
	private long total;
	private double qps;

	public long getTotal() {
		return total;
	}

	public double getQps() {
		return qps;
	}

	public QpsCounterReportUnit(long total, double qps) {
		this.total = total;
		this.qps = qps;
	}

	@Override
	public String toString() {
		return String.format("%f/sec total=%d", qps, total);
	}

	@Override
	public void outputReport(ObserverReportRow row) {
		row.output(total);
		row.output(qps);
	}

	@Override
	public ObserverReportUnit summaryAll(List<ObserverReportUnit> items) {
		long sTotal = 0;
		double sQps = 0.0;

		String error = null;
		for (ObserverReportUnit a : items) {
			QpsCounterReportUnit u = (QpsCounterReportUnit) a;
			sTotal += u.total;
			sQps += u.qps;
		}

		return new QpsCounterReportUnit(sTotal, sQps);
	}

	public static ObserverReportColumn[] getColumns(String name) {
		return new ObserverReportColumn[] {
				new ObserverReportColumn(name + "(total)", ObserverReportColumnType.LONG),
				new ObserverReportColumn(name + "(/sec.)", ObserverReportColumnType.DOUBLE),
		};
	}
}
