package org.helium.database;

import com.feinno.superpojo.util.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Created by Coral on 5/5/15.
 */
public class ConnectionString {
	public static final String DRIVER_CLASS = "DriverClass";
	public static final String DRIVER_CLASS_2 = "driverClass";
	public static final String JDBC_URL = "JdbcUrl";
	public static final String JDBC_URL_2 = "jdbcUrl";
	public static final String USER = "User";
	public static final String USER_2 = "user";
	public static final String PASSWORD = "Password";
	public static final String PASSWORD_2 = "password";

	private String driverClassName;
	private String jdbcUrl;
	private String user;
	private String password;
	private Properties properties = new Properties();

	public String getProperty(String key) {
		return (String) properties.get(key);
	}

	public Properties getProperties() {
		return properties;
	}

	public static ConnectionString fromText(String txt) throws IOException {
		Properties props = new Properties();
		props.load(new StringReader(txt));
		return fromProperties(props);
	}

	public static ConnectionString fromProperties(Properties props) {
		return parseInner(props);
	}

	private static ConnectionString parseInner(Properties props) {
		ConnectionString cs = new ConnectionString();
		cs.driverClassName = props.getProperty(DRIVER_CLASS);
		cs.jdbcUrl = props.getProperty(JDBC_URL);
		cs.user = props.getProperty(USER);
		cs.password = props.getProperty(PASSWORD);

		if (StringUtils.isNullOrEmpty(cs.driverClassName)) {
			cs.driverClassName = props.getProperty(DRIVER_CLASS_2);
		}

		if (StringUtils.isNullOrEmpty(cs.jdbcUrl)) {
			cs.jdbcUrl = props.getProperty(JDBC_URL_2);
		}

		if (StringUtils.isNullOrEmpty(cs.user)) {
			cs.user = props.getProperty(USER_2);
		}

		if (StringUtils.isNullOrEmpty(cs.password)) {
			cs.password = props.getProperty(PASSWORD_2);
		}


		for (Map.Entry<Object, Object> e : props.entrySet()) {
			String k = (String) e.getKey();
			if (DRIVER_CLASS.equals(k) || JDBC_URL.equals(k) || USER.equals(k) || PASSWORD.equals(k)) {
				continue;
			}
			cs.properties.put((String) e.getKey(), (String) e.getValue());
		}
		return cs;
	}

	/**
	 * 设置属性
	 *
	 * @param key1
	 * @param key2
	 * @param def
	 * @param setter
	 */
	public void setProp(String key1, String key2, String def, Consumer<String> setter) {
		String v;
		if (!StringUtils.isNullOrEmpty(key1)) {
			v = this.getProperty(key1);
			if (!StringUtils.isNullOrEmpty(v)) {
				setter.accept(v);
				return;
			}
		}

		if (!StringUtils.isNullOrEmpty(key2)) {
			v = this.getProperty(key2);
			if (!StringUtils.isNullOrEmpty(v)) {
				setter.accept(v);
				return;
			}
		}
		if (!StringUtils.isNullOrEmpty(def)) {
			setter.accept(def);
			return;
		}
	}

	public void setPropInt(String key1, String key2, String def, Consumer<Integer> setter) {
		setProp(key1, key2, def, s -> setter.accept(Integer.parseInt(s)));
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * 创建ConnectionString的构造类
	 * Created by Coral on 11/2/15.
	 */
	public static class Builder {
		private Properties props;

		public Builder() {
			props = new Properties();
		}

		public Builder jdbcUrl(String url) {
			props.put(ConnectionString.JDBC_URL, url);
			if (url.contains("mysql")) {
				this.driverClass("com.mysql.jdbc.Driver");
			}
			return this;
		}

		public Builder driverClass(String driverClass) {
			props.put(ConnectionString.DRIVER_CLASS, driverClass);
			return this;
		}

		public Builder user(String user) {
			props.put(ConnectionString.USER, user);
			return this;
		}

		public Builder password(String password) {
			props.put(ConnectionString.PASSWORD, password);
			return this;
		}

		public ConnectionString toConnStr() {
			return ConnectionString.fromProperties(props);
		}
	}

	public static String toConStr(Properties props) {
		StringBuilder sb = new StringBuilder();
		for (String item : props.stringPropertyNames()) {
			sb.append(item).append("=").append(props.getProperty(item)).append("\n");
		}
		return sb.toString();
	}
}
