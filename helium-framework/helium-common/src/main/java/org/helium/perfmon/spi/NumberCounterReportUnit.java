package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.perfmon.observation.ObserverReportColumnType;
import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.perfmon.observation.ObserverReportUnit;

import java.util.List;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public class NumberCounterReportUnit implements ObserverReportUnit {
	public static ObserverReportColumn[] getColumns(String name) {
		return new ObserverReportColumn[] { new ObserverReportColumn(name, ObserverReportColumnType.LONG) };
	}

	private long count;

	public long getCount() {
		return count;
	}

	public NumberCounterReportUnit(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return String.format("%d", count);
	}

	@Override
	public void outputReport(ObserverReportRow row) {
		row.output(count);
	}

	@Override
	public ObserverReportUnit summaryAll(List<ObserverReportUnit> units) {
		long sum = 0;
		for (ObserverReportUnit a : units) {
			NumberCounterReportUnit u = (NumberCounterReportUnit) a;
			sum += u.count;
		}
		return new NumberCounterReportUnit(sum);
	}
}
