package com.feinno.superpojo.util;

import com.feinno.superpojo.io.CodedInputStream;
import com.feinno.superpojo.io.CodedOutputStream;
import com.feinno.superpojo.io.WireFormat;
import com.feinno.superpojo.io.XmlInputStream;
import com.feinno.superpojo.type.Guid;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * <b>描述: </b>在序列化时对{@link Guid}进行辅助序列化的类
 * <p>
 * <b>功能: </b>在protobuf序列化组件进行序列化时，针对{@link Guid}需要进行特殊处理，此类就是协助序列化组件进行
 * {@link Guid}序列化的类
 * <p>
 * <b>用法: </b>由序列化组件自动生成的序列化辅助类{@link ProtoBuilder}的子类来调用，外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ProtoGuid {
	static final byte FieldLo = 0x01, FieldHi = 0x02;

	/**
	 * Guid的长度
	 */
	public static final int GuidSize = 20;

	/**
	 * 反序列化，从输入流中读取指定长度的字节，且将字节解析成{@link Guid}对象
	 * 
	 * @param input
	 *            输入流
	 * @return
	 * @throws IOException
	 */
	public static Guid deserialize(CodedInputStream input) throws IOException {
		Guid inner = new Guid();

		// 18
		byte length = input.readRawByte();
		if (length == 0)
			return null;
		// 9 fieldNumber=1
		input.readRawByte();
		byte[] data1 = new byte[8];
		for (int i = 0; i < 8; i++)
			data1[i] = input.readRawByte();
		// 17 fieldNumber=2
		input.readRawByte();
		byte[] data2 = new byte[8];
		for (int i = 0; i < 8; i++)
			data2[i] = input.readRawByte();
		inner.setData1(data1);
		inner.setData2(data2);
		return inner;
	}

	/**
	 * 序列化，将指定对象序列化成protobuf协议格式写入输出流中
	 * 
	 * @param value
	 *            待序列化的{@link Guid}对象
	 * @param output
	 *            输出位置
	 * @param lengthPrefix
	 * @throws IOException
	 */
	public static void serialize(Guid value, CodedOutputStream output, boolean lengthPrefix) throws IOException {
		// output.writeTag(fieldNumber, WireFormat.WIRETYPE_VARINT );
		if (value == null) {
			if (lengthPrefix) {

				output.writeRawByte(0);
				// return 1;
				return;
			}
			// return 0;
			return;
		}
		byte[] buffer = value.toPbByteArray();

		// int len;
		if (lengthPrefix) {
			output.writeRawByte((byte) 18);
			// len = 1;
		}
		output.writeRawByte(FieldLo << 3 | WireFormat.FieldType.FIXED64.getWireType());
		// output.writeBytesNoTag(ByteString.copyFrom(buffer, 0, 8));
		for (int i = 0; i < 8; i++)
			output.writeRawByte(buffer[i]);
		output.writeRawByte(FieldHi << 3 | WireFormat.FieldType.FIXED64.getWireType());
		for (int i = 0; i < 8; i++)
			output.writeRawByte(buffer[i + 8]);
		// return len + 18;
	}

	/**
	 * 反序列化，从输入流中读取指定长度的字节，且将字节解析成{@link Guid}对象
	 * 
	 * @param input
	 *            输入流
	 * @return
	 * @throws IOException
	 */
	public static Guid valueOf(XmlInputStream input) throws XMLStreamException {
		Guid inner = new Guid();
		int seq = input.getCurrentSeq();
		boolean d1 = false;
		boolean d2 = false;
		byte[] data1 = new byte[8];
		byte[] data2 = new byte[8];
		for (int i = 0; i < 8; i++) {
			input.nextEvent();
			String name = input.readName(seq);
			if (name == null) {
				break;
			} else if (name.equals("data1")) {
				data1[i] = input.readByte();
			}
			if (i == 7) {
				d1 = true;
			}
		}
		for (int i = 0; i < 8; i++) {
			input.nextEvent();
			String name = input.readName(seq);
			if (name == null) {
				break;
			} else if (name.equals("data2")) {
				data2[i] = input.readByte();
			}
			if (i == 7) {
				d2 = true;
			}
		}
		if (d1 && d2) {
			inner.setData1(data1);
			inner.setData2(data2);
			return inner;
		}
		return null;
		// Guid inner = new Guid();
		// Builder<Guid> builder = SuperPojoManager.getSuperPojoBuilder(inner);
		// builder.parseXmlFrom(input);
		// return builder.getData();
	}
}
