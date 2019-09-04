package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.SuperPojoUtils;

/**
 * 用于读取bean.xml中的<params/>节点
 * Created by Coral on 7/24/15.
 */
@Entity(name = ServletMappingsNode.NAME)
public class ServletMappingsNode extends SuperPojo {
	public static final String NAME = "servletMappings";

	@Field(id = 1, name = "protocol", type = NodeType.ATTR)
	private String protocol;

	@Field(id = 2)
	private String innerXml;

	/**
	 * 获取结点中的xml结点
	 * @return
	 */
	public <E extends SuperPojo> E getInnerNode(Class<E> clazz) {
		AnyNode an = SuperPojoUtils.getAnyNode(this);
		if (an != null) {
			return an.convertTo(clazz);
		} else {
			throw new IllegalArgumentException("Bad servletMappings node:" + this.toXmlString());
		}
	}

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
