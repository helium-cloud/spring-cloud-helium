package org.helium.framework.entitys;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.helium.superpojo.SuperPojo;
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
public class SetterNode extends SuperPojo {

	private String field;


	private String loader;

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

	public static SetterNode create(String fieldName, Class<? extends FieldLoader> loaderClazz, String value) throws JsonProcessingException {
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
		node.parseFromJson(str.toString());
		node.setLoaderClazz(loaderClazz);
		return node;
	}
}
