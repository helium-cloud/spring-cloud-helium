package org.helium.cloud.task.store;


import org.helium.superpojo.SuperPojo;

/**
 * Created by Coral on 9/20/15.
 */
public class RouterTaskArgs extends SuperPojo {

	private String eventId;


	private String tag;


	private String beanId;


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
