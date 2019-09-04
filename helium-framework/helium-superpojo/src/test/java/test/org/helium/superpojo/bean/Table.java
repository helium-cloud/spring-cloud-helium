package test.org.helium.superpojo.bean;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

public class Table extends SuperPojo {

	@Field(id = 1)
	private String name;

	@Field(id = 2)
	private int id;

	@Field(id = 3)
	private String email;

	@Field(id = 4)
	private User user;

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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
