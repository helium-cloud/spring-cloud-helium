package org.helium.data.h2;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wuhao
 * @createTime 2021-06-11 11:15:00
 */
public class H2TableSimple {
	private static final String CREATE_TABLE = "DROP TABLE h2test_tb IF EXISTS; create table h2test_tb(id integer,name VARCHAR(22) )";
	private static final String INSERT_SQL = "INSERT INTO h2test_tb VALUES(%s,'%s')";
	private static final String SELECT_SQL = "SELECT id,name from h2test_tb where id = %s";
	private static final String DELETE_SQL = "DELETE FROM h2test_tb WHERE id = %s";

	public void testCreateTable() throws SQLException {
		Statement createStatement = H2DataSource.getH2Connection().createStatement();
		createStatement.executeUpdate(CREATE_TABLE);
	}

	public void testSelect() throws SQLException {
		int i = 1;
		String value = "h2test";
		testCreateTable();
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		String insertSql = String.format(INSERT_SQL, i, value);
		insertStatement.executeUpdate(insertSql);
		String selectSql = String.format(SELECT_SQL, i);
		PreparedStatement prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		ResultSet rs = prepareStatement.executeQuery();
		// 编历结果集
		int id = 0;
		String name = "";
		while (rs.next()) {
			id = rs.getInt(1); // 从1开始
			name = rs.getString(2);
			System.out.println("id:" + id + " name:" + name);
		}

	}

	public void testDelete() throws SQLException {
		int i = 1;
		String value = "h2test";
		testCreateTable();
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		String insertSql = String.format(INSERT_SQL, i, value);
		insertStatement.executeUpdate(insertSql);
		String selectSql = String.format(SELECT_SQL, i);
		PreparedStatement prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		ResultSet rs = prepareStatement.executeQuery();
		// 编历结果集
		int id = 0;
		String name = "";
		while (rs.next()) {
			id = rs.getInt(1); // 从1开始
			name = rs.getString(2);
			System.out.println("id:" + id + " name:" + name);
		}

		String deleteSql = String.format(DELETE_SQL, i);
		insertStatement.executeUpdate(deleteSql);
		prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		rs = prepareStatement.executeQuery();
		int j = 0;
		while (rs.next()) {
			j++;
		}
		System.out.println("search ret size:" + j);

	}

	public static void main(String[] args) throws SQLException {
		H2TableSimple h2TableSimple = new H2TableSimple();
		h2TableSimple.testSelect();
	}
}
