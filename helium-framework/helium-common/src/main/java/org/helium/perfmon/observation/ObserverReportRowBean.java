package org.helium.perfmon.observation;


import org.helium.superpojo.SuperPojo;

public class ObserverReportRowBean extends SuperPojo {
	private String instance;
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
