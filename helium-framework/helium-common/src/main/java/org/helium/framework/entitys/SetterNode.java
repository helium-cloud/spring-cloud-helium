package org.helium.framework.entitys;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.annotation.Field;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.SuperPojoUtils;
import org.helium.framework.configuration.FieldLoader;
import org.helium.util.XmlUtils;

/**
 * <setter field="field1">111</setter>
 * <p>
 * setter: { field: field1 {
 * <p>
 * } }
 * <p>
 * <setters> <setter field="connStr>mysql://admin@10.0.0.18:3058</setter><!--
 * public void setConnStr(String connStr) --> <setter field="iFC> <iFC>
 * <p>
 * </iFC> </setter> </setters>
 *
 * Created by Coral
 */
@Entity(name = "setter")
public class SetterNode extends SuperPojo {
	@Field(id = 1, name = "field", type = NodeType.ATTR)
	private String field;

	@Field(id = 2, name = "loader", type = NodeType.ATTR)
	private String loader;
	@Field(id = 3, name = "timeout", type = NodeType.ATTR)
	private int timeout;

	private String key;


	private String value;

	private SetterNodeLoadType loadType;


	public SetterNode() {
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getLoader() {
		return loader;
	}

	public void setLoader(String loader) {
		this.loader = loader;
	}

	/**
	 * 用于创建过程中的临时存储
	 */
	private Class<?> loaderClazz;

	private boolean isSet;

	private Object attachment;

	public Object getAttachment() {
		return attachment;
	}

	public void setAttachment(Object attachment) {
		this.attachment = attachment;
	}

	public Class<?> getLoaderClazz() {
		return loaderClazz;
	}

	public void setLoaderClazz(Class<?> loaderClazz) {
		this.loaderClazz = loaderClazz;
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

	public boolean isSet() {
		return isSet;
	}

	public void setIsSet(boolean isSet) {
		this.isSet = isSet;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		SuperPojoUtils.setStringAnyNode(this, value);
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public SetterNodeLoadType getLoadType() {
		if (loadType == null){
			loadType = SetterNodeLoadType.UNKNOWN;
		}
		return loadType;
	}

	public void setLoadType(SetterNodeLoadType loadType) {
		this.loadType = loadType;
	}

	public void setSet(boolean set) {
		isSet = set;
	}

	public static SetterNode create(String fieldName, Class<? extends FieldLoader> loaderClazz, String value) {
		StringBuilder str = new StringBuilder();
		str.append("<setter field=\"");
		str.append(fieldName);
		if (loaderClazz != null) {
			str.append("\" loader=\"");
			str.append(loaderClazz.getName());
			str.append("\">");
		} else {
			str.append("\">");
		}
		str.append(XmlUtils.encode(value));
		str.append("</setter>");

		SetterNode node = new SetterNode();
		node.parseXmlFrom(str.toString());
		node.setLoaderClazz(loaderClazz);
		return node;
	}
}
