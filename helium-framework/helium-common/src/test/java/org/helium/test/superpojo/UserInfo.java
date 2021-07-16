package org.helium.test.superpojo;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
	private String test;
	private List<String> list = new ArrayList<>();

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
}
