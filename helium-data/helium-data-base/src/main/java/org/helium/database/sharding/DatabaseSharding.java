package org.helium.database.sharding;

import org.helium.database.DataTable;
import org.helium.database.Database;
import org.helium.database.Transaction;

import java.sql.SQLException;
import java.util.List;

/** *
 * Created by Coral on 10/12/15.
 */
public class DatabaseSharding implements Database {
	public static final String SHARDING_PLACEHOLDER = "_${SHARDING}";

	private Database db;
	private String shardingName;

	public DatabaseSharding(Database db, String shardingName) {
		this.db = db;
		this.shardingName = shardingName;
	}

	public String getShardingName() {
		return this.shardingName;
	}

	@Override
	public String getName() {
		return db.getName();
	}

	@Override
	public String getTableSchema() {
		return db.getTableSchema();
	}

	@Override
	public boolean test() {
		return false;
	}

	@Override
	public int spExecuteNonQuery(String spName, String[] params, Object... values) throws SQLException {
		throw new UnsupportedOperationException("StoredProc not supported in sharding mode");
	}

	@Override
	public DataTable spExecuteTable(String spName, String[] params, Object... values) throws SQLException {
		throw new UnsupportedOperationException("StoredProc not supported in sharding mode");
	}

	@Override
	public List<DataTable> spExecuteTables(String spName, String[] params, Object[] values) throws SQLException {
		throw new UnsupportedOperationException("StoredProc not supported in sharding mode");
	}

	@Override
	public int executeNonQuery(String sql, Object... values) throws SQLException {
		return db.executeNonQuery(replaceSql(sql), values);
	}

	@Override
	public int executeNonQueryForLog(String sql, Object... values) throws SQLException {
		return db.executeNonQueryForLog(replaceSql(sql), values);
	}

	@Override
	public DataTable executeUpdate(String sql, Object... values) throws SQLException {
		return db.executeUpdate(replaceSql(sql), values);
	}

	@Override
	public DataTable executeTable(String sql, Object... values) throws SQLException {
		return db.executeTable(replaceSql(sql), values);
	}

	@Override
	public <E> E executeValue(String sql, Class<E> clazz, Object... values) throws SQLException {
		return db.executeValue(replaceSql(sql), clazz, values);
	}

	@Override
	public long executeInsert(String sql, Object... values) throws SQLException {
		return db.executeInsert(replaceSql(sql), values);
	}

	@Override
	public long executeInsertWithAutoColumn(String insertSql, Object... values) throws SQLException {
		throw new UnsupportedOperationException("deprecated method");
	}

	@Override
	public Transaction beginTransaction() throws SQLException {
		throw new UnsupportedOperationException("transaction not supported in sharding mode");
	}

	private String replaceSql(String sql) {
		return sql.replace(SHARDING_PLACEHOLDER, "_" + shardingName);
	}
}
