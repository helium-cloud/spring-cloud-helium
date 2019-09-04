package org.helium.perfmon.recoder;

import org.helium.perfmon.observation.Observable;
import org.helium.perfmon.observation.ObserverInspector.ReportCallback;
import org.helium.perfmon.observation.ObserverReport;
import org.helium.perfmon.observation.ObserverReportColumn;

import org.helium.perfmon.observation.ObserverReportRow;
import org.helium.util.Tuple;
import org.helium.database.*;
import org.helium.database.spi.DatabaseManager;
import org.helium.framework.configuration.Environments;
import org.helium.framework.spi.Bootstrap;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.function.Consumer;

/**
 * Created by Coral on 11/11/15.
 */
public class CounterRecorder {
	private static final String DBNAME = "MONDB.LOCAL";
	private String lastDateString;
	private String sqlInsert;
	private Database db;
	private Observable ob;
	private DatabaseOperator operator;
	private SimpleDateFormat dateFormat;
	private String tableNameFormat;

	public CounterRecorder(Observable ob, ConnectionString connStr, String dateFormat, String tableNameFormat) {
		this.ob = ob;

		this.operator = DatabaseManager.INSTANCE.getDatabaseOperator(DBNAME, connStr);
		this.db = DatabaseManager.INSTANCE.getDatabase(DBNAME, connStr);

		this.dateFormat = new SimpleDateFormat(dateFormat);
		this.tableNameFormat = tableNameFormat;

		// 启动的时候删除一次
		try {
			operator.dropRedundantTables(1);
		} catch (Exception ex) {

		}
	}

	public void saveReport(ObserverReport report) throws SQLException {
		String dateString = dateFormat.format(report.getTime().getDate());

		if (!dateString.equals(lastDateString)) {
			String tableName = formatTableName(dateString); // 要将sqlInsert拼出来
			TableSchema schema = createTableSchema(tableName, ob);
			if (!operator.isTableExists(tableName)) {
				operator.createTable(schema);
				// 创建表的时候删除一次
				try {
					operator.dropRedundantTables(1);
				} catch (Exception ex) {

				}
			}
			lastDateString = dateString;
		}

		// Transaction tx = db.beginTransaction();
		Object[] params = new Object[report.getColumns().size() + 4]; // Time,Instance,{Columns}
		params[0] = report.getTime().getDate();
		params[1] = Bootstrap.INSTANCE.getServerId();
		params[2] = Environments.getPid();

		for (ObserverReportRow row: report.getRows()) {
			String[] datas = row.getData();
			params[3] = row.getInstanceName();
			if (params[3] == null) {
				params[3] = "";
 			}
			for (int i = 0; i < datas.length; i++) {
				params[4 + i] = datas[i];
			}
			db.executeInsert(sqlInsert, params);
		}
//		.commit();
	}

	private String formatTableName(String dateString) {
		String counterName = ob.getObserverName().replace(":", "_").replace(".", "_").replace("-", "_");
		return tableNameFormat.replace("${COUNTER}", counterName).replace("${DATE}", dateString);
	}

	/**
	 * 创建一个新的日志表结构
	 *
	 * @return
	 */
	public TableSchema createTableSchema(String tableName, Observable ob) {
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		TableSchema t = new TableSchema(tableName);

		sql1.append("INSERT INTO ");
		sql1.append(tableName);
		// table.addColumn(Column.createAutoIncrementIntColumn("Id"));
		t.addColumn(Column.createDateTimeColumn("Time", false, null));
		t.addColumn(Column.createVarcharColumn("Service", 32, false, null));
		t.addColumn(Column.createLongColumn("Pid", false, null));
		t.addColumn(Column.createVarcharColumn("Instance", 256, false, ""));
		sql1.append(" (Time,Service,Pid,Instance");
		sql2.append(" (?,?,?,?");
		for (ObserverReportColumn col: ob.getObserverColumns()) {
			String colName = col.getName();
			switch (col.getType()) {
				case DOUBLE:
					t.addColumn(Column.createDoubleColumn(colName, false, null));
					break;
				case LONG:
					t.addColumn(Column.createLongColumn(colName, false, null));
					break;
				case RATIO:
					t.addColumn(Column.createDoubleColumn(colName, false, null));
					break;
				case TEXT:
					t.addColumn(Column.createVarcharColumn(colName, 2048, false, null));
					break;
			}
			sql1.append(",`").append(colName).append("`");
			sql2.append(",?");
		};
		sql1.append(") VALUES").append(sql2.toString()).append(")");
		sqlInsert = sql1.toString();
		return t;
	}

	public ReportCallback getReportCallback(Consumer<Tuple<CounterRecorder, ObserverReport>> consumer) {
		return new ReportCallback() {
			@Override
			public boolean handle(ObserverReport report) {
				consumer.accept(new Tuple<>(CounterRecorder.this, report));
				return true;
			}
		};
	}

	private void dropRedundantTables(){
		String sql = "SELECT left(TABLE_NAME, length(TABLE_NAME)-8) as NAME_PRE FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'MonDB' and TABLE_NAME LIKE 'PERFMON_%'group by NAME_PRE";

	}
}
