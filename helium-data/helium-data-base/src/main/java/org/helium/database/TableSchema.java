package org.helium.database;

import com.feinno.superpojo.util.StringUtils;
import org.helium.database.spi.DatabaseOperatorImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * <b>描述: </b>此类为数据库"表结构-->JAVA对象的映射"，通过JAVA对象形式表现数据库的表结构<br>
 * 暂时支持<code>varchar、text、int、long、double、datetime、timestamp</code>类型，
 * ,以及设置主键、自增字段、默认值等功能，创建对象后调用<code>toString()</code>方法可将对象转换为SQL语句
 * 此类为了保证数据库表结构的约束性，不对外提供对象成员的修改方法。
 * <p>
 * <b>功能: </b>提供 数据库"表结构-->JAVA对象的映射"
 * <p>
 * <b>用法: </b>此类与{@link DatabaseOperatorImpl}配合使用效果最佳
 *
 * <pre>
 * String tableName = ...
 * Table table = new Table(tableName);
 * table.addColumns(Table.Column.createAutoIncrementIntColumn(&quot;UserId&quot;));
 * table.addColumns(Table.Column.createVarcharColumn(&quot;Name&quot;, 20, true, null));
 * table.addColumns(Table.Column.createIntColumn(&quot;Age&quot;, false, 20));
 * table.addColumns(Table.Column.createDateTimeColumn(&quot;Birthday&quot;, false, &quot;'2000-1-1'&quot;));
 * table.addPrimaryKey(&quot;UserId&quot;);
 * System.out.println(table.toString); // 输出该表的表建表语句
 *
 * DatabaseOperator operator = DatabaseManager.getDatabaseOperator("test", dbConfigs);
 * operator.createTable(table); //创建表
 * Table table = operator.getTable(tableName) //获得表结构
 * </pre>
 * <p>
 *
 * @author Lv.Mingwei
 * @see DatabaseOperatorImpl
 */
public class TableSchema {
	private String tableName;
	private List<Column> columns = null;
	private List<String> primaryKeys = null;
	private String extension = null;
	private String tail = null;

