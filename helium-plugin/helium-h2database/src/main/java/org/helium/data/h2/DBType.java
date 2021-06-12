package org.helium.data.h2;

/**
 * Compatibility modes for IBM DB2, Apache Derby, HSQLDB, MS SQL Server,
 * MySQL, Oracle, and PostgreSQL.
 *
 * @author wuhao
 * @createTime 2021-06-10 18:52:00
 */
public enum DBType {
	MySQL("MySQL"), Oracle("Oracle"), PostgreSQL("PostgreSQL"),
	DB2("DB2"), Derby("Derby"), HSQLDB("HSQLDB"), MSSQLServer("MSSQLServer");

	private String value;

	DBType(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}
}
