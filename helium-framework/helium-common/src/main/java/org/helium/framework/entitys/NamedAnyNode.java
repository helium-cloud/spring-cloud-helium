/*
 * Created by Coral on 5/9/15.
 */
package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.SuperPojoUtils;
import org.helium.util.StringUtils;

/**
 * 处理如下结点
 * <node name="">...</node>
 */
public class NamedAnyNode extends SuperPojo {
	@Field(id = 1, name = "name", type = NodeType.ATTR)
	private String name;

	@Field(id = 2, name = "class", type = NodeType.ATTR)
	private String clazz;

	@Field(id = 3, name = "path", type = NodeType.ATTR)
	private String path;

	@Field(id = 5, name = "value", type = NodeType.ATTR)
	private String value;

	@Field(id = 6, name = "enabled", type = NodeType.ATTR)
	private boolean enabled;

	private Object attachment;

	/**
	 * 获取结点中的xml结点
	 * @return
	 */
	public AnyNode getInnerNode() {
		return SuperPojoUtils.getAnyNode(this);
	}

	/**
	 * 获取xml结点中的文本
	 * @return
	 */
	public String getInnerText() {
		return SuperPojoUtils.getStringAnyNode(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getText() {
		if (!StringUtils.isNullOrEmpty(value)) {
			return value;
		} else {
			return this.getInnerText();
		}
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}
}
