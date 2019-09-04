package org.helium.perfmon.observation;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

public class ObserverReportRowBean extends SuperPojo {
	@Field(id = 1)
	private String instance;
	@Field(id = 2)
	private String[] data;

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}
}