	public TableSchema(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * 此处用于设置扩展的建表语句，该语句会包含在{@link TableSchema#toString()} 方法生成的建表脚本中，<br>
	 * 可以用它加入例如索引等扩展语句
	 *
	 * @param extension
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}


	/**
	 * 设置类似: ENGINE=InnoDB DEFAULT CHARSET=latin1; 的语句
	 * @param tail
	 */
	public void setTail(String tail) {
		this.tail = tail;
	}

	/**
	 * 为表添加列字段
	 *
	 * @param column
	 * @return 当出现以下情况之一时返回<code>false</code><br>
	 *         1. 列字段格式有问题<br>
	 *         2. 当前表中已经存在该名称的字段
	 */
	public boolean addColumn(Column column) {

		// 列字段格式自检未通过，说明格式有问题，因此返回添加失败
		if (column == null || !column.check()) {
			return false;
		}

		if (columns == null) {
			columns = new ArrayList<Column>();
		}

		// 不允许添加列名相同的字段(不区分大小写)
		for (Column columnTemp : columns) {
			if (columnTemp.getName().equalsIgnoreCase(column.getName())) {
				return false;
			}
		}
		return columns.add(column);
	}

	/**
	 * 向{@link TableSchema}中增加主键，增加主键时必须要保证该对象中以存在相应的列
	 *
	 * @param primaryKey
	 * @return 当出现以下情况之一时返回<code>false</code><br>
	 *         1. 如果列字段中不存在以该主键命名的字段 <br>
	 *         2. 如果主键集合中已经存在该主键<br>
	 */
	public boolean addPrimaryKey(String primaryKey) {

		if (columns == null || columns.size() == 0) {
			return false;
		}

		for (Column column : columns) {
			if (column == null || column.getName() == null) {
				continue;
			}
			if (column.getName().equalsIgnoreCase(primaryKey)) {
				if (primaryKeys == null) {
					primaryKeys = new ArrayList<String>();
				}
				if (primaryKeys.contains(primaryKey)) {
					return false;
				}
				// 如果为主键,则当前列不能为空
				column.setNullable(false);
				return primaryKeys.add(primaryKey);
			}
		}
		return false;
	}

	/**
	 * 覆盖的toString()方法,对象的表示方式既为SQL语句
	 */
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("create table `").append(tableName).append("`(");
		// Step1. 拼接列
		if (columns != null && columns.size() != 0) {
			for (Column column : columns) {
				stringBuilder.append(column.toString()).append(",");
				if (column.isAutoIncrement()) {
					// 如果某一列是自增字段,那么此列默认是主键中的一个
					this.addPrimaryKey(column.getName());
				}
			}
		}
		// Step2. 拼接主键
		if (primaryKeys != null && primaryKeys.size() != 0) {
			stringBuilder.append("primary key (");
			for (String primaryKey : primaryKeys) {
				stringBuilder.append("`").append(primaryKey).append("`,");
			}
			stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length()).append(")").append(",");
		}
		if (stringBuilder.charAt(stringBuilder.length() - 1) == ',') {
			stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
		}
		// Step3. 拼接扩展内容，此扩展内容可以包括例如索引等SQL
		if (extension != null && extension.length() > 0) {
			stringBuilder.append(",");
			stringBuilder.append(extension);
		}
		stringBuilder.append(")");
		if (!StringUtils.isNullOrEmpty(tail)) {
			stringBuilder.append(" ").append(tail).append(";");
		}
		return stringBuilder.toString();
	}

	/**
	 * 覆盖equals方法，目的为用于两个数据库表结构的比较
	 */
	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		// 如果指向同一内存地址，不用比对了，直接相同
		if (this == obj) {
			return true;
		}

		if (this.getClass() != obj.getClass()) {
			return false;
		}

		if (!(obj instanceof TableSchema)) {
			return false;
		}

		TableSchema table = (TableSchema) obj;

		if (this.tableName != null) {
			if (!this.tableName.equalsIgnoreCase(table.tableName)) {
				return false;
			}
		} else if (table.tableName != null) {
			return false;
		}

		// 比对列
		if (this.columns != null && table.columns != null) {
			if (this.columns.size() != table.columns.size()) {
				return false;
			} else {
				for (Column column1 : columns) {
					if (column1.isAutoIncrement()) {
						this.addPrimaryKey(column1.getName());
					}
					// 如果有一个不相同的，则返回不同
					if (!table.columns.contains(column1)) {
						return false;
					}
				}
			}
		} else if (this.columns == null && table.columns != null) {
			return false;
		} else if (this.columns != null && table.columns == null) {
			return false;
		}

		// 比对主键
		if (this.primaryKeys != null && table.primaryKeys != null) {
			if (this.primaryKeys.size() != table.primaryKeys.size()) {
				return false;
			} else {
				boolean isSame = false;
				for (String primaryKey1 : primaryKeys) {
					isSame = false;
					for (String primaryKey2 : table.primaryKeys) {
						if (primaryKey1.equals(primaryKey2)) {
							isSame = true;
							break;
						}
					}
					// 在数量相同的前提下，如果有一个不相同的，则返回不同
					if (!isSame) {
						return false;
					}
				}
			}
		} else if (this.primaryKeys == null && table.primaryKeys != null) {
			return false;
		} else if (this.primaryKeys != null && table.primaryKeys == null) {
			return false;
		}

		return true;
	}

	/**
	 * 因为{@link Object#hashCode()}方法被覆盖了，为了防止{@link TableSchema}被放置在Map中找不到对象，此处也覆盖了
	 * {@link Object#hashCode()}方法
	 */
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + (tableName != null ? tableName.hashCode() : 0);
		if (columns != null) {
			for (Column column : columns)
				result = 31 * result + column.hashCode();
		}
		if (primaryKeys != null) {
			for (String primaryKey : primaryKeys)
				result = 31 * result + primaryKey.hashCode();
		}
		return result;
	}

	/**
	 * 将{@link ResultSet}类型转换为{@link TableSchema}类型的表结构对象
	 *
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static TableSchema valueOf(ResultSet rs) throws SQLException {
		if (rs == null) {
			return null;
		}
		TableSchema table = null;
		while (rs.next()) {
			if (table == null) {
				table = new TableSchema(rs.getString("TABLE_NAME"));
			}
			table.addColumn(Column.valueOf(rs));
		}

		return table;
	}

	/**
	 * 为了保证{@link TableSchema }
	 * 相对于数据库表的约束性是相同的，因此会隐藏部分数据，防止该对象的数据被篡改，但是这样会影响到类的创建者无法看到类中的数据，
	 * 也就无法检查此类是否适合创建数据库表 基于这个原因这里提供了一个方法， 用以在提交时检验此对象是否符合数据库表格要求<br>
	 * {@Table }对象是否符合数据库表结构的要求
	 *
	 * @return
	 */
	public boolean check() {
		// 验证表格名称是否存在
		if (tableName == null || tableName.length() == 0) {
			return false;
		}
		// 验证列是否存在
		if (columns == null || columns.size() == 0) {
			return false;
		}
		// 验证列的格式是否正确
		for (Column column : columns) {
			if (column == null || !column.check()) {
				return false;
			}
		}
		// 如果存在主键，则主键名称一定要是一个列的名字
		if (primaryKeys != null) {
			for (String primaryKey : primaryKeys) {
				boolean isSuccess = false;
				for (Column column : columns) {
					if (column.getName().equalsIgnoreCase(primaryKey)) {
						isSuccess = true;
						break;
					}
				}
				if (!isSuccess) {
					return false;
				}
			}
		}
		return true;
	}
}
