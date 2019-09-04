package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.util.ClassUtil;
import com.feinno.superpojo.util.ProtoGenericsUtils;

/**
 * <b>描述: </b>用于序列化组件，这是一个枚举类型的解释器，在自动生成源码的过程中发现类某一个字段为枚举类型时，会自动调用此解释器，
 * 用于在创建源码时解释此字段如何序列化( write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * <b>功能: </b>枚举类型的解释器,用于在创建源码时解释此字段如何序列化( write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到枚举类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class EnumFieldInterpreter extends AbstractFieldInterpreter {

	private static FieldInterpreter INSTANCE = null;

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEnum_obj() != null) output.writeEnum(1, data.getEnum_obj().intValue());</code>
	 * <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) ${output}.write${streamType}(${number}, ${data}.${getterName}().intValue());";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>if(data.getEnum_obj() != null) size += com.feinno.serialization.protobuf.CodedOutputStream.computeEnumSize(1, data.getEnum_obj().intValue());</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},${data}.${getterName}().intValue());";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>data.setEnum_obj((com.test.serialization.PersonEnum)com.feinno.superpojo.util.EnumParser.parseInt(com.test.serialization.PersonEnum.class,input.readEnum()));</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}((${enum_class})${package_util}.EnumParser.parseInt(${enum_class}.class,${input}.read${streamType}()));";

	/**
	 * 表示反序列化XML代码的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){input.nextEvent();data.setId(input.readInt());}</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${input}.nextEvent(); ${data}.${setterName}(${input}.read${xmlType}(${enum_class}.class));}";

	/**
	 * 表示反序列化代码XML的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){data.setId(input.readInt());}</code> <br>
	 * 的输出
	 */
	private static final String PARSE_ATTR_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${data}.${setterName}(${input}.read${xmlType}(${enum_class}.class));}";

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>output.writeEnum(1, value.intValue());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_ARRAY_TEMPLATE = "${output}.write${streamType}(${number}, value.intValue());";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>size += com.feinno.serialization.protobuf.CodedOutputStream.computeEnumSize(1, value.intValue());</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},value.intValue());";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>fieldValue = (com.test.serialization.PersonEnum)com.feinno.superpojo.util.EnumParser.parseInt(com.test.serialization.PersonEnum.class,input.readEnum());</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = (${enum_class})${package_util}.EnumParser.parseInt(${enum_class}.class,${input}.read${streamType}());";

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new EnumFieldInterpreter(ProtoFieldType.ENUM);
		}
		return INSTANCE;
	}

	private EnumFieldInterpreter(ProtoFieldType protoFieldType) {
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
		String writeCode = getWriteCodeTemplate();
		writeCode = writeCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeCode = writeCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		writeCode = writeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		return writeCode;
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
		String sizeCode = getSizeCodeTemplate();
		sizeCode = sizeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		sizeCode = sizeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		return sizeCode;
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
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.FEINNO_UTIL);
		parseCode = parseCode.replaceAll("\\$\\{enum_class\\}",
				ClassUtil.processClassName(fieldInformation.getField().getType().getName()));
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getParseNodeXmlCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	public String getParseNodeXmlCode(FieldInformation fieldInformation) {
		String parseCode = PARSE_NODE_XML_CODE_TEMPLATE;
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{enum_class\\}",
				ClassUtil.processClassName(fieldInformation.getField().getType().getName()));
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getParseAttrXmlCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	public String getParseAttrXmlCode(FieldInformation fieldInformation) {
		String parseCode = PARSE_ATTR_XML_CODE_TEMPLATE;
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{enum_class\\}",
				ClassUtil.processClassName(fieldInformation.getField().getType().getName()));
		return parseCode;
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
		String writeCode = getWriteCodeForArrayTemplate();
		writeCode = writeCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeCode = writeCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		writeCode = writeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		return writeCode;
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
		String sizeCode = getSizeCodeForArrayTemplate();
		sizeCode = sizeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		sizeCode = sizeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		return sizeCode;
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
		String parseCode = getParseCodeForArrayTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.FEINNO_UTIL);
		parseCode = parseCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		parseCode = parseCode.replaceAll("\\$\\{enum_class\\}",
				ProtoGenericsUtils.getClassFullName(fieldInformation.getCurrentType()));
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.AbstractFieldInterpreter
	 * #getParseNodeXmlCodeForArray(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	@Override
	public String getParseNodeXmlCodeForArray(FieldInformation fieldInformation) {
		return getParseCodeForArray(fieldInformation);
	}

	protected String getWriteCodeTemplate() {
		return WRITE_CODE_TEMPLATE;
	}

	protected String getSizeCodeTemplate() {
		return SIZE_CODE_TEMPLATE;
	}

	protected String getParseCodeTemplate() {
		return PARSE_CODE_TEMPLATE;
	}

	protected String getWriteCodeForArrayTemplate() {
		return WRITE_CODE_ARRAY_TEMPLATE;
	}

	protected String getSizeCodeForArrayTemplate() {
		return SIZE_CODE_ARRAY_TEMPLATE;
	}

	protected String getParseCodeForArrayTemplate() {
		return PARSE_CODE_ARRAY_TEMPLATE;
	}
}
