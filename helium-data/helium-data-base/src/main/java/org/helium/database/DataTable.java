/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-24
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.database;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * <b>描述：</b> 数据库表<br/>
 * <p>
 * <b>功能：</b> 提供对表内的列和行的访问
 * <p>
 * <b>用法：</b>
 * 
 * 注意：对于列的访问，序号从1开始，比如访问第一行的第一列：myTable.getRow(0).getInt(1);</br>
 * 根据名称获取列序号也是一样的，获取的都是从1开始的编号；</br>
 * 这么做是为了和JDBC保持一致</br> 
 * 
 * @author 黄湘龙 huangxianglong@feinno.com
 */
public class DataTable {
	private static final DataColumn[] EMPTY_COLUMNS = new DataColumn[0];
	private static final Map<String, Integer> EMPTY_COLUMN_NAME_INDEX = Collections.unmodifiableMap(new HashMap<String, Integer>(1));
	private static final List<DataRow> EMPTY_ROWS = Collections.unmodifiableList(new ArrayList<DataRow>(1)); 
	private String name;
	private DataColumn[] columns;
	private Map<String, Integer> columnsNameIndex;
	private List<DataRow> rows;

	public DataTable(ResultSet rs) throws SQLException {

		if(rs==null || rs.getMetaData().getColumnCount() == 0) {
			this.name="";
			this.columns = EMPTY_COLUMNS;
			this.columnsNameIndex = EMPTY_COLUMN_NAME_INDEX;
			this.rows = EMPTY_ROWS;
		} else {
			ResultSetMetaData meta = rs.getMetaData();
			rows=new ArrayList();
			name=meta.getTableName(1);
			columns =new DataColumn[meta.getColumnCount()];
			columnsNameIndex = new HashMap<String, Integer>(meta.getColumnCount());
			for(int i=1;i<=meta.getColumnCount();i++){
				DataColumn dataColumn=new DataColumn(this,meta.getColumnLabel(i),meta.getColumnType(i));
				columns[i-1]=dataColumn;
				columnsNameIndex.put(meta.getColumnLabel(i), i);
			}
			while (rs.next()) {
				rows.add(new DataRow(this, rs));
			}
		}
	}
	
	
	/**
	 * 获取表名
	 * @return String
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * 设置表名
	 * @param name
	 */
	public void setName(String name){
		this.name=name;
	}

	/**
	 * 获取列数量
	 * @return int
	 */
	public int getColumnCount() {
		return columns.length;

	}

	/**
	 * 获取指定列
	 * @param columnIndex 列序号，第一列为1，第二列为2
	 * @return DataColumn
	 */
	public DataColumn getColumn(int columnIndex) {
		return columns[columnIndex-1];
	}
	
	/**
	 * 返回列集合
	 * @return DataColumn数组
	 */
	public DataColumn[] getColumns(){
		return columns;
	}

	/**
	 * 返回是否含有指定列
	 * @param columnName 列名
	 * @return boolean
	 */
	public boolean isContainColumn(String columnName) {
		return columnsNameIndex.containsKey(columnName);
	}

	/**
	 * 根据名字获取列
	 * @param columnName
	 * @return	如果没有找到，返回null
	 */
	public DataColumn getColumn(String columnName) {
		if(columnsNameIndex.containsKey(columnName))
			return columns[columnsNameIndex.get(columnName)];
		else
			return null;
	}
	
	/**
	 * 获取列的序号
	 * @param columnName
	 * @return Integer
	 */
	public Integer getColumnIndex(String columnName){
		return columnsNameIndex.get(columnName);
	}

	/**
	 * 返回记录的数量
	 * @return int
	 */
	public int getRowCount() {
		return rows.size();
	}

	/**
	 * 获取指定记录
	 * @param rowIndex
	 * @return DataRow
	 */
	public DataRow getRow(int rowIndex) {
		return rows.get(rowIndex);
	}

	/**
	 * 获取所有记录
	 * @return ArrayList
	 */
	public List<DataRow> getRows() {
		return rows;
	}
	
	/**
	 * @return 如果有结果集，返回true；否则返回false
	 */
	public boolean hasResultSet() {
		return this.columns != EMPTY_COLUMNS; 
	}
}
