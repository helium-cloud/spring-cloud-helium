package org.helium.database.test;

import org.helium.database.spi.DatabaseHelper;

/**
 * Created by Coral on 10/10/16.
 */
public class DatabaseHelperTest {

	public static void main(String[] args) {
		System.out.println(DatabaseHelper.extractPerfTag("/*perf=insertSqlTask*/select * from UP_User"));
		System.out.println(DatabaseHelper.extractPerfTag("select * from UP_User"));
	}
}
