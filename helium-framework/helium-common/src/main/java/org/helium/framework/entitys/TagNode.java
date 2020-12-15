package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.SuperPojoUtils;
import org.helium.framework.tag.Tag;
import org.helium.util.XmlUtils;

/**
 *
 * Created by Coral on 7/20/15.
 */
@Entity(name = "tag")
public class TagNode extends SuperPojo {
	@Field(id = 1, name = "class", type = NodeType.ATTR)
	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * 用于创建过程中的缓冲
	 */
	private Class<?> clazz;

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

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

	public static TagNode create(Class<? extends Tag> clazz, String value) {
		TagNode node = create(clazz.getName(), value);
		node.setClazz(clazz);
		return node;
	}

	public static TagNode create(String className, String value) {
		StringBuilder str = new StringBuilder();
		str.append("<tag class=\"");
		str.append(className);
		str.append("\">");
		str.append(XmlUtils.encode(value));
		str.append("</tag>");

		TagNode node = new TagNode();
		node.parseXmlFrom(str.toString());
		return node;
	}
}
