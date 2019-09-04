package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.util.ClassUtil;
import com.feinno.superpojo.util.ProtoGenericsUtils;
import com.feinno.superpojo.util.StringUtils;

/**
 * <b>描述: </b>用于序列化组件，这个MessageFieldInterpreter是{@link ProtoEntity}
 * 类型的解释器，当一个类中的字段为{@link ProtoEntity}
 * 类型时，此解释器被启用，用于解释这个类型的字段如何序列化、如何反序列化以及如何获取序列化的长度
 * <p>
 * <b>功能: </b>用于解释{@link ProtoEntity}类型的字段如何序列化、如何反序列化以及如何获取序列化的长度
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到{@link ProtoEntity}类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class MessageFieldInterpreter extends AbstractFieldInterpreter {

	private static FieldInterpreter INSTANCE = null;

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getPersonTest() != null) output.writeMessage(1, new com.feinno.serialization.protobuf.extension.PersonProtoBuilder(data.getPersonTest()));</code>
	 * <br>
	 * 的输出,基类提供的公用模板已经无法阻挡它的变化了，因此在这里覆盖了基类的模板,多了一个${proto_builder}变量
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${builder_name} != null) ${output}.write${streamType}(${number}, ${builder_name});";

	private static final String WRITE_XML_CODE_NODE_TEMPLATE = "if( ${data}.${getterName}() != null) {${output}.writeStartElement(\"${fieldName}\");if( ${builder_name} != null) ${output}.write(${builder_name});${output}.writeEndElement(\"${fieldName}\");}";
	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>jsonObject.add("name",data.getXX() != null ? ProtoEntity.toJsonObject() : null);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_TEMPLATE = "${jsonObject}.add(\"${fieldName}\",  ${data}.${getterName}() != null ? ${data}.${getterName}().toJsonObject() : null);";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>if(data.getPersonTest() != null) size += com.feinno.serialization.protobuf.CodedOutputStream.computeMessageSize(1, new com.feinno.serialization.protobuf.extension.PersonProtoBuilder(data.getPersonTest()));</code>
	 * <br>
	 * 的输出,基类提供的公用模板已经无法阻挡它的变化了，因此在这里覆盖了基类的模板,多了一个${proto_builder}变量
	 */
	private static final String SIZE_CODE_TEMPLATE = "if( ${builder_name} != null) size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},${builder_name});";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.test.serialization.Person bean = new com.test.serialization.Person();com.feinno.serialization.protobuf.extension.PersonProtoBuilder builder = new com.feinno.serialization.protobuf.extension.PersonProtoBuilder(bean);input.readMessage(builder);data.setPersonTest(bean);</code>
	 * <br>
	 * 的输出,基类提供的公用模板已经无法阻挡它的变化了，因此在这里覆盖了基类的模板,这里多了一个${message_class}变量和${
	 * proto_builder}变量， 因此需要覆盖相应的getParseCode方法，用取到的基本字符串，将这个变量替换上去
	 */
	private static final String PARSE_CODE_TEMPLATE = "${message_class} bean = new ${message_class}();${proto_builder} builder = new ${proto_builder}(bean);${input}.read${streamType}(builder);${data}.${setterName}(bean);";

	/**
	 * 表示反序列化XML代码的格式，期待一个类似于<br>
	 * <code>else if(name.equals("user")){input.nextEvent();data.setUser(input.readMessage(User.class));}</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) { ${data}.${setterName}(${input}.read${xmlType}(${message_class}.class));}";

	/**
	 * 数组中表示序列化代码的格式，期待一个类似<br>
	 * <code> output.writeMessage(1, new com.feinno.serialization.protobuf.extension.PersonProtoBuilder(value);</code>
	 * <br>
	 * 的输出,基类提供的公用模板已经无法阻挡它的变化了，因此在这里覆盖了基类的模板,多了一个${proto_builder}变量
	 */
	private static final String WRITE_CODE_ARRAY_TEMPLATE = "${output}.write${streamType}(${number}, new ${proto_builder}(value));";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value.toString());jsonArray.add(jsonPrimitive);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_ARRAY_TEMPLATE = "jsonArray.add(value.toJsonObject());";

	/**
	 * 数组中表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code> size += com.feinno.serialization.protobuf.CodedOutputStream.computeMessageSize(1, new com.feinno.serialization.protobuf.extension.PersonProtoBuilder(value));</code>
	 * <br>
	 * 的输出,基类提供的公用模板已经无法阻挡它的变化了，因此在这里覆盖了基类的模板,多了一个${proto_builder}变量
	 */
	private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},new ${proto_builder}(value));";

	/**
	 * 数组中表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.test.serialization.Person fieldValue = new com.test.serialization.Person();com.feinno.serialization.protobuf.extension.PersonProtoBuilder builder = new com.feinno.serialization.protobuf.extension.PersonProtoBuilder(fieldValue);input.readMessage(builder);</code>
	 * <br>
	 * 的输出,基类提供的公用模板已经无法阻挡它的变化了，因此在这里覆盖了基类的模板,这里多了一个${message_class}变量和${
	 * proto_builder}变量， 因此需要覆盖相应的getParseCode方法，用取到的基本字符串，将这个变量替换上去
	 */
	private static final String PARSE_CODE_ARRAY_TEMPLATE = " fieldValue = new ${message_class}();${proto_builder} builder = new ${proto_builder}(fieldValue);${input}.read${streamType}(builder);";

	/**
	 * 表示反序列化XML代码的格式,用于数组，期待一个类似于<br>
	 * <code>fieldValue = input.readInt()</code> <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_ARRAY_TEMPLATE = "fieldValue = ${input}.read${xmlType}(${message_class}.class);";

	private static final String GLOBAL_CODE_TEMPLATE = "private ${proto_builder} ${builder_name} = null;{if(${data}.${getterName}() != null)${builder_name} = new ${proto_builder}(${data}.${getterName}());}";

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new MessageFieldInterpreter(ProtoFieldType.MESSAGE);
		}
		return INSTANCE;
	}

	private MessageFieldInterpreter(ProtoFieldType protoFieldType) {
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
		writeCode = writeCode.replaceAll("\\$\\{builder_name\\}", getBuilderClassName(fieldInformation));
		return writeCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.superpojo.generator.Field.protobuf.FieldInterpreter#
	 * getWriteNodeXmlCode
	 * (com.feinno.superpojo.generator.Field.protobuf.FieldInformation)
	 */
	public String getWriteNodeXmlCode(FieldInformation fieldInformation) {
		if (getNodeType(fieldInformation) != NodeType.NODE) {
			return "";
		}
		String writeXmlCode = WRITE_XML_CODE_NODE_TEMPLATE;
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{builder_name\\}", getBuilderClassName(fieldInformation));
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		if (fieldInformation.getAnnoField() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoField().name())) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getAnnoField().name());
		} else if (fieldInformation.getAnnoChilds() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoChilds().child())) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getAnnoChilds().child());
		} else {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getField().getName());
		}
		return writeXmlCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getJsonCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	public String getJsonCode(FieldInformation fieldInformation) {
		String jsonObjectCode = getJsonObjectCodeTemplate();
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{jsonObject\\}", VARIABLE_NAME_JSON_OBJECT);
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getField().getName());
		return jsonObjectCode;
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
		sizeCode = sizeCode.replaceAll("\\$\\{builder_name\\}", getBuilderClassName(fieldInformation));
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
		parseCode = parseCode.replaceAll("\\$\\{proto_builder\\}",
				ClassUtil.getBuilderClassFullName(fieldInformation.getField().getType()));
		parseCode = parseCode.replaceAll("\\$\\{message_class\\}", fieldInformation.getField().getType().getName()
				.replace("$", "."));
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
		if (getNodeType(fieldInformation) != NodeType.NODE) {
			return "";
		}
		String parseCode = PARSE_NODE_XML_CODE_TEMPLATE;
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{message_class\\}",
				ProtoGenericsUtils.getClassFullName(fieldInformation.getCurrentType()));
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
		writeCode = writeCode.replaceAll("\\$\\{proto_builder\\}",
				ClassUtil.getBuilderClassFullName(ProtoGenericsUtils.getClass(fieldInformation.getCurrentType())));
		return writeCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.AbstractFieldInterpreter
	 * #getJsonCodeForArray(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	public String getJsonCodeForArray(FieldInformation fieldInformation) {
		return getJsonCodeForArrayTemplate();
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
		sizeCode = sizeCode.replaceAll("\\$\\{proto_builder\\}",
				ClassUtil.getBuilderClassFullName(ProtoGenericsUtils.getClass(fieldInformation.getCurrentType())));
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
		parseCode = parseCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		parseCode = parseCode.replaceAll("\\$\\{proto_builder\\}",
				ClassUtil.getBuilderClassFullName(ProtoGenericsUtils.getClass(fieldInformation.getCurrentType())));
		parseCode = parseCode.replaceAll("\\$\\{message_class\\}",
				ProtoGenericsUtils.getClassFullName(fieldInformation.getCurrentType()));
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.getParseNodeXmlCodeForArray
	 * #getGlobalCode(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	public String getParseNodeXmlCodeForArray(FieldInformation fieldInformation) {
		String parseCode = PARSE_NODE_XML_CODE_ARRAY_TEMPLATE;
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{message_class\\}",
				ProtoGenericsUtils.getClassFullName(fieldInformation.getCurrentType()));
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.getParseAttrXmlCodeForArray
	 * #getGlobalCode(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	public String getParseAttrXmlCodeForArray(FieldInformation fieldInformation) {
		return getParseNodeXmlCodeForArray(fieldInformation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.feinno.serialization.protobuf.generator.field.AbstractFieldInterpreter
	 * #getGlobalCode(com.feinno.serialization.protobuf.generator.field.
	 * FieldInformation)
	 */
	@Override
	public String getGlobalCode(FieldInformation fieldInformation) {
		String globalVariableCode = getGlobalCodeTemplate();
		globalVariableCode = globalVariableCode.replaceAll("\\$\\{proto_builder\\}",
				ClassUtil.getBuilderClassFullName(ProtoGenericsUtils.getClass(fieldInformation.getCurrentType())));
		globalVariableCode = globalVariableCode.replaceAll("\\$\\{builder_name\\}",
				getBuilderClassName(fieldInformation));
		globalVariableCode = globalVariableCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		globalVariableCode = globalVariableCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		return globalVariableCode;
	}

	private String getBuilderClassName(FieldInformation fieldInformation) {
		StringBuilder builderClassName = new StringBuilder(ClassUtil.getBuilderClassName(ProtoGenericsUtils
				.getClass(fieldInformation.getCurrentType())));
		builderClassName.setCharAt(0, Character.toLowerCase(builderClassName.charAt(0)));
		builderClassName.append(fieldInformation.getNumber());
		return builderClassName.toString();
	}

	protected String getWriteCodeTemplate() {
		return WRITE_CODE_TEMPLATE;
	}

	/**
	 * 获得将当前对象加入到JsonObject的代码，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getJsonObjectCodeTemplate() {
		return JSON_OBJECT_CODE_TEMPLATE;
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

	protected String getJsonCodeForArrayTemplate() {
		return JSON_OBJECT_CODE_ARRAY_TEMPLATE;
	}

	protected String getSizeCodeForArrayTemplate() {
		return SIZE_CODE_ARRAY_TEMPLATE;
	}

	protected String getParseCodeForArrayTemplate() {
		return PARSE_CODE_ARRAY_TEMPLATE;
	}

	protected String getGlobalCodeTemplate() {
		return GLOBAL_CODE_TEMPLATE;
	}
}
