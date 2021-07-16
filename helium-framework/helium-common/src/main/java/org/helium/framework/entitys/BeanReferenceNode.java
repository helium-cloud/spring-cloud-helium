package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;
import org.helium.util.TypeUtils;

import java.util.List;

/**
 * bootstrap.xml或bundle.xml中的<references/>节点
 *
 * Created by Coral on 7/25/15.
 */
public final class BeanReferenceNode extends SuperPojo {

	private String path;

	private String clazz;

	private String interfaceClazz;

	private String endpointsAttr;

	private String id;

	public List<String> getEndpoints() {
		return TypeUtils.split(endpointsAttr, ",");
	}
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getInterfaceClazz() {
		return interfaceClazz;
	}

	public void setInterfaceClazz(String interfaceClazz) {
		this.interfaceClazz = interfaceClazz;
	}

	public String getEndpointsAttr() {
		return endpointsAttr;
	}

	public void setEndpointsAttr(String endpointsAttr) {
		this.endpointsAttr = endpointsAttr;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
