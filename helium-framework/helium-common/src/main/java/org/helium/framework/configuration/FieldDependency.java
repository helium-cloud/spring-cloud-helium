package org.helium.framework.configuration;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.annotation.Field;

/**
 * Bean的依赖关系
 * Created by Coral on 7/4/15.
 */
public class FieldDependency extends SuperPojo {
	/**
	 * 依赖的字段
	 */
	@Field(id = 1)
	private String field;

	/**
	 * 依赖类型
	 */
	@Field(id = 2)
	private FieldDependencyType referenceType;

	@Field(id = 3)
	private String loaderType;

	/**
	 * 依赖的具体值
	 */
	@Field(id = 4)
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
