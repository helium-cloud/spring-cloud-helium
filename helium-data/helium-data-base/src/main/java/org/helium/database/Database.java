package org.helium.database;

import org.helium.data.DataException;
import org.helium.database.spi.DatabaseFieldLoader;
import org.helium.framework.annotations.FieldLoaderType;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * 用于操作数据库的通用接口
 */
@FieldLoaderType(loaderType = DatabaseFieldLoader.class)
public interface Database {
	/**
	 * 获取数据库名
	 * @return
	 */
	String getName();

	String getTableSchema();

	/**
	 * 执行'select 1'，判定数据库是否可用
	 */
	boolean test();
	
	/**
	 * 执行不返回结果集的存储过程
	 * @param spName 存储过程名
	 * @param params 参数名,如果参数为空，需要传递String类型的空数组，如定义：String[] params = {};然后传递params。
	 * @param values 参数值
	 * @return (1)SQL 数据操作语言 (DML) 语句的行数 (2)对于无返回内容的SQL语句，返回 0
	 * @throws SQLException
	 */
	int spExecuteNonQuery(String spName, String[] params, Object... values) throws SQLException;
	
	/**
	 * 执行一个存储过程, 返回一个DataTable对象
	 * @param spName 存储过程名
	 * @param params 参数名,如果参数为空，需要传递String类型的空数组，如定义：String[] params = {};然后传递params。
	 * @param values 参数值
	 * @return 包含该查询生成的数据的DataTable对象
	 * @throws SQLException
	 */
	DataTable spExecuteTable(String spName, String[] params, Object... values) throws SQLException;

	/**
	 * 返回多张表的情况
	 * @param spName
	 * @param params
	 * @param values
	 * @return
	 * @throws SQLException
	 */
	List<DataTable> spExecuteTables(String spName, String[] params, Object[] values) throws SQLException;

	/**
	 * 执行一个不返回结果的SQL语句
	 * @param sql 可以包含?参数的sql语句
	 * @param values ?对应的参数值
	 * @return (1)SQL 数据操作语言 (DML) 语句的行数 (2)对于无返回内容的SQL语句，返回 0
	 * @throws SQLException
	 */
	int executeNonQuery(String sql, Object... values) throws SQLException;

	/**
	 * 日志专用，删除异常捕获后的logger.ERROR避免死循环
	 * 执行一个不返回结果的SQL语句
	 * @param sql 可以包含?参数的sql语句
	 * @param values ?对应的参数值
	 * @return (1)SQL 数据操作语言 (DML) 语句的行数 (2)对于无返回内容的SQL语句，返回 0
	 * @throws SQLException
	 */
	int executeNonQueryForLog(String sql, Object... values) throws SQLException;

	/**
	 * 执行一次升级, 的指令
	 * @return
	 * @throws DataException
	 */
	DataTable executeUpdate(String sql, Object... values) throws SQLException;
	 /**
	  * 
	  * 执行一个SQL语句, 返回一个DataTable对象
	  * @param sql 可以包含?参数的sql语句
	  * @param values ?对应的参数值
	  * @return 包含该查询生成的数据的DataTable对象
	  * @throws SQLException
	  */	
	DataTable executeTable(String sql, Object... values) throws SQLException;

	/**
	 * 执行一个SQL语句, 返回一个对象, 仅适用于返回第一行, 第一列数据的情况
	 * @param sql
	 * @param values
	 * @return
	 * @throws DataException
	 */
	<E> E executeValue(String sql, Class<E> clazz, Object... values) throws SQLException;

    /**
     * 对一个带有自增长字段的表，执行一条insert语句，并返回自增长的值。
     * @param sql 可以包含?参数的insert语句
     * @param values ?对应的参数值
     * @return 返回自增长字段的值。如果该表不带自增长字段，则返回-1。
     * @throws SQLException
     */
	long executeInsert(String sql, Object... values) throws SQLException;

	/**
	 * use executeInsert instead
	 * @return
	 */
	@Deprecated
	default long executeInsertWithAutoColumn(String sql, Object... values) throws SQLException {
		return executeInsert(sql, values);
	}

	/**
	 * 启动一个Transaction
	 * @return
	 * @throws SQLException
	 */
	Transaction beginTransaction() throws SQLException;

	default DataSource getDataSource(){return null;};
}
