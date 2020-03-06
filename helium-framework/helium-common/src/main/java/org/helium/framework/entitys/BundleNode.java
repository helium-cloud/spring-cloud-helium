package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;

/**
 *
 * Created by Coral on 7/23/15.
 */
@Entity(name = "bundle")
public class BundleNode extends SuperPojo {
	@Field(id = 1, name = "path", type = NodeType.ATTR)
	private String path;

	@Field(id = 2, name = "jar", type = NodeType.ATTR)
	private String jar;

	@Field(id = 3, name = "export", type = NodeType.ATTR)
	private boolean export;

	@Field(id = 4, name = "stacks", type = NodeType.ATTR)
	private String stacks;

	@Field(id = 9, name = "location", type = NodeType.ATTR)
	private String location;

	@Field(id = 11, name = "endpoints", type = NodeType.ATTR)
	private String endpoints;

	@Field(id = 12, name = "grayFactors", type = NodeType.NODE)
	private FactorGroupNode grayFactors;

	public boolean isExport() {
		return export;
	}

	public void setExport(boolean export) {
		this.export = export;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getStacks() {
		return stacks;
	}

	public void setStacks(String stacks) {
		this.stacks = stacks;
	}

	public String getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(String endpoints) {
		this.endpoints = endpoints;
	}

	public FactorGroupNode getGrayFactors() {
		return grayFactors;
	}

	public void setGrayFactors(FactorGroupNode grayFactors) {
		this.grayFactors = grayFactors;
	}

	public String getJar() {
		return jar;
	}

	public void setJar(String jar) {
		this.jar = jar;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
