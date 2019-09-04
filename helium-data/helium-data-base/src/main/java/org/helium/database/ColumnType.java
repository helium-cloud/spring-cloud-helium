package org.helium.database;

/**
 * 数据库字段类型<br>
 * 以后如果添加一种全新的类型时需要进行以下三步：<br>
 * 1.在此枚举处增加想要的类型<br>
 * 2.在<code>Column</code>类中加一个此类型的静态构造方法<br>
 * 3.在<code>Column</code>类中的valueOf()方法中增加一个此类型由数据库<code>ResultSet</code>
 * 对象如何转为Column对象的代码段
 *
 * @author Lv.Mingwei
 *
 */
public enum ColumnType {
	INT("int"),
	LONG("bigint"),
	DOUBLE("double"),
	DECIMAL("decimal"),
	DATETIME("datetime"),
	TIMESTAMP("timestamp"),
	VARCHAR("varchar"),
	TEXT("text");

	private String value;

	private ColumnType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
