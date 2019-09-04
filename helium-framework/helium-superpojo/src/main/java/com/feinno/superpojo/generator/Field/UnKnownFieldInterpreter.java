package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.generator.ProtoFieldType;

/**
 * <b>描述: </b>用于序列化组件，这是一个空的字段解释器,他存在的目的是为了那些不能够正确识别字段类型的字段来准备的,针对不能识别类型的字段,
 * 我们在序列化时会将他抛弃，不能处理的类型是指超出{@link ProtoFieldType}枚举范围之外的类型
 * <p>
 * <b>功能: </b>为了那些不能够正确识别字段类型的字段来准备的,针对不能识别类型的字段, 我们在序列化时会将他抛弃
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到未知的类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class UnKnownFieldInterpreter extends AbstractFieldInterpreter {
	/**
	 * 用于未知类型反序列化代码时的格式，期待一个类似于<br>
	 * <code>data.getUnknownFields().parseUnknownField(tag, input);</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "${data}.getUnknownFields().parseUnknownField(tag, ${input});";

	private static UnKnownFieldInterpreter INSTANCE = null;

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UnKnownFieldInterpreter(ProtoFieldType.UNKNOWN);
		}
		return INSTANCE;
	}

	private UnKnownFieldInterpreter(ProtoFieldType protoFieldType) {
		super(protoFieldType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getWriteCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getWriteCode(FieldInformation fieldInformation) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getSizeCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getSizeCode(FieldInformation fieldInformation) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getParseCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getParseCode(FieldInformation fieldInformation) {
		String parseCode = getParseCodeTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getRequiredCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getRequiredCode(FieldInformation fieldInformation) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.AbstractFieldInterpreter
	 * #getWriteCodeForArray(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	@Override
	public String getWriteCodeForArray(FieldInformation fieldInformation) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.AbstractFieldInterpreter
	 * #getSizeCodeForArray(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	@Override
	public String getSizeCodeForArray(FieldInformation fieldInformation) {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.AbstractFieldInterpreter
	 * #getParseCodeForArray(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	@Override
	public String getParseCodeForArray(FieldInformation fieldInformation) {
		String parseCode = getParseCodeTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		return parseCode;
	}

	public String getParseCodeTemplate() {
		return PARSE_CODE_TEMPLATE;
	}
}
