package com.feinno.superpojo;

import com.feinno.superpojo.io.ByteString;
import com.feinno.superpojo.io.CodedOutputStream;
import com.feinno.superpojo.io.WireFormat;
import com.feinno.superpojo.type.AnyNode;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Locale;

/**
 * 
 * <b>描述: </b>用于表示未知字段的二元组，用于配合{@link UnknownFieldSet}
 * 的使用，用于标识某一个字段及其所属protobuf类型wireFormat
 * <p>
 * <b>功能: </b>用于标识某一个字段及其所属protobuf类型wireFormat
 * <p>
 * <b>用法: </b>由序列化组件直接创建并赋值
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 * @param <T>
 */
public class UnknownField<T> {

	private T t;

	private int wireFormat;

	// 保留字段
	public static final int RESERVED_FIELD = -1;

	/**
	 * 构造方法，构造一个wireFormat类型的t对象
	 * 
	 * @param t
	 * @param wireFormat
	 */
	public UnknownField(T t, int wireFormat) {
		this.t = t;
		this.wireFormat = wireFormat;
	}

	/**
	 * 获得数据
	 * 
	 * @return
	 */
	public final T getData() {
		return t;
	}

	/**
	 * 获得数据的protobuf类型
	 * 
	 * @return
	 */
	public final int getWireFormat() {
		return wireFormat;
	}

	/**
	 * 写入某一个未知字段到流中
	 * 
	 * @param field
	 * @param number
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public boolean wrteTo(int number, CodedOutputStream output) throws IOException {
		switch (getWireFormat()) {
		case UnknownField.RESERVED_FIELD:
			// skip .
			return true;
		case WireFormat.WIRETYPE_VARINT:
			output.writeUInt64(number, (Long) getData());
			return true;
		case WireFormat.WIRETYPE_FIXED64:
			output.writeFixed64(number, (Long) getData());
			return true;
		case WireFormat.WIRETYPE_LENGTH_DELIMITED:
			output.writeBytes(number, (ByteString) getData());
			return true;
		case WireFormat.WIRETYPE_START_GROUP:
			return false;
		case WireFormat.WIRETYPE_END_GROUP:
			return false;
		case WireFormat.WIRETYPE_FIXED32:
			output.writeFixed32(number, (Integer) getData());
			return true;
		default:
			throw InvalidProtocolBufferException.invalidWireType();
		}
	}

	/**
	 * 获得某一个未知字段的长度
	 * 
	 * @param field
	 * @param number
	 * @param output
	 * @return
	 * @throws IOException
	 */
	public long getSerializedSize(int number) {
		switch (getWireFormat()) {
		case WireFormat.WIRETYPE_VARINT:
			return CodedOutputStream.computeUInt64Size(number, (Long) getData());
		case WireFormat.WIRETYPE_FIXED64:
			return CodedOutputStream.computeFixed64Size(number, (Long) getData());
		case WireFormat.WIRETYPE_LENGTH_DELIMITED:
			return CodedOutputStream.computeBytesSize(number, (ByteString) getData());
		case WireFormat.WIRETYPE_START_GROUP:
			return 0;
		case WireFormat.WIRETYPE_END_GROUP:
			return 0;
		case WireFormat.WIRETYPE_FIXED32:
			return CodedOutputStream.computeFixed32Size(number, (Integer) getData());
		default:
			return 0;
		}
	}

	@Override
	public String toString() {
		String value;
		switch (getWireFormat()) {
		case UnknownField.RESERVED_FIELD:
			value = new String(((AnyNode) getData()).toXmlByteArray());
			break;
		case WireFormat.WIRETYPE_VARINT:
			value = unsignedToString((Long) getData());
		case WireFormat.WIRETYPE_FIXED64:
			value = String.format((Locale) null, "0x%016x", getData());
			break;
		case WireFormat.WIRETYPE_LENGTH_DELIMITED:
			value = escapeBytes((ByteString) getData());
			break;
		case WireFormat.WIRETYPE_START_GROUP:
			value = "null";
			break;
		case WireFormat.WIRETYPE_END_GROUP:
			value = "null";
			break;
		case WireFormat.WIRETYPE_FIXED32:
			value = String.format((Locale) null, "0x%08x", getData());
			break;
		default:
			value = "null";
			break;
		}
		return String.format("WireFormat:[%s], data:[%s]", wireFormat, value);
	}

	// /** Convert an unsigned 32-bit integer to a string. */
	// private static String unsignedToString(final int value) {
	// if (value >= 0) {
	// return Integer.toString(value);
	// } else {
	// return Long.toString(((long) value) & 0x00000000FFFFFFFFL);
	// }
	// }

	/** Convert an unsigned 64-bit integer to a string. */
	private static String unsignedToString(final long value) {
		if (value >= 0) {
			return Long.toString(value);
		} else {
			// Pull off the most-significant bit so that BigInteger doesn't
			// think
			// the number is negative, then set it again using setBit().
			return BigInteger.valueOf(value & 0x7FFFFFFFFFFFFFFFL).setBit(63).toString();
		}
	}

	/**
	 * Escapes bytes in the format used in protocol buffer text format, which is
	 * the same as the format used for C string literals. All bytes that are not
	 * printable 7-bit ASCII characters are escaped, as well as backslash,
	 * single-quote, and double-quote characters. Characters for which no
	 * defined short-hand escape sequence is defined will be escaped using
	 * 3-digit octal sequences.
	 */
	static String escapeBytes(final ByteString input) {
		final StringBuilder builder = new StringBuilder(input.size());
		for (int i = 0; i < input.size(); i++) {
			final byte b = input.byteAt(i);
			switch (b) {
			// Java does not recognize \a or \v, apparently.
			case 0x07:
				builder.append("\\a");
				break;
			case '\b':
				builder.append("\\b");
				break;
			case '\f':
				builder.append("\\f");
				break;
			case '\n':
				builder.append("\\n");
				break;
			case '\r':
				builder.append("\\r");
				break;
			case '\t':
				builder.append("\\t");
				break;
			case 0x0b:
				builder.append("\\v");
				break;
			case '\\':
				builder.append("\\\\");
				break;
			case '\'':
				builder.append("\\\'");
				break;
			case '"':
				builder.append("\\\"");
				break;
			default:
				if (b >= 0x20) {
					builder.append((char) b);
				} else {
					builder.append('\\');
					builder.append((char) ('0' + ((b >>> 6) & 3)));
					builder.append((char) ('0' + ((b >>> 3) & 7)));
					builder.append((char) ('0' + (b & 7)));
				}
				break;
			}
		}
		return builder.toString();
	}

}
