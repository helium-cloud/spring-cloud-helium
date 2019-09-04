package org.helium.perfmon.spi;

import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.perfmon.observation.ObserverReportColumnType;
import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.perfmon.observation.ObserverReportUnit;
import org.helium.util.StringUtils;

import java.util.List;

/**
 * {在这里补充类的功能说明}
 *
 * Created by Coral
 */
public class TransactionCounterReportUnit implements ObserverReportUnit {
    private long totalFired;
    private long fired;
    private long totalFailed;
    private long costNanos;
    private long concurrent;
    private long nanos;
    private String error;

    public long getTotalFired() {
        return totalFired;
    }

    public long getTotalFailed() {
        return totalFailed;
    }

    public long getConcurrent() {
        return concurrent;
    }

    public double getAvgCostNanos() {

        if (fired == 0) {
            return 0.0;
        }

        return (double) costNanos / fired;
    }

    public double getPerSecond() {
        return (double) fired * 1E9 / nanos;
    }

    public String getLastError() {
        return error;
    }

    public TransactionCounterReportUnit(long totalFired, long fired, long totalFailed, long concurrent, long cost, long nanos, String error) {
        this.totalFired = totalFired;
        this.fired = fired;
        this.totalFailed = totalFailed;
        this.concurrent = concurrent;
        this.costNanos = cost;
        this.nanos = nanos;
        this.error = error;
    }

    @Override
    public String toString() {
        return String.format("%f/sec.concurrent=%d total=%d(%d error) elapse=%fms error=%s", getPerSecond(), concurrent, totalFired, totalFailed,
                getAvgCostNanos() / 1E6, error);
    }

    @Override
    public void outputReport(ObserverReportRow row) {
        row.output(totalFired);
        row.output(getPerSecond());
        row.output(concurrent);
        row.output(totalFailed);
        row.output(getAvgCostNanos() / 1E6);
        row.output(error);
    }

    @Override
    public ObserverReportUnit summaryAll(List<ObserverReportUnit> items) {
        long sTotal = 0;
        long sCount = 0;
        long sFailed = 0;
        long sConcurrent = 0;
        long sCost = 0;

        String error = null;
        for (ObserverReportUnit a : items) {
            TransactionCounterReportUnit u = (TransactionCounterReportUnit) a;
            sTotal += u.totalFired;
            sCount += u.fired;
            sFailed += u.totalFailed;
            sConcurrent += u.concurrent;
            sCost += u.costNanos;
            if (error == null && !StringUtils.isNullOrEmpty(u.error)) {
                error = u.error;
            }
        }

        return new TransactionCounterReportUnit(sTotal, sCount, sFailed, sConcurrent, sCost, nanos, error);
    }

    public static ObserverReportColumn[] getColumns(String name) {
        return new ObserverReportColumn[]{
                new ObserverReportColumn(name + "(total.)", ObserverReportColumnType.LONG),
                new ObserverReportColumn(name + "(/sec.)", ObserverReportColumnType.DOUBLE),
                new ObserverReportColumn(name + "(concurrent.)", ObserverReportColumnType.LONG),
                new ObserverReportColumn(name + "(failed.)", ObserverReportColumnType.LONG),
                new ObserverReportColumn(name + "(cost ms.)", ObserverReportColumnType.DOUBLE),
                new ObserverReportColumn(name + "(error)", ObserverReportColumnType.TEXT),
        };
    }
}
