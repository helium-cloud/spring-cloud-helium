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

	@Test
	public void testCreateTable() throws SQLException {
		Statement createStatement = H2DataSource.getInstance().getConnection().createStatement();
		String createTable = "DROP TABLE h2test_tb IF EXISTS; create table h2test_tb(id integer,name VARCHAR(22) )";
		long f1 = createStatement.executeUpdate(createTable);
		Assert.assertEquals(f1, 0);
	}

	@Test
	public void testInsert() throws SQLException {
		Statement insertStatement = H2DataSource.getInstance().getConnection().createStatement();
		testCreateTable();
		String insertSql = "INSERT INTO h2test_tb VALUES(1,'h2test')";
		long f2 = insertStatement.executeUpdate(insertSql);
		System.out.println("testInsert：" + f2);
		Assert.assertEquals(f2, 1);
	}

	@Test
	public void testSelect() throws SQLException {
		testCreateTable();
		Statement insertStatement = H2DataSource.getInstance().getConnection().createStatement();
		String insertSql = "INSERT INTO h2test_tb VALUES(1,'h2test')";
		insertStatement.executeUpdate(insertSql);
		String selectSql = "select id,name from h2test_tb";
		PreparedStatement prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		ResultSet rs = prepareStatement.executeQuery();
		// 编历结果集
		int id = 0;
		String name = "";
		while (rs.next()) {
			id = rs.getInt(1); // 从1开始
			name = rs.getString(2);
		}
		System.out.println("id:" + id + " name:" + name);
		Assert.assertEquals(name, "h2test");
	}

	@Test
	public void testDelete() throws SQLException {
		testCreateTable();
		Statement insertStatement = H2DataSource.getInstance().getConnection().createStatement();
		String insertSql = "INSERT INTO h2test_tb VALUES(1,'h2test')";
		insertStatement.executeUpdate(insertSql);

		String selectSql = "select id,name from h2test_tb";
		PreparedStatement prepareStatement = H2DataSource.getInstance().getConnection().prepareStatement(selectSql);
		// 发送SQL 返回一个ResultSet
		ResultSet rs = prepareStatement.executeQuery();
		int i = 0;
		while (rs.next()) {
			i++;
		}
		Assert.assertEquals(1, i);

		String deleteSql = "DELETE FROM h2test_tb WHERE id = 1";
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
