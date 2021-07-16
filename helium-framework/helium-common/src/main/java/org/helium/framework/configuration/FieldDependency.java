package org.helium.framework.configuration;


import org.helium.superpojo.SuperPojo;

/**
 * Bean的依赖关系
 * Created by Coral on 7/4/15.
 */
public class FieldDependency extends SuperPojo {
	/**
	 * 依赖的字段
	 */
	private String field;

	/**
	 * 依赖类型
	 */
	private FieldDependencyType referenceType;

	private String loaderType;

	/**
	 * 依赖的具体值
	 */
	private String value;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public FieldDependencyType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(FieldDependencyType referenceType) {
		this.referenceType = referenceType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLoaderType() {
		return loaderType;
	}

	public void setLoaderType(String loaderType) {
		this.loaderType = loaderType;
	}
}
