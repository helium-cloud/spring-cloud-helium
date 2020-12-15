/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-25
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.database;

/**
 * <b>描述：</b>数据库column的类
 * <p>
 * <b>功能：</b>数据库DataCoumn的访问类
 * <p>
 * <b>用法：</b>
 * <pre><code>
 * DataColumn dc = table.getColumn(1); 从1开始，参考：{@link DataTable}
 * System.out.println(dc.getColumnName());
 *  DataColumn dc2 = table.getColumn("UserId");
 *  System.out.println(dc2.getColumnName());
 * </code></pre>
 *
 * @author 黄湘龙 huangxianglong@feinno.com
 */

public class DataColumn {
	// private static final Logger LOGGER = LoggerFactory.getLogger(DataColumn.class);
	private DataTable dataTable;
	private int sqlType;
	private String name;

	/**
	 * 构造函数
	 */
	public DataColumn() {
	}

	/**
	 * 构造函数
	 *
	 * @param dataTable
	 */
	public DataColumn(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	/**
	 * 构造函数
	 *
	 * @param dataTable 所属数据表对象
	 * @param name      列名
	 * @param type      列SQL type
	 */
	public DataColumn(DataTable dataTable, String name, int type) {
		this.dataTable = dataTable;
		this.name = name;
		this.sqlType = type;
	}


	/**
	 * 设置列的名称
	 *
	 * @param name
	 */
	public void setColumnName(String name) {
		this.name = name;
	}

	/**
	 * 获取列的名称
	 *
	 * @return String
	 */
	public String getColumnName() {
		return name;
	}

	/**
	 * 设置列的SQL type
	 *
	 * @param type
	 */
	public void setSqlType(int type) {
		this.sqlType = type;
	}

	/**
	 * 获取列SQL type
	 */
	public int getSqlType() {
		return sqlType;
	}


	/**
	 * 获取列隶属的DataTable
	 *
	 * @return DataTable
	 */
	public DataTable getDataTable() {
		return dataTable;
	}

	/**
	 * 设置隶属数据库
	 *
	 * @param dataTable
	 */
	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}


}
