package org.helium.data.h2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;

/**
 * 对外提供使用
 * @author wuhao
 * @createTime 2021-06-09 09:25:00
 */
public class H2DataSource implements DataSource {

	private static final Logger LOGGER = LoggerFactory.getLogger(H2DataSource.class);

	private H2DataBase h2DataBase;

	public static H2DataSource getInstance() {
		return H2DataSourceHoler.INSTANCE;
	}

	public static Connection getH2Connection() {
		return H2DataSource.getInstance().h2DataBase.getConnection();
	}


	public static class H2DataSourceHoler {
		private static H2DataSource INSTANCE = new H2DataSource();
	}

	private H2DataSource() {
		h2DataBase = new H2DataBase();
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getConnection("", "");
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return h2DataBase.getConnection();
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
