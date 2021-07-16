package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;

/**
 * 用于读取bean.xml中的<params/>节点
 * Created by Coral on 7/24/15.
 */

public class ServletMappingsNode extends SuperPojo {
	public static final String NAME = "servletMappings";

	private String protocol;


	private String innerXml;

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getInnerXml() {
		return innerXml;
	}

	public void setInnerXml(String innerXml) {
		this.innerXml = innerXml;
	}
}
