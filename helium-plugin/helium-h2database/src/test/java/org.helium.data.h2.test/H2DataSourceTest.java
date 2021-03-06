package org.helium.data.h2.test;

import org.helium.data.h2.H2DataSource;
import org.junit.Assert;
import org.junit.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author wuhao
 * @createTime 2021-06-09 09:41:00
 */
public class H2DataSourceTest {
	private static final String CREATE_TABLE = "DROP TABLE h2test_tb IF EXISTS; create table h2test_tb(id integer,name VARCHAR(22) )";
	private static final String INSERT_SQL = "INSERT INTO h2test_tb VALUES(%s,'%s')";
	private static final String SELECT_SQL = "SELECT id,name from h2test_tb where id = %s";
	private static final String DELETE_SQL = "DELETE FROM h2test_tb WHERE id = %s";

	@Test
	public void testCreateTable() throws SQLException {
		Statement createStatement = H2DataSource.getH2Connection().createStatement();
		long f1 = createStatement.executeUpdate(CREATE_TABLE);
		Assert.assertEquals(f1, 0);
	}

	@Test
	public void testInsert() throws SQLException {
		int i = 1;
		String value = "h2test";
		testCreateTable();
		Statement insertStatement = H2DataSource.getH2Connection().createStatement();
		String insertSql = String.format(INSERT_SQL, i, value);
		long f2 = insertStatement.executeUpdate(insertSql);
		System.out.println("testInsert：" + f2);
		Assert.assertEquals(f2, 1);
	}

	@Test
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
		Assert.assertEquals(value, name);
	}

	@Test
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
		Assert.assertEquals(0, j);

	}

}
