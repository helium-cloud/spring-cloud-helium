package com.feinno.superpojo.generator.Field;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * 
 * <b>描述: </b>用于序列化组件，为序列化组件在生成某一字段序列化代码时提供这个字段的字段信息
 * <p>
 * <b>功能: </b>提供字段的字段信息
 * <p>
 * <b>用法: </b>该类由序列化组件调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public final class FieldInformation implements Cloneable {

	private Class<?> outterClazz;

	private Field field;

	private com.feinno.superpojo.annotation.Field annoField;

	private com.feinno.superpojo.annotation.FieldExtensions annoFieldExtensions;

	private com.feinno.superpojo.annotation.Childs annoChilds;

	// private ProtoType protoType;

	// private int number;

	// private boolean required;

	// private TimeZone timezone;

	private Type currentType;

	private int currentNumber;

	public FieldInformation(Class<?> outterClazz, Field field) {
		init(outterClazz, field);
	}

	private final void init(Class<?> outterClazz, Field field) {
		annoField = field.getAnnotation(com.feinno.superpojo.annotation.Field.class);
		annoFieldExtensions = field.getAnnotation(com.feinno.superpojo.annotation.FieldExtensions.class);
		annoChilds = field.getAnnotation(com.feinno.superpojo.annotation.Childs.class);
		this.outterClazz = outterClazz;
		this.field = field;
		this.currentNumber = annoChilds != null ? annoChilds.id() : annoField.id();
		this.currentType = field.getGenericType();
	}

	public final FieldInformation clone() {
		FieldInformation fieldInformation = new FieldInformation(this.outterClazz, this.field);
		fieldInformation.currentType = currentType;
		fieldInformation.currentNumber = currentNumber;
		return fieldInformation;
	}

	public final Field getField() {
		return field;
	}

	public com.feinno.superpojo.annotation.Field getAnnoField() {
		return annoField;
	}

	public com.feinno.superpojo.annotation.FieldExtensions getAnnoFieldExtensions() {
		return annoFieldExtensions;
	}

	public com.feinno.superpojo.annotation.Childs getAnnoChilds() {
		return annoChilds;
	}

	public final Type getCurrentType() {
		return currentType;
	}

	public final FieldInformation setCurrentType(Type currentType) {
		FieldInformation fieldInformation = this.clone();
		fieldInformation.currentType = currentType;
		return fieldInformation;
	}

	public final int getNumber() {
		if (annoChilds != null) {
			return annoChilds.id();
		} else {
			return annoField.id();
		}
	}

	public final int getCurrentNumber() {
		return currentNumber;
	}

	public final FieldInformation setCurrentNumber(int currentNumber) {
		FieldInformation fieldInformation = this.clone();
		fieldInformation.currentNumber = currentNumber;
		return fieldInformation;
	}

	public final Class<?> getOutterClass() {
		return outterClazz;
	}
}
