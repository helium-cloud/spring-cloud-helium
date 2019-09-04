package org.helium.sample.bootstrap.quickstart;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import org.helium.framework.task.DedicatedTaskArgs;

/**
 * 
 * Created by Coral on 6/15/17.
 */
public class SampleUser extends SuperPojo implements DedicatedTaskArgs {
	@Field(id = 1)
	private int id;
	@Field(id = 2)
	private String name;
	@Field(id = 3)
	private String role;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@Override
	public String getTag() {
		return Integer.toString(id);
	}
}
