package com.feinno.superpojo.util;

import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import com.feinno.superpojo.SuperPojo;
import com.feinno.superpojo.UnknownField;
import com.feinno.superpojo.UnknownFieldSet;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.type.EnumInteger;

/**
 * 
 * @author Lv.Mingwei
 * 
 */
public class SuperPojoUtils {

	/**
	 * 
	 * @param superPojo
	 * @param clazz
	 * @return
	 */
	public static AnyNode getAnyNode(SuperPojo superPojo) {
		if (superPojo == null || superPojo.getUnknownFields() == null) {
			return null;
		}
		UnknownFieldSet unknowFieldSet = superPojo.getUnknownFields();
		Iterator<UnknownField<?>> iterator = unknowFieldSet.getUnknowFields(XmlTypeEnum.NODE.intValue());
		if (iterator == null) {
			return null;
		}
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			UnknownField<AnyNode> unknownField = (UnknownField<AnyNode>) iterator.next();
			AnyNode node = unknownField.getData();
			return node;
		}
		return null;
	}

	public static String getStringAnyNode(SuperPojo superPojo) {
		if (superPojo == null || superPojo.getUnknownFields() == null) {
			return null;
		}
		UnknownFieldSet unknowFieldSet = superPojo.getUnknownFields();
		Iterator<UnknownField<?>> iterator = unknowFieldSet.getUnknowFields(XmlTypeEnum.STRING.intValue());
		if (iterator == null) {
			return null;
		}
		while (iterator.hasNext()) {
			@SuppressWarnings("unchecked")
			UnknownField<String> unknownField = (UnknownField<String>) iterator.next();
			return unknownField.getData();
		}
		return null;
	}

	public static void setStringAnyNode(SuperPojo superPojo, String value) {
		if (superPojo == null || superPojo.getUnknownFields() == null) {
			return;
		}
		UnknownFieldSet unknowFieldSet = superPojo.getUnknownFields();
		try {
			unknowFieldSet.putStringAnyNode(value);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return;
	}

	public static void setAnyNode(SuperPojo superPojo, AnyNode node) {
		if (superPojo == null || superPojo.getUnknownFields() == null) {
			return;
		}
		UnknownFieldSet unknowFieldSet = superPojo.getUnknownFields();
		try {
			unknowFieldSet.putAnyNode(node);
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(XmlTypeEnum.STRING.intValue());
		System.out.println(XmlTypeEnum.NODE.intValue());
	}

	public static enum XmlTypeEnum implements EnumInteger {

		STRING(1 << 28), NODE((1 << 28) + 1);

		int value;

		XmlTypeEnum(int value) {
			this.value = value;
		}

		public int intValue() {
			return value;
		}
	}
}
