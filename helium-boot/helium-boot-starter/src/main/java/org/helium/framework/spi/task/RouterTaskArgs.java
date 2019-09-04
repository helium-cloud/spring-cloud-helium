package org.helium.framework.spi.task;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Created by Coral on 9/20/15.
 */
public class RouterTaskArgs extends SuperPojo {
	@Field(id = 1)
	private String eventId;

	@Field(id = 2)
	private String tag;

	@Field(id = 3)
	private String beanId;

	@Field(id = 4)
	private byte[] argsData;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public byte[] getArgsData() {
		return argsData;
	}

	public void setArgsData(byte[] argsData) {
		this.argsData = argsData;
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}
}
