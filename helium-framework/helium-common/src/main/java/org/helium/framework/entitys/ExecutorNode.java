package org.helium.framework.entitys;


import org.helium.superpojo.SuperPojo;

/**
 * Created by Coral on 12/1/15.
 */
public class ExecutorNode extends SuperPojo {
	private String type = "fixed";

	private String name;

	private int size;

	private int maxSize;

	private int limit;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
	}
}
