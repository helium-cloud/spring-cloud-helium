package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 * Created by Coral on 12/1/15.
 */
public class ExecutorNode extends SuperPojo {
	@Field(id = 1, name = "type", type = NodeType.ATTR)
	private String type = "fixed";

	@Field(id = 2, name = "name", type = NodeType.ATTR)
	private String name;

	@Field(id = 3, name = "size", type = NodeType.ATTR)
	private int size;

	@Field(id = 4, name = "maxSize", type = NodeType.ATTR)
	private int maxSize;

	@Field(id = 5, name = "limit", type = NodeType.ATTR)
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
