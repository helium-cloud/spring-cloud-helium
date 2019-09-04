package com.feinno.superpojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.BitSet;

/**
 * SuperPojo类型基类，所有可序列化的实体应该直接或简介继承此基类
 * 
 * @author Lv.Mingwei
 * 
 */
public class SuperPojo implements IProtobufPojo, IJsonPojo, IXmlPojo {

	/** 由于某种原因导致流中{@link ProtoMember}序号不在当前的JAVA对象中，则将此序号和数据存储在此区域 */
	@JSONField(serialize=false)
	private UnknownFieldSet unknownFieldSet = new UnknownFieldSet();

	/** 用于装载反序列化的字段名称，通过此集合中的内容，可以知道哪些字段值被反序列化到当前对象中 */
	@JSONField(serialize=false)
	private BitSet serializationFieldSet = new BitSet();

	/**
	 * 获得全部的未知字段，关于未知字段的描述和功能，请参见{@link UnknownFieldSet}
	 * 
	 * @return
	 */
	@JSONField(serialize=false)
	public UnknownFieldSet getUnknownFields() {
		return unknownFieldSet;
	}

	/**
	 * 通过调用此方法，来标识出一个字段是反序列化的
	 * 
	 * @param tag
	 */
	public void putSerializationFieldTag(int tag) {
		serializationFieldSet.set(tag);
	}

	/**
	 * 通过字段的tag值来判断一个字段是否是反序列化过来的
	 * 
	 * @param fieldName
	 * @return
	 */
	public boolean hasValue(int tag) {
		return serializationFieldSet.get(tag);
	}

	@Override
	public void writePbTo(OutputStream output) {
		SuperPojoManager.writePbTo(this, output);
	}

	@Override
	public byte[] toPbByteArray() {
		return SuperPojoManager.toPbByteArray(this);
	}

	@Override
	public void parsePbFrom(InputStream input) {
		SuperPojoManager.parsePbFrom(input, this);
	}

	@Override
	public void parsePbFrom(byte[] buffer) {
		SuperPojoManager.parsePbFrom(buffer, this);
	}

	@Override
	public JsonObject toJsonObject() {
		return SuperPojoManager.toJsonObject(this);
	}

	@Override
	public void writeXmlTo(OutputStream output) {
		SuperPojoManager.writeXmlTo(this, output);
	}

	public String toXmlString() {
		byte[] buffer = toXmlByteArray();
		return new String(buffer);
	}

	@Override
	public byte[] toXmlByteArray() {
		return SuperPojoManager.toXmlByteArray(this);
	}

	@Override
	public void parseXmlFrom(InputStream input) {
		SuperPojoManager.parseXmlFrom(input, this);
	}

	@Override
	public void parseXmlFrom(String xml) {
		SuperPojoManager.parseXmlFrom(xml, this);
	}

	/**
	 * 覆盖基类方法，以json格式打印该对象的完整内容
	 */
	@Override
	public String toString() {
		return this.toXmlString();
	}

}
