package org.helium.perfmon.observation;

import java.util.List;

/**
 * {在这里补充类的功能说明}
 * 
 * Created by Coral
 */
public interface ObserverReportUnit {
	void outputReport(ObserverReportRow row);

	ObserverReportUnit summaryAll(List<ObserverReportUnit> items);
}
