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
public class ThroughputCounterReportUnit implements ObserverReportUnit {
	private long throughput;
	private long times;
	private long nanos;

	public ThroughputCounterReportUnit(long throughput, long times, long nanos) {
		super();
		this.throughput = throughput;
		this.times = times;
		this.nanos = nanos;
	}

	@Override
	public String toString() {
		double tr = (double) throughput * 1E9 / nanos;
		double ti = (double) times * 1E9 / nanos;
		return String.format("%f %f", tr, ti);
	}

	@Override
	public void outputReport(ObserverReportRow row) {
		double tr = (double) throughput * 1E9 / nanos;
		double ti = (double) times * 1E9 / nanos;
		row.output(tr);
		row.output(ti);
	}

	@Override
	public ObserverReportUnit summaryAll(List<ObserverReportUnit> items) {
		long stp = 0;
		long sti = 0;

		for (ObserverReportUnit a : items) {
			ThroughputCounterReportUnit u = (ThroughputCounterReportUnit) a;
			stp += u.throughput;
			sti += u.times;
		}

		return new ThroughputCounterReportUnit(stp, sti, nanos);
	}

	public static ObserverReportColumn[] getColumns(String name) {
		return new ObserverReportColumn[] { new ObserverReportColumn(name + "(bytes/sec)", ObserverReportColumnType.DOUBLE),
				new ObserverReportColumn(name + "(/sec)", ObserverReportColumnType.DOUBLE), };
	}
}
