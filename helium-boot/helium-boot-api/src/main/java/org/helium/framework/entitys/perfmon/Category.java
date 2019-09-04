package org.helium.framework.entitys.perfmon;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

import java.util.List;

/**
 * Created by Coral on 2015/8/17.
 */
public class Category extends SuperPojo {
	@Field(id = 1)
	String name;

	@Field(id = 2)
	int instance;

	@Field(id = 3)
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
