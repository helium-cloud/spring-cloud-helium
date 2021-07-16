package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;
import org.helium.framework.tag.Tag;

/**
 *
 * Created by Coral on 7/20/15.
 */
public class TagNode extends SuperPojo {

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


	public static TagNode create(Class<? extends Tag> clazz, String value) {
		TagNode node = create(clazz.getName(), value);
		node.setClazz(clazz);
		return node;
	}

	public static TagNode create(String className, String value) {
//		StringBuilder str = new StringBuilder();
//		str.append("<tag class=\"");
//		str.append(className);
//		str.append("\">");
//		str.append(XmlUtils.encode(value));
//		str.append("</tag>");

		TagNode node = new TagNode();
		//node.parseFromJson(str.toString());
		return node;
	}
}
