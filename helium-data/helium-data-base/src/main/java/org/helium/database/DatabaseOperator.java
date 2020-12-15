package org.helium.database;

import org.helium.database.spi.DatabaseOperatorLoader;
import org.helium.framework.annotations.FieldLoaderType;

import java.sql.SQLException;

/**
 * 从MondbHelper中重构，具备数据库的初级建表与操作功能
 * <p>
 * Created by Coral on 11/2/15.
 *
 * @Author Lv.Mingwei
 */
@FieldLoaderType(loaderType = DatabaseOperatorLoader.class)
public interface DatabaseOperator {
	/**
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	TableSchema getTableSchema(String tableName) throws SQLException;

	/**
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	boolean isTableExists(String tableName) throws SQLException;

	/**
	 * @param table
	 * @return
	 * @throws SQLException
	 */
	boolean createTable(TableSchema table) throws SQLException;


	/**
	 * @param type 0 log,1 Perfmon
	 * @return
	 * @throws SQLException
	 */
	boolean dropRedundantTables(int type) throws SQLException;

	/**
	 * @param srcName
	 * @param descName
	 * @return
	 * @throws SQLException
	 */
	boolean renameTable(String srcName, String descName) throws SQLException;

	/**
	 * @param sql
	 * @return
	 */
	boolean execute(String sql);
}
