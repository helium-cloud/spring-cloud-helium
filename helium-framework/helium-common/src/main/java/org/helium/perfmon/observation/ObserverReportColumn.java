package org.helium.perfmon.observation;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;


/**
 * {在这里补充类的功能说明}
 *
 * Created by Coral
 */
public class ObserverReportColumn extends SuperPojo {
	@Field(id = 1)
	private String name;

	@Field(id = 2)
	private ObserverReportColumnType type = ObserverReportColumnType.LONG;

	public ObserverReportColumn(String name, ObserverReportColumnType type) {
		this.name = name;
		this.type = type;
	}

	public ObserverReportColumn() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ObserverReportColumnType getType() {
		return type;
	}

	public void setType(ObserverReportColumnType type) {
		this.type = type;
	}
}
