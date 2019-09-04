package com.feinno.superpojo.annotation;

/**
 * 
 * <b>描述: </b>Google的Protobuf原生序列化类型<br>
 * 默认会使用Automatic类型，Automatic会自动匹配相应的序列化类型<br>
 * 仅在特殊情况才需手动指定特殊的类型，请注意，下面的大部分类型仅在int和long的原始类型或包装类型的字段中才有效
 * <p>
 * <b>功能: </b>用于指定一个字段是以何种格式写入二进制流中，例如一个int是使用有符号的字节码写入还是以无符号的字节码写入到流中
 * <p>
 * <b>用法: </b>此枚举类型用于{@link ProtoMember#type()}字段
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public enum ProtoType {

	AUTOMATIC(null), DOUBLE("Double"), FLOAT("Float"), INT32("Int32"), INT64("Int64"), UINT32("UInt32"), UINT64(
			"UInt64"), SINT32("SInt32"), SINT64("SInt64"), FIXED32("Fixed32"), FIXED64("Fixed64"), SFIXED32("SFixed32"), SFIXED64(
			"SFixed64"), BOOL("Bool"), STRING("String"), BYTES("Bytes");

	/** 某一种类型方式在操作流时的方法名称 */
	private String protoStreamString;

	ProtoType(String protoStreamString) {
		this.protoStreamString = protoStreamString;
	}

	/**
	 * 获得序列化类型的字符串表达
	 * 
	 * @return
	 */
	public String getProtoStreamString() {
		return protoStreamString;
	}
}
