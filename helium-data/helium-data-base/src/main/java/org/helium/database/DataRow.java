/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-25
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <b>描述：</b>数据行
 * <p>
 * <b>功能：</b>数据行的使用类
 * <p>
 * <b>用法：</b>
 * <p>
 * 根据字段序号（从1开是的序号）或者字段名访问行中的字段<br/>
 * 提供int，boolean等常规数据类型的访问，<br/>
 * 对于非常规数据类型请自己调用获取getObject()方法后拆包<br/>
 * <p>
 * <pre><code>
 * DataRow dr = table.getRow(0); 参考{@link DataTable}
 * int userId = dr.getInt(1);
 * 。。。
 * </code></pre>
 *
 * @author 黄湘龙 huangxianglong@feinno.com
 */
public class DataRow {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataRow.class);

	private int length;
	private DataTable dataTable;
	private Object[] values;

	/**
	 * 构造函数，使用DataTable，ResultSet初始化本对象
	 *
	 * @param dataTable
	 * @param rs
	 */
	public DataRow(DataTable dataTable, ResultSet rs) {

		this.dataTable = dataTable;
		length = dataTable.getColumnCount();
		values = new Object[length];

		// 读取所有值
		try {
			int internalIndex;

			for (int i = 1; i <= values.length; i++) {
				internalIndex = i - 1;
				values[internalIndex] = rs.getObject(i);
			}
		} catch (SQLException e) {
			LOGGER.error(String.format("初始化DataRow出错：DataTable:%s", dataTable.getName()), e);
		}
	}

	/**
	 * 判定指定列是否为SQL NULL
	 *
	 * @param columnIndex 列序号
	 * @return boolean
	 */
	public boolean isNull(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		return values[columnIndex] == null;
	}

	/**
	 * 根据列名查找列序号
	 *
	 * @param columnName 列名
	 * @throws SQLException
	 * @return 列序号，如果没有找到，返回-1
	 */
	public int findColumn(String columnName) throws SQLException {
		Integer index = dataTable.getColumnIndex(columnName);
		return index == null ? -1 : index;
	}

	/**
	 * 获取指定行的原始对象
	 *
	 * @param columnIndex 列序号
	 * @throws SQLException
	 * @return 如果本列为SQL NULL，返回null
	 */
	public Object getObject(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		return values[internalColumnIndex];
	}


	/**
	 * 得到指定列String类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
	public String getString(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		return (String) values[internalColumnIndex];
	}

	/**
	 * 得到指定列Boolean类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回false
	 * @throws SQLException
	 */
	public Boolean getBoolean(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		//return getByte(columnIndex).byteValue() == 1;
		return Boolean.valueOf(values[internalColumnIndex].toString());
	}

	/**
	 * 得到指定列byte类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Byte getByte(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? 0 : Byte.valueOf(oo.toString());
	}

	/**
	 * 得到指定列short类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Short getShort(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? 0 : Short.valueOf(oo.toString());
	}

	/**
	 * 获取指定列的int类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Integer getInt(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? 0 : Integer.parseInt(oo.toString());
	}

	/**
	 * 获取指定列的long类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Long getLong(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? 0 : Long.parseLong(oo.toString());
	}

	/**
	 * 获取指定列的java.math.BigInteger类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public BigInteger getBigInteger(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? BigInteger.valueOf(0) : new BigInteger(oo.toString());
	}

	/**
	 * 获取指定列的float类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Float getFloat(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? 0 : Float.parseFloat(oo.toString());
	}

	/**
	 * 获取指定列的double类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Double getDouble(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		Object oo = values[internalColumnIndex];
		return oo == null ? 0 : Double.parseDouble(oo.toString());
	}

	/**
	 * 获取指定列的byte[]类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		return (byte[]) values[internalColumnIndex];
	}

//	/**
//	 * 获取指定列的java.sql.Date类型的值
//	 * 
//	 * @param columnIndex
//	 *            列序号
//	 * @return 如果本列为SQL NULL，返回null
//	 * @throws SQLException
//	 */
//	public java.sql.Date getDate(int columnIndex) throws SQLException {
//		checkColumnIndexArg(columnIndex);
//		int internalColumnIndex=columnIndex-1;
//		return (java.sql.Date)values[internalColumnIndex];
//	}
//
//	/**
//	 * 获取指定列的java.sql.Time类型的值
//	 * 
//	 * @param columnIndex
//	 *            列序号
//	 * @return 如果本列为SQL NULL，返回null
//	 * @throws SQLException
//	 */
//	public java.sql.Time getTime(int columnIndex) throws SQLException {
//		checkColumnIndexArg(columnIndex);
//		int internalColumnIndex=columnIndex-1;
//		return (java.sql.Time)values[internalColumnIndex];
//	}

	public java.util.Date getDateTime(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		return (java.util.Date) values[internalColumnIndex];
	}

	/**
	 * 获取指定列的java.sql.Timestamp类型的值
	 *
	 * @param columnIndex 列序号
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
	public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
		checkColumnIndexArg(columnIndex);
		int internalColumnIndex = columnIndex - 1;
		return (java.sql.Timestamp) values[internalColumnIndex];
	}


	/**
	 * 获取指定行的原始对象
	 *
	 * @param columnName 列名
	 * @throws SQLException
	 * @return 如果本列为SQL NULL，返回null
	 */
	public Object getObject(String columnName) throws SQLException {
		return getObject(findColumn(columnName));
	}

	/**
	 * 得到指定列String类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
	public String getString(String columnName) throws SQLException {
		return getString(findColumn(columnName));
	}

	/**
	 * 得到指定列Boolean类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回false
	 * @throws SQLException
	 */
	public Boolean getBoolean(String columnName) throws SQLException {
		return getBoolean(findColumn(columnName));
	}

	/**
	 * 得到指定列byte类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Byte getByte(String columnName) throws SQLException {
		return getByte(findColumn(columnName));
	}

	/**
	 * 得到指定列short类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Short getShort(String columnName) throws SQLException {
		return getShort(findColumn(columnName));
	}

	/**
	 * 获取指定列的int类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Integer getInt(String columnName) throws SQLException {
		return getInt(findColumn(columnName));
	}

	/**
	 * 获取指定列的long类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Long getLong(String columnName) throws SQLException {
		return getLong(findColumn(columnName));
	}

	/**
	 * 获取指定列的float类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Float getFloat(String columnName) throws SQLException {
		return getFloat(findColumn(columnName));
	}

	/**
	 * 获取指定列的double类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回0
	 * @throws SQLException
	 */
	public Double getDouble(String columnName) throws SQLException {
		return getDouble(findColumn(columnName));
	}

	/**
	 * 获取指定列的byte[]类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return getBytes(findColumn(columnName));
	}

	/**
	 * 获取指定列的java.sql.Date类型的值
	 *
	 * @param columnName
	 *            列名
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
//	public java.sql.Date getDate(String columnName) throws SQLException {
//		return getDate(findColumn(columnName));
//	}

	/**
	 * 获取指定列的java.sql.Time类型的值
	 *
	 * @param columnName
	 *            列名
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
//	public java.sql.Time getTime(String columnName) throws SQLException {
//		return getTime(findColumn(columnName));
//	}

	/**
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	public java.util.Date getDateTime(String columnName) throws SQLException {
		return getDateTime(findColumn(columnName));
	}

	public BigInteger getBigInteger(String columnName) throws SQLException {
		return getBigInteger(findColumn(columnName));
	}

	/**
	 * 获取指定列的java.sql.Timestamp类型的值
	 *
	 * @param columnName 列名
	 * @return 如果本列为SQL NULL，返回null
	 * @throws SQLException
	 */
	public java.sql.Timestamp getTimestamp(String columnName) throws SQLException {
		return getTimestamp(findColumn(columnName));
	}


	private void checkColumnIndexArg(int columnIndex) throws SQLException {
		if (columnIndex > length || columnIndex < 0) {
			LOGGER.error("参数错误，请求列的序号超过界限");
			throw new SQLException("参数错误，请求列的序号超过界限");
		}
	}


}
