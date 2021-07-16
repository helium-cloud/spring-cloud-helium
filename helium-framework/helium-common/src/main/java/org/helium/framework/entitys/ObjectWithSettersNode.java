package org.helium.framework.entitys;

import org.helium.superpojo.SuperPojo;
import org.helium.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * for dynamic create node
 *
 * <object group="" id="" name="" interface="" class="" enabled="true">
 *     <setters>
 *         ...
 *     </setters>
 * </object>
 */
public class ObjectWithSettersNode extends SuperPojo {
	private String id;

	private String className;

	private String mode;

	private String params;

	private String isEnabled;

	private String value;


	private List<SetterNode> setters = new ArrayList<>();

	/**
	 * 用于创建过程中的缓冲
	 */
	private Class<?> clazz;

	/**
	 * 合并Setters
	 * @param setters2
	 * @param replaceSameKey
	 */
	public void mergeSetters(List<SetterNode> setters2, boolean replaceSameKey) {
		Map<String, SetterNode> map1 = CollectionUtils.generateHashMap(setters, s -> s.getField());
		Map<String, SetterNode> map2 = CollectionUtils.generateHashMap(setters2, s -> s.getField());
		CollectionUtils.mergeMap(map1, map2, replaceSameKey);
		setters = CollectionUtils.cloneValues(map1);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	public String getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}

	public List<SetterNode> getSetters() {
		return setters;
	}

	public void setSetters(List<SetterNode> setters) {
		this.setters = setters;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

	public String getMode() {
		return mode;
	}

	public ObjectWithSettersNode setMode(String mode) {
		this.mode = mode;
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
