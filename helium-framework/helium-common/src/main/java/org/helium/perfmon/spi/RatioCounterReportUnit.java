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
public class RatioCounterReportUnit implements ObserverReportUnit {
	private long hits;
	private long total;
	private long nanos;

	public long getHits() {
		return hits;
	}

	public long getTotal() {
		return total;
	}

	public double getRatio() {
		if (total == 0) {
			return 0.0f;
		} else {
			return (double) hits / total;
		}
	}

	public RatioCounterReportUnit(long hits, long total, long nanos) {
		super();
		this.hits = hits;
		this.total = total;
		this.nanos = nanos;
	}

	@Override
	public void outputReport(ObserverReportRow row) {
		row.output((double)total * 1E9 / nanos);
		row.output(getRatio());
	}

	@Override
	public ObserverReportUnit summaryAll(List<ObserverReportUnit> items) {
		long sTotal = 0;
		long sHits = 0;

		for (ObserverReportUnit a : items) {
			RatioCounterReportUnit u = (RatioCounterReportUnit) a;
			sTotal += u.total;
			sHits += u.hits;
		}

		return new RatioCounterReportUnit(sTotal, sHits, nanos);
	}

	public static ObserverReportColumn[] getColumns(String name) {
		return new ObserverReportColumn[] {
				new ObserverReportColumn(name + "(/sec)", ObserverReportColumnType.DOUBLE),
				new ObserverReportColumn(name + "(ratio)", ObserverReportColumnType.RATIO),
		};
	}

	public String toString() {
		return String.format("total %d ratio %f", total, getRatio());
	}
}
