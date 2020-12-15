package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Childs;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 11/13/15.
 */
public class DatabaseNode extends SuperPojo {
	@Field(id = 1, name = "jdbcUrl", type = NodeType.NODE)
	private String jdbcUrl;

	@Field(id = 2, name = "driverClass", type = NodeType.NODE)
	private String driverClass;

	@Field(id = 3, name = "user", type = NodeType.NODE)
	private String user;

	@Field(id = 4, name = "password", type = NodeType.NODE)
	private String password;

	@Childs(id = 11, child = "property", parent = "properties")
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
