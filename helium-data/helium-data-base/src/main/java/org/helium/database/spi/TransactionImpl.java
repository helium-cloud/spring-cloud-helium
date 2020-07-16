package org.helium.database.spi;
import org.helium.database.DataTable;
import org.helium.database.Transaction;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.SmartCounter;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;

/**
 * Created by Coral on 5/5/15.
 */
class TransactionImpl implements Transaction {
	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionImpl.class);

	private Connection connection;
	private SmartCounter counter;
	private String dbName;

	TransactionImpl(Connection connection, String dbName) {
		this.connection = connection;
		this.dbName = dbName;
		DatabaseCounters counters = PerformanceCounterFactory.getCounters(DatabaseCounters.class, dbName + ".transaction");
		counter = counters.getTx();
	}

	@Override
	public void close() {
		DatabaseHelper.attemptClose(connection);
	}

	@Override
	public void commit() throws SQLException {
		try {
			connection.commit();
		} catch (SQLException ex) {
			LOGGER.error("Transaction Commit failed {}", ex);
			throw ex;
		}
	}

	@Override
	public void rollback() throws SQLException {
		try {
			connection.rollback();
		} catch (SQLException ex) {
			LOGGER.error("Transaction Rollback failed {}", ex);
			throw ex;
		}
	}

	@Override
	public String getName() {
		return dbName;
	}

	@Override
	public String getTableSchema() {
		try {
			return connection.getSchema();
		} catch (SQLException e) {
			LOGGER.error("Transaction getTableSchema failed {}", e);
		}
		return null;
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
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public DataTable spExecuteTable(String spName, String[] params, Object... values) throws SQLException {
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public List<DataTable> spExecuteTables(String spName, String[] params, Object[] values) throws SQLException {
		return null;
	}

	@Override
	public int executeNonQuery(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = this.connection;
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
		}
	}

	@Override
	public int executeNonQueryForLog(String sql, Object... values) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		SmartCounter counter = getTxCounter(sql);
		Stopwatch watch = counter.begin();

		try {
			conn = this.connection;
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
			conn = this.connection;
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
			conn = this.connection;
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
			conn = this.connection;
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
					r = (E)new Integer(rs.getInt(0));
				} else if (Long.class.equals(clazz)) {
					r = (E)new Long(rs.getLong(0));
				} else if (Boolean.class.equals(clazz)) {
					r = (E)new Boolean(rs.getBoolean(0));
				} else if (String.class.equals(clazz)) {
					r = (E)rs.getString(0);
				} else if (Date.class.equals(clazz)) {
					r = (E)rs.getDate(0);
				} else if (Byte.class.equals(clazz)) {
					r = (E)new Byte(rs.getByte(0));
				} else if (Short.class.equals(clazz)) {
					r = (E)new Short(rs.getShort(0));
				} else if (Object.class.equals(clazz)) {
					r = (E)rs.getObject(0);
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
			conn = this.connection;
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
			LOGGER.error("executeInsertWithAutoColumn sql='{}' failed: {}", ex);
			throw ex;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
		}
	}

	@Override
	public Transaction beginTransaction() throws SQLException {
		return null;
	}

	private SmartCounter getTxCounter(String sql) {
		return counter;
	}

	private void detectSlowSQL() {

	}
}
