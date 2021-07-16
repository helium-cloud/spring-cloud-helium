package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 11/13/15.
 */
public class DatabaseNode extends SuperPojo {

	private String jdbcUrl;


	private String driverClass;

	private String user;

	private String password;

	private List<KeyValueNode> properties = new ArrayList<>();

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getDriverClass() {
		return driverClass;
	}

	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<KeyValueNode> getProperties() {
		return properties;
	}

	public void setProperties(List<KeyValueNode> properties) {
		this.properties = properties;
	}
}
