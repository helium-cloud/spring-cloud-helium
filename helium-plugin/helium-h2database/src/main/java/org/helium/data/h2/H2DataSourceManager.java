package org.helium.data.h2;

import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuhao
 *
 * @createTime 2021-06-09 09:25:00
 */
public class H2DataSourceManager implements DataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DataSourceManager.class);

	private static volatile H2DataSourceManager h2DataSource;
	private static final String URL = "jdbc:h2:~/test";
	private static final String USER = "sa";
	private static final String PWD = "";

	private static final Map<DBType, String> urlMap = new HashMap<>();


	private Connection connection = null;


	public H2DataSourceManager() throws ClassNotFoundException, SQLException {
		urlMap.clear();
		urlMap.put(DBType.DEFAULT, "jdbc:h2:~/test");
		urlMap.put(DBType.MYSQL , "jdbc:h2:~/test");
		// 加载H2 DB的JDBC驱动
		Class.forName("org.h2.Driver");
		//Server.createWebServer("-web", "-webAllowOthers", "-webPort", "18082");
		Server server = new Server();
		server.runTool( "-web", "-webAllowOthers", "-webPort", "18082");
		// 链接数据库，自动在~创建数据库test，得到联接对象 connection
		connection = DriverManager.getConnection(URL, USER, PWD);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection(USER, PWD);
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		if (connection != null && !connection.isClosed()) {
			return connection;
		}
		synchronized (this) {
			connection = DriverManager.getConnection(URL, USER, PWD);
		}
		return null;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getConnection().unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getConnection().isWrapperFor(iface);
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {

	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {

	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}


}
