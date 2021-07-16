package org.helium.perfmon.observation;


import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ObserverReportEntity extends SuperPojo {
	private String category;

	private Date time;

	private List<ObserverReportColumn> columns = new ArrayList<ObserverReportColumn>();

	private List<ObserverReportRowBean> rows = new ArrayList<ObserverReportRowBean>();

	/*
	 * private ObserverReportBean() { super(); }
	 * 
	 * public ObserverReportBean(String category, DateTime time,
	 * List<ObserverReportColumn> columns, List<ObserverReportRowBean> rows) {
	 * super(); this.category = category; this.time = time; this.columns =
	 * columns; this.rows = rows; }
	 */

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public List<ObserverReportColumn> getColumns() {
		return columns;
	}

	public void setColumns(List<ObserverReportColumn> columns) {
		this.columns = columns;
	}

	public List<ObserverReportRowBean> getRows() {
		return rows;
	}

	public void setRows(List<ObserverReportRowBean> rows) {
		this.rows = rows;
	}
}
