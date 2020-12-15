package org.helium.database.logging.db;

import org.helium.database.*;
import org.helium.database.spi.DatabaseManager;
import org.helium.logging.LogAppender;
import org.helium.logging.LogLevel;
import org.helium.logging.spi.LogEvent;
import org.helium.logging.spi.LogUtils;
import org.helium.util.ServiceEnviornment;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * 基于Jdbc的日志输出工具
 *
 * Created by Coral
 */
public class JdbcAppender implements LogAppender {
	private static final String serviceName = ManagementFactory.getRuntimeMXBean().getName();
	private static final String LOGDB = "LOGDB.LOCAL";
	/** 日期格式 */
	private String dateFormat = "yyyyMMdd";

	/** 表名 */
	private String tableNameFormat = "LOG_${DATE}";

	/** 数据库Url */
	private String jdbcUrl;

	/** 数据库驱动 */
	private String driverClass = "com.mysql.jdbc.Driver";

	/** 数据库用户名 */
	private String user;

	/** 数据库密码 */
	private String password;

	/** 记录日志等级 */
	private String level = "INFO";

	public JdbcAppender() {
	}

	public JdbcAppender(String jdbcUrl, String user, String password) {
		this.jdbcUrl = jdbcUrl;
		this.user = user;
		this.password = password;
	}



	@Override
	public void open() {
		ConnectionString connStr = ConnectionString.builder()
				.jdbcUrl(jdbcUrl)
				.driverClass(driverClass)
				.user(user)
				.password(password)
				.toConnStr();
		operator = DatabaseManager.INSTANCE.getDatabaseOperator(LOGDB, connStr);
		db = DatabaseManager.INSTANCE.getDatabase(LOGDB, connStr);

		dateFormatObject = new SimpleDateFormat(dateFormat);

		try {
			// 每次创建的时候删除一下之前的表
			try {
				operator.dropRedundantTables(0);
			}catch (Exception ex){

			}

			prepareTable(new Date());
		} catch (Exception ex) {
			throw new RuntimeException("JdbcAppender open() failed!", ex);
		}
	}

	@Override
	public void close() {

	}

	@Override
	public boolean needQueue() {
		return true;
	}

	@Override
	public void writeLog(LogEvent event) throws IOException {
		try {
			prepareTable(event.getTime());
			insertLogEvent(event);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	private void insertLogEvent(LogEvent e) throws SQLException {
		LogLevel currentLevel = LogUtils.parseLogLevel(level.toUpperCase());
		if(!e.getLevel().canLog(currentLevel)){ return ;}
		db.executeNonQueryForLog(currentInsertSQL,
				e.getTime(),
				e.getLoggerName(),
				e.getLevel().intValue(),
				e.getMessage().length() < 2000 ? e.getMessage() : e.getMessage().substring(0, 2000),
				e.getError() == null ? "" : LogUtils.formatError(e.getError()),
				e.getMarker() != null ? e.getMarker().toString() : "",
				e.getThreadId(),
				e.getThreadName(),
				ServiceEnviornment.getPid(),
				serviceName,
				serviceName);
	}

	private void prepareTable(Date t) throws SQLException {
		String dateString = dateFormatObject.format(t);
		if (dateString.equals(currentDateString)) {
			return;
		}

		// 每次创建的时候删除一下之前的表
		try {
			operator.dropRedundantTables(0);
		}catch (Exception ex){

		}

		String tableName = tableNameFormat.replace("${DATE}", dateString);
		currentDateString = dateString;
		currentInsertSQL = INSERT_SQL.replace("${TABLE_NAME}", tableName);

		if (!operator.isTableExists(tableName)) {
			TableSchema ts = createTableSchema(tableName);
			operator.createTable(ts);
		}
	}

	/**
	 * 创建一个新的日志表结构
	 *
	 * @param tablename
	 * @return
	 */
	private TableSchema createTableSchema(String tablename) {
		TableSchema table = new TableSchema(tablename);
		table.addColumn(Column.createDateTimeColumn("Time", false, null));
		table.addColumn(Column.createVarcharColumn("LoggerName", 256, false, null));
		table.addColumn(Column.createIntColumn("Level", false, null));
		table.addColumn(Column.createTextColumn("Message", true));
		table.addColumn(Column.createTextColumn("Error", true));
		table.addColumn(Column.createVarcharColumn("Marker", 256, true, null));
		table.addColumn(Column.createIntColumn("ThreadId", false, null));
		table.addColumn(Column.createVarcharColumn("ThreadName", 128, false, null));
		table.addColumn(Column.createIntColumn("Pid", false, null));
		table.addColumn(Column.createVarcharColumn("ServiceName", 128, true, null));
		table.addColumn(Column.createVarcharColumn("Computer", 128, true, null));
		table.setExtension("KEY `IX_Time` (`Time`) USING BTREE, " +
				"KEY `IX_LoggerName` (`LoggerName`) USING BTREE, " +
				"KEY `IX_Marker` (`Marker`) USING BTREE");
		return table;
	}

	private Database db;
	private String currentDateString;
	private String currentInsertSQL;
	private DateFormat dateFormatObject;
	private DatabaseOperator operator;

	private static final String INSERT_SQL = "insert into ${TABLE_NAME} " +
			"(Time,LoggerName,Level,Message,Error,Marker,ThreadId,ThreadName,Pid,ServiceName,Computer) " +
			"values (?,?,?,?,?,?,?,?,?,?,?)";
}
