package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import org.helium.util.TypeUtils;

import java.util.List;

/**
 * bootstrap.xml或bundle.xml中的<references/>节点
 *
 * Created by Coral on 7/25/15.
 */
public final class BeanReferenceNode extends SuperPojo {
	@Field(id = 1, name = "path", type = NodeType.ATTR)
	private String path;

	@Field(id = 2, name = "class", type = NodeType.ATTR)
	private String clazz;

	@Field(id = 3, name = "interface", type = NodeType.ATTR)
	private String interfaceClazz;

	@Field(id = 4, name = "endpoints", type = NodeType.ATTR)
	private String endpointsAttr;

	@Field(id = 5, name = "id", type = NodeType.ATTR)
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
