package com.feinno.superpojo;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.stream.XMLStreamException;

import com.feinno.superpojo.io.ByteString;
import com.feinno.superpojo.io.CodedInputStream;
import com.feinno.superpojo.io.CodedOutputStream;
import com.feinno.superpojo.io.WireFormat;
import com.feinno.superpojo.io.XmlInputStream;
import com.feinno.superpojo.type.AnyNode;
import com.feinno.superpojo.util.SuperPojoUtils.XmlTypeEnum;

/**
 * 
 * <b>描述: </b>针对protobuf反序列化后未知的字段数据存储所准备的一种数据结构<br>
 * <p>
 * <b>功能:
 * </b>当一端序列化A类时，另一端反序列化的A类与原始A类存在不同，那么反序列化也不会出现字段丢失的情况，无法解析的字段会被默认存储在该对象中
 * ，保证传输时数据不会丢失
 * <p>
 * <b>使用场景：</b>当待序列化的JavaBean文件一端版本发生变更，而另一端未及时更新<br>
 * <b>细节: </b>当一端在序列化时，存入A字段，但在另一端反序列化时，因为版本等其他问题因素，对方不存在A这个字段，<br>
 * 那么这个A字段所保存的消息会被存入此数据类型中,这么做可以实现版本兼容，即使序列化两端的版本或字段不一样，也可以进行正常的解析。
 * <p>
 * <b>用法: </b>该对象由序列化组件创建并赋值，可以从{@link ProtoEntity}中取出
 * 
 * <pre>
 * A a = new A();
 * a.setId(1);
 * a.setName(&quot;Feinno&quot;);
 * byte[] buffer = ProtoManager.toByteArray(a);
 * 
 * B b = new B();
 * ProtoManager.parseForm(b, buffer);
 * UnknownFieldSet unknowFieldSet = b.getUnknownFields();
 * </pre>
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class UnknownFieldSet implements Serializable {

	private Map<Integer, List<UnknownField<?>>> fieldMap;

	/**
	 * 获取某一ProtoMember号中的字段所存储的全部内容
	 * 
	 * @param number
	 * @return
	 */
	public Iterator<UnknownField<?>> getUnknowFields(int number) {
		if (fieldMap != null && fieldMap.get(number) != null) {
			return fieldMap.get(number).iterator();
		}
		return null;
	}

	/**
	 * 获得全部未知字段的ProtoMember号
	 * 
	 * @return
	 */
	public Iterator<Integer> getNumbers() {
		if (fieldMap != null) {
			return fieldMap.keySet().iterator();
		}
		return null;
	}

	/**
	 * 向这个未知字段中增加值
	 * 
	 * @param number
	 * @param unKnownField
	 */
	private void putUnKnownField(int number, UnknownField<?> unKnownField) {
		if (fieldMap == null) {
			fieldMap = new TreeMap<Integer, List<UnknownField<?>>>();
		}
		List<UnknownField<?>> list = fieldMap.get(number);
		if (list == null) {
			list = new LinkedList<UnknownField<?>>();
			fieldMap.put(number, list);
		}
		list.add(unKnownField);
	}

	/**
	 * 反序列化未知字段到当前对象中
	 * 
	 * @param tag
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public boolean parseUnknownField(int tag, CodedInputStream input) throws IOException {
		final int number = WireFormat.getTagFieldNumber(tag);
		switch (WireFormat.getTagWireType(tag)) {
		case WireFormat.WIRETYPE_VARINT:
			putUnKnownField(number, new UnknownField<Long>(input.readInt64(), WireFormat.WIRETYPE_VARINT));
			return true;
		case WireFormat.WIRETYPE_FIXED64:
			putUnKnownField(number, new UnknownField<Long>(input.readFixed64(), WireFormat.WIRETYPE_FIXED64));
			return true;
		case WireFormat.WIRETYPE_LENGTH_DELIMITED:
			putUnKnownField(number, new UnknownField<ByteString>(input.readBytes(),
					WireFormat.WIRETYPE_LENGTH_DELIMITED));
			return true;
		case WireFormat.WIRETYPE_START_GROUP:
			// 此种类型消息直接丢弃
			input.skipMessage();
			return false;
		case WireFormat.WIRETYPE_END_GROUP:
			return false;
		case WireFormat.WIRETYPE_FIXED32:
			putUnKnownField(number, new UnknownField<Integer>(input.readFixed32(), WireFormat.WIRETYPE_FIXED32));
			return true;
		default:
			throw InvalidProtocolBufferException.invalidWireType();
		}
	}

	public boolean parseAnyNode(String name, XmlInputStream input) throws XMLStreamException {
		AnyNode node = input.readNode();
		node.setName(name);
		return putAnyNode(node);
	}

	public boolean putAnyNode(AnyNode node) throws XMLStreamException {
		putUnKnownField(XmlTypeEnum.NODE.intValue(), new com.feinno.superpojo.UnknownField<AnyNode>(
				node, com.feinno.superpojo.UnknownField.RESERVED_FIELD));
		return true;
	}

	public boolean putStringAnyNode(String value) throws XMLStreamException {
		putUnKnownField(XmlTypeEnum.STRING.intValue(), new com.feinno.superpojo.UnknownField<String>(value,
				com.feinno.superpojo.UnknownField.RESERVED_FIELD));
		return true;

	}

	/**
	 * 将未知字段写入到输出流中
	 * 
	 * @param output
	 * @return
	 */
	public boolean writeUnknownField(CodedOutputStream output) throws IOException {
		Iterator<Integer> iteratorNumber = getNumbers();
		if (iteratorNumber == null) {
			return true;
		}
		while (iteratorNumber.hasNext()) {
			Integer number = iteratorNumber.next();
			Iterator<UnknownField<?>> fields = getUnknowFields(number);
			if (fields == null) {
				continue;
			}
			while (fields.hasNext()) {
				UnknownField<?> field = fields.next();
				field.wrteTo(number, output);
			}
		}
		return true;
	}

	/**
	 * 获得位置字段的序列化长度
	 * 
	 * @return
	 */
	public long getSerializedSize() {
		long size = 0;
		Iterator<Integer> iteratorNumber = getNumbers();
		if (iteratorNumber == null) {
			return size;
		}
		while (iteratorNumber.hasNext()) {
			Integer number = iteratorNumber.next();
			Iterator<UnknownField<?>> fields = getUnknowFields(number);
			if (fields == null) {
				continue;
			}
			while (fields.hasNext()) {
				UnknownField<?> field = fields.next();
				size += field.getSerializedSize(number);
			}
		}
		return size;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (fieldMap == null) {
			return "";
		}
		for (final Map.Entry<Integer, List<UnknownField<?>>> entry : fieldMap.entrySet()) {
			sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
		}
		return sb.toString();
	}

}
