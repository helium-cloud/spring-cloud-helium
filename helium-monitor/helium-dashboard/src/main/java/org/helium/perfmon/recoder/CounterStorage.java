package org.helium.perfmon.recoder;


import com.alibaba.fastjson.JSONObject;
import org.helium.data.h2.H2DataSource;
import org.helium.framework.configuration.Environments;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverInspector.ReportCallback;
import org.helium.perfmon.observation.ObserverReport;
import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.util.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

/**
 * Created by Coral on 11/11/15.
 */
public class CounterStorage {
	private String lastDateString;
	private SimpleDateFormat dateFormat;
	private static final Logger LOGGER = LoggerFactory.getLogger(CounterStorage.class);

	public CounterStorage(Observable ob, String dateFormat, String tableNameFormat) {

		this.dateFormat = new SimpleDateFormat(dateFormat);

		initStorage();
	}

	public void initStorage() {
		try {
			H2DataSource.getInstance().getConnection().createStatement();
			createTable();
		} catch (SQLException throwables) {
			throwables.printStackTrace();
		}
	}

	/**
	 * 创建db
	 *
	 * @throws SQLException
	 */
	public void createTable() throws SQLException {
		Statement createStatement = H2DataSource.getInstance().getConnection().createStatement();
		//Time,Service,Pid,Instance
		String createTable = "DROP TABLE perfmon_tb IF EXISTS; create table perfmon_tb(Time VARCHAR(50), " +
				"Service VARCHAR(50) , Pid VARCHAR(50) ,Instance VARCHAR(50), ,Data VARCHAR(50) )";
		createStatement.executeUpdate(createTable);

	}

	public void insertData(Object[] params) throws SQLException {
		Statement insertStatement = H2DataSource.getInstance().getConnection().createStatement();
		String insertSql = "INSERT INTO perfmon_tb VALUES('" + params[0] + "','" + params[1]
				+ "', '" + params[2] + "', '" + params[3] + "',"+ JSONObject.toJSONString(params[4])+" )";
		insertStatement.executeUpdate(insertSql);

	}

	public void saveReport(ObserverReport report) throws SQLException {
		String dateString = dateFormat.format(report.getTime().getDate());

		if (!dateString.equals(lastDateString)) {
			lastDateString = dateString;
		}


		Object[] params = new Object[report.getColumns().size() + 4]; // Time,Instance,{Columns}
		params[0] = report.getTime().getDate();
		params[1] = "server";
		params[2] = Environments.getPid();

		for (ObserverReportRow row : report.getRows()) {
			String[] datas = row.getData();
			params[3] = row.getInstanceName();
			if (params[3] == null) {
				params[3] = "";
			}
			params[4] = datas;
//			for (int i = 0; i < datas.length; i++) {
//				params[4 + i] = datas[i];
//			}
			insertData(params);
		}
	}


	public ReportCallback getReportCallback(Consumer<Tuple<CounterStorage, ObserverReport>> consumer) {
		return new ReportCallback() {
			@Override
			public boolean handle(ObserverReport report) {
				consumer.accept(new Tuple<>(CounterStorage.this, report));
				return true;
			}
		};
	}

}
