package org.helium.perfmon.recoder;


import org.helium.data.h2.H2DataSource;
import org.helium.framework.configuration.Environments;
import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverInspector.ReportCallback;
import org.helium.perfmon.observation.ObserverReport;
import org.helium.perfmon.observation.ObserverReportColumn;
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
		createTable(ob);
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
			for (int i = 0; i < datas.length; i++) {
				params[4 + i] = datas[i];
			}
			saveData(report.getCategory(), params);
		}
	}

	public void createTable(Observable ob) {
		StringBuilder sb = new StringBuilder();
		try {
			String tableName = ob.getObserverName().replace("-", "_");
			sb.append("DROP TABLE ");
			sb.append(tableName);
			sb.append(" IF EXISTS;");
			sb.append("create table ");
			sb.append(tableName);
			sb.append("(Time varchar(50),Service varchar(50),Pid varchar(50),Instance varchar(50)");
			for (ObserverReportColumn reportColumn : ob.getObserverColumns()) {
				String type = "varchar(50)";
				switch (reportColumn.getType()) {
					case DOUBLE:
						type = "DOUBLE";
						break;
					case LONG:
						type = "INT";
						break;
					case RATIO:
						type = "INT";
						break;
					case TEXT:
						type = "varchar(50)";
						break;
				}
				sb.append(",`").append(reportColumn.getName().trim()).append("` ").append(type);
			}
			sb.append(")");
			sb.toString();

			Statement createStatement = H2DataSource.getH2Connection().createStatement();
			createStatement.executeUpdate(sb.toString());
		} catch (Exception e) {
			LOGGER.error("createTable exception:{}", sb, e);
		}
	}


	public void saveData(String tableName, Object[] params) throws SQLException {
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO ");
		sb.append(tableName.replace("-", "_"));
		sb.append(" VALUES(");
		for (int i = 0; i < params.length; i++) {
			if (i < 4){
				sb.append("'").append(params[i]).append("'").append(", ");
			} else if (i < params.length - 1) {
				sb.append(params[i]).append(", ");
			} else {
				sb.append("'").append(params[i]).append("'");
			}

		}
		sb.append(")");
		insertStatement.executeUpdate(sb.toString());

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
