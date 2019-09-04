package com.feinno.superpojo;

import com.feinno.superpojo.io.*;
import com.google.gson.JsonObject;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * 序列化POJO对象的辅助类
 * 
 * @author lvmingwei
 * 
 * @param <T>
 */
public class NativeBuilder<T> extends Builder<T> {

	private Builder<NativeSuperPojo<T>> builder;

	/**
	 * 构造方法，构造时需要传递将要序列化或反序列化的Java对象
	 * 
	 * @param data
	 *            将要序列化或反序列化的Java对象
	 */
	public NativeBuilder(T data) {
		super(data);
	}

	public void regEffectiveBuilder(Builder<NativeSuperPojo<T>> builder) {
		this.builder = builder;
	}

	@Override
	public JsonObject toJsonObject() {
		return builder.toJsonObject();
	}

	@Override
	public void parseJsonFrom(JsonInputStream input) {
		builder.parseJsonFrom(input);
	}

	@Override
	public int getSerializedSize() {
		return builder.getSerializedSize();
	}

	@Override
	public void writePbTo(CodedOutputStream output) throws IOException {
		builder.writePbTo(output);
	}

	@Override
	public void parsePbFrom(CodedInputStream input) throws IOException {
		builder.parsePbFrom(input);
	}

	@Override
	public void writeXmlTo(XmlOutputStream output) throws XMLStreamException {
		builder.writeXmlTo(output);
	}

	@Override
	public void parseXmlFrom(XmlInputStream input) throws XMLStreamException {
		builder.parseXmlFrom(input);
	}

	/**
	 * 获得被操作的Java对象
	 * 
	 * @return
	 */
	public T getData() {
		return builder.getData().getData();
	}

}
