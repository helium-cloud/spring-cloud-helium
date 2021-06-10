package org.helium.data.h2.test;

import java.sql.*;
import java.util.*;
public class Test {
	public static void main(String[] args) throws Exception {
		String url = "jdbc:h2:~/test;MODE=MYSQL";
		Properties prop = new Properties();
		prop.setProperty("user", "sa");
		prop.put("password", "123456");
		Connection conn = null;
		conn = DriverManager.getConnection(url, prop);
		conn.close();
	}
}