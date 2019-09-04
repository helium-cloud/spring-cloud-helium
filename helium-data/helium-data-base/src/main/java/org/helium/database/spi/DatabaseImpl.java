package org.helium.database.spi;

import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.Stopwatch;
import com.feinno.superpojo.util.StringUtils;
import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.database.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库实现
 */
class DatabaseImpl implements Database {
	private static final int SLOW_SQL_ELAPSED = 5000;
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseImpl.class);

	private String dbName;
	private String tableSchema;
	private DataSource dataSource;
	private DatabaseCounters totalCounter;

	DatabaseImpl(String name, DataSource ds) {
		this.dbName = name;
		this.dataSource = ds;
		this.totalCounter = PerformanceCounterFactory.getCounters(DatabaseCounters.class, name);
		// this.txCounter = counters.getTx();
	}

	@Override
	public String getName() {
		return dbName;
	}

	@Override
	public DataSource getDataSource() {
		return dataSource;
	}

	void setTableSchema(String name) {
		this.tableSchema = name;
	}


	@Override
	public String getTableSchema() {
		return this.tableSchema;
	}

	@Override
	public boolean test() {
		try {
			DataTable table = executeTable("select 1");
			return true;
		} catch (SQLException e) {
			LOGGER.info("Database:{} test failed", this.dbName);
			return false;
		}
	}

	@Override
	public int spExecuteNonQuery(String spName, String[] params, Object... values) throws SQLException {
		params = params == null ? new String[]{} : params;
		values = values == null ? new String[]{} : values;

		Connection conn = null;
		CallableStatement stmt = null;
		String sql = "";
		String[] paramsTemp = copyParams(params);// 临时存放参数名称，防止先mysql后sqlserver访问时修改了参数名称出错

		Stopwatch watch = getTxCounter(spName).begin();
		try {
			if (params != null) {
				if (params.length != values.length) {
					throw new IllegalArgumentException("params.length != values.length");
				}
			}
			conn = getConnection();

			//此处开始才真的是执行数据库时间，上面的还包含getConnection的时间
//			SmartCounter execCounter = DatabasePerfmon.getExecCounter(obKey);
//			execWatch = execCounter.begin();

			String dbType = conn.getMetaData().getDatabaseProductName();
//			if (dbType.equalsIgnoreCase("MySQL")) {
//				convertAtToT(paramsTemp, params);
//			}
			sql = DatabaseHelper.getCallSql(spName, values == null ? 0 : values.length);
			stmt = conn.prepareCall(sql);
			if (dbType.equalsIgnoreCase("MySQL")) {
				DatabaseHelper.fillStatement(stmt, paramsTemp, values);
			} else {
				DatabaseHelper.fillStatement(stmt, params, values);
			}

			int ret = stmt.executeUpdate();
			watch.end();
			return ret;
		} catch (SQLException e) {
			watch.fail(e);
			LOGGER.error(sql + " error: {}", e);
			throw e;
		} finally {
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
//			filter(obKey,watch,execWatch);
		}
	}

	@Override
	public DataTable spExecuteTable(String spName, String[] params, Object... values) throws SQLException {
		params = params == null ? new String[]{} : params;
		values = values == null ? new String[]{} : values;

		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";

		String[] paramsTemp = copyParams(params);// 临时存放参数名称，防止先mysql后sqlserver访问时修改了参数名称出错

//		Stopwatch watch = null;
//		Stopwatch execWatch = null;
		String obKey = "";
		try {
			obKey = dbName + ":" + spName;
//			SmartCounter counter = DatabasePerfmon.getCounter(obKey);
//			watch = counter.begin();

			if (params != null) {
				if (params.length != values.length) {
					throw new IllegalArgumentException("params.length != values.length");
				}
			}
			conn = getConnection();
			//此处开始才真的是执行数据库时间，上面的还包含getConnection的时间
//			SmartCounter execCounter = DatabasePerfmon.getExecCounter(obKey);
//			execWatch = execCounter.begin();


			String dbType = conn.getMetaData().getDatabaseProductName();
//			if (dbType.equalsIgnoreCase("MySQL")) {
//				convertAtToT(paramsTemp, params);
//			}
			sql = DatabaseHelper.getCallSql(spName, values == null ? 0 : values.length);
			stmt = conn.prepareCall(sql);
			if (dbType.equalsIgnoreCase("MySQL")) {
				DatabaseHelper.fillStatement(stmt, paramsTemp, values);
			} else {
				DatabaseHelper.fillStatement(stmt, params, values);
			}
			rs = stmt.executeQuery();
			return new DataTable(rs);
		} catch (SQLException e) {
//			watch.fail(e);
			LOGGER.error(obKey + " error: {}", e);
			//throw new RuntimeException("spExecuteTable:" + spName + ":failed @" + this.dbName, e);
			throw e;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
//			filter(obKey,watch,execWatch);
		}
	}

	@Override
	public List<DataTable> spExecuteTables(String spName, String[] params, Object... values) throws SQLException {

		params = params == null ? new String[]{} : params;
		values = values == null ? new String[]{} : values;

		Connection conn = null;
		CallableStatement stmt = null;
		ResultSet rs = null;
		String sql = "";
		String[] paramsTemp = copyParams(params);// 临时存放参数名称，防止先mysql后sqlserver访问时修改了参数名称出错

//		Stopwatch watch = null;
//		Stopwatch execWatch = null;
		String obKey = "";
		try {
			obKey = dbName + ":" + spName;
//			SmartCounter counter = DatabasePerfmon.getCounter(obKey);
//			watch = counter.begin();

			if (params != null) {
				if (params.length != values.length) {
					throw new IllegalArgumentException("params.length != values.length");
				}
			}
			conn = getConnection();
			//此处开始才真的是执行数据库时间，上面的还包含getConnection的时间
//			SmartCounter execCounter = DatabasePerfmon.getExecCounter(obKey);
//			execWatch = execCounter.begin();

			String dbType = conn.getMetaData().getDatabaseProductName();
			if (dbType.equalsIgnoreCase("MySQL")) {
				// convertAtToT(paramsTemp, params);
			}
			sql = DatabaseHelper.getCallSql(spName, values.length);
			stmt = conn.prepareCall(sql);
			if (dbType.equalsIgnoreCase("MySQL")) {
				DatabaseHelper.fillStatement(stmt, paramsTemp, values);
			} else {
				DatabaseHelper.fillStatement(stmt, params, values);
			}
			rs = stmt.executeQuery();

			List<DataTable> ds = new ArrayList<DataTable>();
			ds.add(new DataTable(rs));

			while (stmt.getMoreResults()) {
				rs = stmt.getResultSet();
				ds.add(new DataTable(rs));
			}
			return ds;
		} catch (SQLException e) {
//			watch.fail(e);
			LOGGER.error(obKey + " error: {}", e);
			throw e;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
//			filter(obKey,watch,execWatch);
		}
	}

	private String[] copyParams(String[] params) {
		String[] paramsTemp = new String[params.length];// 临时存放参数名称，防止先mysql后sqlserver访问时修改了参数名称出错
		for (int i = 0; i < params.length; i++) {
			paramsTemp[i] = params[i];
		}
		return paramsTemp;
	}

	@Override
	public int executeNonQuery(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					stmt.setObject(i + 1, values[i]);
				}
			}
			int r = stmt.executeUpdate();
			watch.end();
			return r;
		} catch (SQLException ex) {
			watch.fail(ex);
			LOGGER.error("executeNonQuery sql='{}' failed:{} ", sql, ex);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
			detectSlowSQL(sql, values, watch);
		}
	}

	public int executeNonQueryForLog(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					stmt.setObject(i + 1, values[i]);
				}
			}
			int r = stmt.executeUpdate();
			watch.end();
			return r;
		} catch (SQLException ex) {
			watch.fail(ex);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
			detectSlowSQL(sql, values, watch);
		}
	}

	@Override
	public DataTable executeUpdate(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					stmt.setObject(i + 1, values[i]);
				}
			}
			stmt.executeUpdate();
			rs = stmt.getResultSet();
			DataTable t = new DataTable(rs);
			watch.end();
			return t;
		} catch (SQLException ex) {
			watch.fail(ex);
			LOGGER.error("executeTable sql='{}' failed: {}", sql);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
		}
	}


	@Override
	public DataTable executeTable(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					stmt.setObject(i + 1, values[i]);
				}
			}
			rs = stmt.executeQuery();
			DataTable t = new DataTable(rs);
			watch.end();
			return t;
		} catch (SQLException ex) {
			watch.fail(ex);
			LOGGER.error("executeTable sql='{}' failed: {}", sql);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
		}
	}

	@Override
	public <E> E executeValue(String sql, Class<E> clazz, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					stmt.setObject(i + 1, values[i]);
				}
			}
			E r = null;
			rs = stmt.executeQuery();
			if (rs != null && rs.next()) {
				if (Integer.class.equals(clazz)) {
					r = (E)new Integer(rs.getInt(1));
				} else if (Long.class.equals(clazz)) {
					r = (E)new Long(rs.getLong(1));
				} else if (Boolean.class.equals(clazz)) {
					r = (E)new Boolean(rs.getBoolean(1));
				} else if (String.class.equals(clazz)) {
					r = (E)rs.getString(1);
				} else if (Date.class.equals(clazz)) {
					r = (E)rs.getDate(1);
				} else if (Byte.class.equals(clazz)) {
					r = (E)new Byte(rs.getByte(1));
				} else if (Short.class.equals(clazz)) {
					r = (E)new Short(rs.getShort(1));
				} else if (Object.class.equals(clazz)) {
					r = (E)rs.getObject(1);
				} else {
					throw new UnsupportedOperationException("Nonsupported type:" + clazz.getName());
				}
			}
			watch.end();
			return r;
		} catch (SQLException ex) {
			watch.fail(ex);
			LOGGER.error("executeValue sql='{}' failed: {}", sql);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
		}
	}

	@Override
	public long executeInsert(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

			if (values != null) {
				for (int i = 0; i < values.length; i++) {
					stmt.setObject(i + 1, values[i]);
				}
			}

			stmt.executeUpdate();
			watch.end();
			rs = stmt.getGeneratedKeys();

			if (rs != null && rs.next()) {
				return rs.getLong(1);
			} else {
				return 0;
			}
		} catch (SQLException ex) {
			LOGGER.error("executeInsertWithAutoColumn sql='{}' failed: {}", sql, ex);
			watch.fail(ex);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			closeConnection(conn);
		}
	}

	/**
	 * 为了实现事务管理，返回Transaction对象，通过Transaction对象实现业务逻辑。
	 *
	 * @return Transaction对象
	 * @throws SQLException
	 */
	public Transaction beginTransaction() throws SQLException {
		Connection conn = getConnection();
		return new TransactionImpl(conn, dbName);
	}

	protected Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	protected void closeConnection(Connection conn) {
		DatabaseHelper.attemptClose(conn);
	}

	private SmartCounter getTxCounter(String sql) {
		String perfTag = DatabaseHelper.extractPerfTag(sql);
		if (!StringUtils.isNullOrEmpty(perfTag)) {
			perfTag = dbName + "." + perfTag;
			DatabaseCounters counters = PerformanceCounterFactory.getCounters(DatabaseCounters.class, perfTag);
			return SmartCounter.combine(counters.getTx(), totalCounter.getTx());
		} else {
			return totalCounter.getTx();
		}
	}

	private void detectSlowSQL(String sql, Object[] values, Stopwatch watch) {
		if (watch.getMillseconds() > SLOW_SQL_ELAPSED) {
			LOGGER.error("Detect slow SQL costMs={} {}.{}", watch.getMillseconds(), dbName, sql);
		}
	}
}
