package org.helium.perfmon.recoder;


import org.helium.common.extension.SPI;
import org.helium.data.h2.H2DataSource;
import org.helium.framework.configuration.Environments;
import org.helium.perfmon.Stopwatch;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverInspector.ReportCallback;
import org.helium.perfmon.observation.ObserverReport;
import org.helium.perfmon.observation.ObserverReportColumn;
import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.perfmon.simple.PerfmonCounters;
import org.helium.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;

/**
 * Created by Coral on 11/11/15.
 */
@SPI
public interface ICounterStorage {
	/**
	 * 设置存储缓存
	 * @param ob
	 * @param dateFormat
	 * @param tableNameFormat
	 */
	void setEnv(Observable ob, String dateFormat, String tableNameFormat);
	/**
	 * 存储内容
	 * @param report
	 */
	void saveReport(ObserverReport report);


	ReportCallback getReportCallback(Consumer<Tuple<ICounterStorage, ObserverReport>> consumer);

}
