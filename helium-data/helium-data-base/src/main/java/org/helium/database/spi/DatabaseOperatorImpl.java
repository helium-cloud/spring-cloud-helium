package org.helium.database.spi;

import org.helium.database.DatabaseOperator;
import org.helium.database.TableSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * <b>描述: </b>与数据库进行交互的类，主要为{@link TableSchema}提供和数据库进行操作的协助类
 * <p>
 * <b>功能: </b>为{@link TableSchema}提供和数据库进行操作的协助类
 * <p>
 * <b>用法: </b>此类与{@link TableSchema}配合使用效果最佳
 *
 * <pre>
 * String tableName = ...
 * DatabaseOperator operator = new DatabaseOperator(db); // 创建实例
 *
 * Table table = new Table(tableName);
 * table.addColumns...
 * operator.createTable(table); //创建表
 * Table table = operator.getTable(tableName) //获得表结构
 *
 * </pre>
 * <p>
 *
 * @author Lv.Mingwei
 *
 */
public class DatabaseOperatorImpl extends DatabaseImpl implements DatabaseOperator {
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseOperatorImpl.class);

	DatabaseOperatorImpl(String dbName, DataSource ds) {
		super(dbName, ds);
	}

	@Override
	public String getName() {
		return super.getName();
	}

	/**
	 * 根据表结构创建数据库表
	 *
	 * @param table
	 * @return
	 * @throws SQLException
	 *             数据库访问错误
	 */
	@Override
	public boolean createTable(TableSchema table) throws SQLException {

		if (table == null || !table.check()) {
			return false;
		}

		// 判断是否存在相同的表名
		if (isTableExists(table.getTableName())) {
			throw new SQLException(String.format("TableExist: %s ", table.getTableName()));
		}
			// 不存在相同的表名,则创建此表
		String sql = table.toString();
		return execute(sql);
	}

	/**
	 * @return
	 * @throws SQLException
	 */
	@Override
	public boolean dropRedundantTables(int type) throws SQLException {
		if(type == 0)
			return dropRedundantTables("LogDB", "LOG_%");
		else if (type == 1)
			return dropRedundantPerfmonTables();
		return false;
	}

	private boolean dropRedundantTables(String dbname, String tablePreName){
		LOGGER.info("begin drop tables.");
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement dropStmt = null;
		ResultSet rs = null;
		String showTablesSql = String.format("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '%s' AND TABLE_NAME LIKE '%s' ORDER BY TABLE_NAME DESC limit 7,1000", dbname, tablePreName);
		try {
			// TODO 注意，此处使用的TABLE_SCHEMA未默认的test，切记需要修改
			conn = getConnection();
			stmt = conn.prepareStatement(showTablesSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				String logTableName = rs.getString("TABLE_NAME");
				dropStmt = conn.prepareStatement("DROP TABLE `" + logTableName + "`");
				dropStmt.execute();
				dropStmt.close();
				LOGGER.info("drop table complete. table:" + logTableName);
			}
			LOGGER.info("drop table complete.");
			return true;
		}catch (Exception ex){
			LOGGER.error("drop table failed.", ex);
			return false;
		}finally{
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			DatabaseHelper.attemptClose(dropStmt);
			DatabaseHelper.attemptClose(conn);
		}
	}
	private boolean dropRedundantPerfmonTables(){
		List<String> perfmonTablePreName = getPerfmonTablesPreName();
		for (String tablePreName :perfmonTablePreName) {
			dropRedundantTables("MonDB", tablePreName + "%");
		}
		return true;
	}

	private List<String> getPerfmonTablesPreName(){
		LOGGER.info("begin get perfmon tables.");
		List<String> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String showTablesSql = "SELECT left(TABLE_NAME, length(TABLE_NAME)-8) as NAME_PRE FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'MonDB' and TABLE_NAME LIKE 'PERFMON_%'group by NAME_PRE ";
		try {
			// TODO 注意，此处使用的TABLE_SCHEMA未默认的test，切记需要修改
			conn = getConnection();
			stmt = conn.prepareStatement(showTablesSql);
			rs = stmt.executeQuery();
			while(rs.next()){
				String tablePreName = rs.getString("NAME_PRE");
				result.add(tablePreName);
				LOGGER.info("find perfmon table, pre name:" + tablePreName);
			}
			return result;
		}catch (Exception ex){
			LOGGER.error("drop perfmon table failed.", ex);
			return null;
		}finally{
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			DatabaseHelper.attemptClose(conn);
		}
	}

	/**
	 * 根据表名判断表是否存在
	 *
	 * @param tableName
	 * @return
	 * @throws SQLException
	 *             数据库访问错误
	 */
	@Override
	public boolean isTableExists(String tableName) throws SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;

		String sql = "select `TABLE_NAME` from `INFORMATION_SCHEMA`.`TABLES` where `TABLE_SCHEMA`=? and `TABLE_NAME`=?";
		try {
			// TODO 注意，此处使用的TABLE_SCHEMA未默认的test，切记需要修改
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, getTableSchema());
			stmt.setString(2, tableName);
			rs = (ResultSet) stmt.executeQuery();
			boolean isExist = rs.next();
			return isExist;
		} finally {
			DatabaseHelper.attemptClose(rs);
			DatabaseHelper.attemptClose(stmt);
			DatabaseHelper.attemptClose(conn);
		}
	}

	/**
	 * 根据表名获得数据库表结构
	 *
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	@Override
	public TableSchema getTableSchema(String tableName) throws SQLException {
		if (tableName == null || tableName.indexOf("'") != -1 || !isTableExists(tableName)) {
			return null;
		}
		TableSchema table = null;
		Connection conn = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		try {
			conn = getConnection();

			// 取列内容
			rs1 = conn.getMetaData().getColumns(getTableSchema(), null, tableName, null);
			table = TableSchema.valueOf(rs1);

			if (table == null) {
				return null;
			}

			// 如果有主键，则在此处添加主键到Table对象中
			rs2 = conn.getMetaData().getPrimaryKeys(null, null, tableName);
			while (rs2.next()) {
				table.addPrimaryKey(rs2.getObject(4).toString());
			}
		} catch (SQLException e) {
			// 数据库访问错误向上抛出异常，告诉调用者本地出错，返回数据可能有异常，需处理
			throw e;
		} finally {
			DatabaseHelper.attemptClose(rs1);
			DatabaseHelper.attemptClose(rs2);
			DatabaseHelper.attemptClose(conn);
		}
		return table;
	}

	/**
	 * 将表名修改为...
	 *
	 * @param srcName
	 *            原始表表名
	 * @param descName
	 *            修改后的表名
	 * @return
	 * @throws SQLException
	 */
	@Override
	public boolean renameTable(String srcName, String descName) throws SQLException {
		if (srcName == null || descName == null || srcName.indexOf("'") != -1 || descName.indexOf("'") != -1) {
			return false;
		}
		//
		// 目标表名已存在,抛出异常
		if (isTableExists(descName)) {
			throw new SQLException(String.format("TableExists: %s", descName));
		}
		//
		// 当源表存在时，才可以进行更名，否则更名失败
		if (isTableExists(srcName)) {
			return execute(String.format("ALTER  TABLE `%s` RENAME TO `%s`", srcName, descName));
		} else {
			throw new SQLException(String.format("TableNotExists: %s", srcName));
		}
	}

	/**
	 * 为此类提供的SQL语句执行方法
	 *
	 * @param sql
	 * @return
	 */
	@Override
	public boolean execute(String sql) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = getConnection();
			stmt = conn.prepareStatement(sql);
			stmt.execute();
			return true;
		} catch (Exception e) {
			LOGGER.error("sql='{}' {}", sql, e);
		} finally {
			try {
				DatabaseHelper.attemptClose(stmt);
				DatabaseHelper.attemptClose(conn);
			} catch (Exception ex) {
				LOGGER.error("{}", ex);
			}
		}
		return false;
	}
}
