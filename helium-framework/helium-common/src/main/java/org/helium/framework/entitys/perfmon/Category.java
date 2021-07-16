package org.helium.framework.entitys.perfmon;

import org.helium.superpojo.SuperPojo;

import java.util.List;

/**
 * Created by Coral on 2015/8/17.
 */
public class Category extends SuperPojo {

	String name;

	int instance;

	List<ReportColumn> columns;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getInstance() {
		return instance;
	}

	public void setInstance(int instance) {
		this.instance = instance;
	}

	public List<ReportColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ReportColumn> columns) {
		this.columns = columns;
	}
}
