package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;

/**
 *
 * Created by Coral on 7/23/15.
 */
public class BundleNode extends SuperPojo {
	private String path;


	private String jar;

	private boolean export;

	private String stacks;

	private String location;

	private String endpoints;


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
