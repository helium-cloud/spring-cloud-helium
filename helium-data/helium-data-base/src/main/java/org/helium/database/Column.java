package org.helium.database;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 数据库中的列格式类型
 *
 * @author Lv.Mingwei
 *
 */
public class Column {
	private static final int INT_DEFAULT_LENGTH = 11;
	private static final int LONG_DEFAULT_LENGTH = 63;

	private String name;
	private ColumnType columnType;
	private String length;
	private boolean isNullable;
	private boolean isAutoIncrement;
	private String defauleValue;

	/**
	 * 为提高安全性,保证每一列的类型与格式相对应，符合数据库的规范要求，因此屏蔽了默认的构造方法
	 */
	private Column() {
	}

	/**
	 * 创建一个varchar类型的列
	 *
	 * @param name
	 *            列名称
	 * @param length
	 *            列长度
	 * @param isNull
	 *            是否为空
	 * @param defauleValue
	 *            默认值
	 * @return
	 */
	public static Column createVarcharColumn(String name, int length, boolean isNull, String defauleValue) {
		Column column = new Column();
		column.name = name;
		column.length = Integer.toString(length);
		column.isNullable = isNull;
		column.columnType = ColumnType.VARCHAR;
		column.defauleValue = defauleValue != null && defauleValue.length() > 0 ? "'" + defauleValue + "'" : null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 创建一个int类型的列，列长度默认为10
	 *
	 * @param name
	 *            列名称
	 * @param isNull
	 *            是否为空
	 * @param defauleValue
	 *            列的默认值
	 * @return
	 */
	public static Column createIntColumn(String name, boolean isNull, Integer defauleValue) {
		Column column = new Column();
		column.name = name;
		column.length = "" + Column.INT_DEFAULT_LENGTH;
		column.isNullable = isNull;
		column.columnType = ColumnType.INT;
		column.defauleValue = defauleValue != null ? String.valueOf(defauleValue) : null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 创建一个具有自增特性的int类型的列,列长度默认是10,默认不能为空<br>
	 * 请注意，创建了此类型字段的表格，此字段默认会成为主键中的一个
	 *
	 * @param name
	 *            列名称
	 * @return
	 */
	public static Column createAutoIncrementIntColumn(String name) {
		Column column = new Column();
		column.name = name;
		column.length = "" + Column.INT_DEFAULT_LENGTH;
		column.isNullable = true;
		column.columnType = ColumnType.INT;
		column.defauleValue = null;
		column.isAutoIncrement = true;
		return column;
	}

	/**
	 * 创建一个具有自增特性的int类型的列,列长度默认是10,默认不能为空<br>
	 * 请注意，创建了此类型字段的表格，此字段默认会成为主键中的一个
	 *
	 * @param name
	 *            列名称
	 * @return
	 */
	public static Column createAutoIncrementLongColumn(String name) {
		Column column = new Column();
		column.name = name;
		column.length = "" + Column.LONG_DEFAULT_LENGTH;
		column.isNullable = false;
		column.columnType = ColumnType.LONG;
		column.defauleValue = null;
		column.isAutoIncrement = true;
		return column;
	}

	/**
	 * 创建一个text类型的列字段
	 *
	 * @param name
	 *            字段名称
	 * @param isNull
	 *            是否为空
	 * @return
	 */
	public static Column createTextColumn(String name, boolean isNull) {
		Column column = new Column();
		column.name = name;
		column.isNullable = isNull;
		column.columnType = ColumnType.TEXT;
		column.defauleValue = null;
		column.length = null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 创建一个日期datime类型的列字段，当列字段有默认值时，会自动选用timestamp,无需调用者担心
	 *
	 * @param name
	 *            列名称
	 * @param isNull
	 *            是否为空
	 * @param defaultValue
	 *            默认内容
	 * @return
	 */
	public static Column createDateTimeColumn(String name, boolean isNull, String defaultValue) {
		Column column = new Column();
		column.name = name;
		column.isNullable = isNull;
		if (defaultValue != null && defaultValue.length() > 0) {
			column.columnType = ColumnType.TIMESTAMP;
			column.isNullable = true;
		} else {
			column.columnType = ColumnType.DATETIME;
		}
		column.defauleValue = defaultValue != null && defaultValue.length() > 0 ? defaultValue : null;
		column.length = null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 显式创建Timestamp列
	 * @param name
	 * @param isNull
	 * @param defaultValue
	 * @return
	 */
	public static Column createTimestampColumn(String name, boolean isNull, String defaultValue) {
		Column column = new Column();
		column.name = name;
		column.columnType = ColumnType.TIMESTAMP;
		column.length = "" + 6;
		column.isNullable = isNull;
		column.defauleValue = defaultValue != null && defaultValue.length() > 0 ? defaultValue : null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 创建一个Long类型的列，根据MYSQL特性，会自动启用BIGINT，默认长度63位
	 *
	 * @param name
	 *            列字段名称
	 * @param isNull
	 *            是否为空
	 * @param defauleValue
	 *            默认值
	 * @return
	 */
	public static Column createLongColumn(String name, boolean isNull, Long defauleValue) {
		Column column = new Column();
		column.name = name;
		column.length = "" + Column.LONG_DEFAULT_LENGTH;
		column.isNullable = isNull;
		column.columnType = ColumnType.LONG;
		column.defauleValue = defauleValue != null ? String.valueOf(defauleValue) : null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 创建一个double类型的列字段
	 *
	 * @param name
	 *            列名称
	 * @param isNull
	 *            是否为空
	 * @param defauleValue
	 *            默认值
	 * @return
	 */
	public static Column createDoubleColumn(String name, boolean isNull, Double defauleValue) {
		Column column = new Column();
		column.name = name;
		column.length = null;
		column.isNullable = isNull;
		column.columnType = ColumnType.DOUBLE;
		column.defauleValue = defauleValue != null ? String.valueOf(defauleValue) : null;
		column.isAutoIncrement = false;
		return column;
	}

	public static Column createDecimalColumn(String name, String length, boolean isNull, Double defauleValue) {
		Column column = new Column();
		column.name = name;
		column.length = length;
		column.isNullable = isNull;
		column.columnType = ColumnType.DECIMAL;
		column.defauleValue = defauleValue != null ? String.valueOf(defauleValue) : null;
		column.isAutoIncrement = false;
		return column;
	}

	/**
	 * 以SQL语句的方式展示一个表结构
	 */
	public String toString() {

		StringBuilder stringBuilder = new StringBuilder();

		// Step 1.拼接字段名及类型
		stringBuilder.append("`").append(this.getName()).append("` ").append(this.getColumnType().getValue());

		// Step 2.拼接类型长度
		if (this.length != null) {
			stringBuilder.append("(").append(this.length).append(")");
		}

		// Step 3.拼接是否为空
		if (!this.isNullable()) {
			stringBuilder.append(" not null");
		}

		// Step 4.拼接默认值
		if (this.defauleValue != null && this.defauleValue.length() > 0) {
			stringBuilder.append((" default " + this.defauleValue));
		}

		// Step 5.拼接是否自增
		if (this.isAutoIncrement) {
			stringBuilder.append(" auto_increment");
		}

		return stringBuilder.toString();
	}

	/**
	 * 覆盖此方法的目的在于进行数据库两张表中的列格式对比
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		if (!(obj instanceof Column)) {
			return false;
		}

		Column column = (Column) obj;

		if (this.name != null) {
			if (!this.name.equalsIgnoreCase(column.name)) {
				return false;
			}
		} else if (column.name != null) {
			return false;
		}

		if (this.columnType != column.columnType) {
			return false;
		}

		if (this.length != null) {
			if (!this.length.equals(column.length)) {
				return false;
			}
		}

		if (this.isNullable != column.isNullable) {
			return false;
		}

		if (this.defauleValue != null) {
			if (!this.defauleValue.equalsIgnoreCase(column.defauleValue)) {
				return false;
			}
		} else if (column.defauleValue != null) {
			return false;
		}

		if (this.isAutoIncrement != column.isAutoIncrement) {
			return false;
		}

		return true;
	}

	/**
	 * 因为{@link Object#hashCode()}方法被覆盖了，为了防止{@link Column}
	 * 被放置在Map中找不到对象，此处也覆盖了{@link Object#hashCode()}方法
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (length != null ? length.hashCode() : 0);
		result = 31 * result + (isNullable ? 1 : 0);
		result = 31 * result + (columnType != null ? columnType.getValue().hashCode() : 0);
		result = 31 * result + (defauleValue != null ? defauleValue.hashCode() : 0);
		result = 31 * result + (isAutoIncrement ? 1 : 0);
		return result;
	}

	/**
	 * 根据结果集对象的当前内容创建一个列
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static Column valueOf(ResultSet rs) throws SQLException {
		int columnType = rs.getInt("DATA_TYPE");
		switch (columnType) {

			case java.sql.Types.INTEGER:
				// 如果是主键,则创建int主键列，否则创建普通的int列
				if (rs.getString("IS_AUTOINCREMENT") != null
						&& rs.getString("IS_AUTOINCREMENT").equalsIgnoreCase("YES")) {
					return Column.createAutoIncrementIntColumn(rs.getString("COLUMN_NAME"));
				} else {
					return Column.createIntColumn(
							rs.getString("COLUMN_NAME"),
							rs.getInt("NULLABLE") == 0 ? false : true,
							rs.getString("COLUMN_DEF") != null && rs.getString("COLUMN_DEF").length() > 0 ? Integer
									.valueOf(rs.getString("COLUMN_DEF")) : null);
				}

			case java.sql.Types.VARCHAR:
				return Column.createVarcharColumn(rs.getString("COLUMN_NAME"), rs.getInt("COLUMN_SIZE"),
						rs.getInt("NULLABLE") == 0 ? false : true, rs.getString("COLUMN_DEF"));

			case java.sql.Types.LONGVARCHAR:
				return Column.createTextColumn(rs.getString("COLUMN_NAME"), rs.getInt("NULLABLE") == 0 ? false : true);

			case java.sql.Types.BIGINT:
				return Column.createLongColumn(
						rs.getString("COLUMN_NAME"),
						rs.getInt("NULLABLE") == 0 ? false : true,
						rs.getString("COLUMN_DEF") != null && rs.getString("COLUMN_DEF").length() > 0 ? Long.valueOf(rs
								.getString("COLUMN_DEF")) : null);

			case java.sql.Types.DOUBLE:
				return Column.createDoubleColumn(
						rs.getString("COLUMN_NAME"),
						rs.getInt("NULLABLE") == 0 ? false : true,
						rs.getString("COLUMN_DEF") != null && rs.getString("COLUMN_DEF").length() > 0 ? Double
								.valueOf(rs.getString("COLUMN_DEF")) : null);

			case java.sql.Types.TIMESTAMP:
				return Column.createDateTimeColumn(rs.getString("COLUMN_NAME"), rs.getInt("NULLABLE") == 0 ? false
						: true, rs.getString("COLUMN_DEF"));

			default:
				break;
		}
		return null;
	}

	public String getName() {
		return name;
	}

	public ColumnType getColumnType() {
		return columnType;
	}

	public boolean isNullable() {
		return isNullable;
	}

	public String getDefauleValue() {
		return defauleValue;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	/**
	 * 为防止Column被其他类破坏规则，此处仅允许Table对他进行修改
	 *
	 * @param isNull
	 */
	public void setNullable(boolean isNull) {
		this.isNullable = isNull;
	}

	/**
	 * 为了保证{@link Column}
	 * 相对于数据库表的约束性是相同的，因此会隐藏部分数据，防止该对象的数据被篡改，但是这样会影响到类的创建者无法看到类中的数据，
	 * 也就无法检查此类是否适合创建数据库表 基于这个原因这里提供了一个方法， 用以在提交时检验此对象是否符合数据库列要求<br>
	 * {@Column }对象是否符合数据库表结构的列要求
	 *
	 * @return
	 */
	public boolean check() {

		if (name == null || name.length() == 0) {
			return false;
		}
		if (columnType == null) {
			return false;
		}

		return true;
	}
}
