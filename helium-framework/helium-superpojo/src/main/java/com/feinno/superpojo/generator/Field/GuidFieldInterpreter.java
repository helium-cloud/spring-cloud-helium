package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.generator.ProtoFieldType;

/**
 * 
 * <b>描述: </b>用于序列化组件，这是一个Guid类型的解释器，在自动生成源码的过程中发现类某一个字段为Guid类型时，会自动调用此解释器，
 * 用于在创建源码时解释此字段如何序列化( write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * <b>功能: </b>Guid类型的解释器,解释此字段如何序列化( write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到Guid类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class GuidFieldInterpreter extends AbstractFieldInterpreter {

	private static GuidFieldInterpreter INSTANCE;

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getGuid() != null){ output.writeTag(number, 2);com.feinno.serialization.protobuf.ProtoGuid.serialize(data.getGuid(), output, true);}</code>
	 * <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null){ ${output}.writeTag(${number}, 2);${package_util}.ProtoGuid.serialize(${data}.${getterName}(), ${output}, true);}";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>jsonObject.add("name",data.getName() != null ? data.getName() : null);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_TEMPLATE = "${jsonObject}.add(\"${fieldName}\",  ${data}.${getterName}() != null ? ${data}.${getterName}().toJsonObject() : null);";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>if(data.getGuid() != null) size += ( 19 + ${package_util}.CodedOutputStream.computeTagSize(${number}));</code>
	 * <br>
	 * 的输出,因为这个Guid是一个固定长度，因此这样输出了
	 */
	private static final String SIZE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) size += ( 19 + ${package_io}.CodedOutputStream.computeTagSize(${number}));";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>data.setGuid(ProtoGuid.deserialize(input););</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(${package_util}.ProtoGuid.deserialize(${input}));";

	/**
	 * 表示反序列化XML代码的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){input.nextEvent();data.setId(input.readInt());}</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${data}.${setterName}(${package_util}.ProtoGuid.valueOf(${input}));}";

	/**
	 * 表示反序列化代码XML的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){data.setId(input.readInt());}</code> <br>
	 * 的输出
	 */
	private static final String PARSE_ATTR_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${data}.${setterName}(${package_util}.ProtoGuid.valueOf(${input}));}";

	/**
	 * 表示反序列化XML代码的格式,用于数组，期待一个类似于<br>
	 * <code>fieldValue = input.readInt()</code> <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_ARRAY_TEMPLATE = "fieldValue = ${package_util}.ProtoGuid.valueOf(${input});";

	/**
	 * 用于数组或集合类时序列化代码的格式，期待一个类似<br>
	 * <code>output.writeTag(number, 2);com.feinno.serialization.protobuf.ProtoGuid.serialize(data.getGuid(), output, true);</code>
	 * <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_ARRAY_TEMPLATE = "if( value != null){ ${output}.writeTag(${number}, 2);${package_util}.ProtoGuid.serialize(value, ${output}, true);}";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>jsonArray.add(value.toJsonObject());</code> <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_ARRAY_TEMPLATE = "jsonArray.add(value.toJsonObject());";

	/**
	 * 用于数组或集合类时取序列化长度的代码格式，期待一个类似<br>
	 * <code>size += ( 19 + ${package_util}.protobuf.CodedOutputStream.computeTagSize(${number}));;</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ( 19 + ${package_io}.CodedOutputStream.computeTagSize(${number}));";

	/**
	 * 用于数组或集合类时反序列化代码的格式，期待一个类似于<br>
	 * <code>fieldValue = ProtoGuid.deserialize(input);</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = ${package_util}.ProtoGuid.deserialize(${input});";

	/**
	 * 构造方法必须要求持有字段枚举类型的引用
	 * 
	 * @param protoFieldType
	 */
	public GuidFieldInterpreter(ProtoFieldType protoFieldType) {
		super(protoFieldType);
	}

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public static synchronized GuidFieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new GuidFieldInterpreter(ProtoFieldType.GUID);
		}
		return INSTANCE;
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
		writeCode = writeCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		return writeCode;
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
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		sizeCode = sizeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		sizeCode = sizeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
		sizeCode = sizeCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
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
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		return parseCode;
	}

	/**
	 * 获得该字段在XML方式node反序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
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
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		return parseCode;
	}

	/**
	 * 获得该字段在XML方式attribute反序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseAttrXmlCode(FieldInformation fieldInformation) {
		if (getNodeType(fieldInformation) != NodeType.ATTR) {
			return "";
		}
		String parseCode = PARSE_ATTR_XML_CODE_TEMPLATE;
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		return parseCode;
	}

	/**
	 * 获得该字段在XML方式node反序列化时的代码，用于集合类
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseNodeXmlCodeForArray(FieldInformation fieldInformation) {
		String parseCode = PARSE_NODE_XML_CODE_ARRAY_TEMPLATE;
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
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
		writeCode = writeCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
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
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		sizeCode = sizeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		sizeCode = sizeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
		sizeCode = sizeCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
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
		parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
		return parseCode;
	}

	/**
	 * 获得序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
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

	/**
	 * 获得序列化长度计算代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getSizeCodeTemplate() {
		return SIZE_CODE_TEMPLATE;
	}

	/**
	 * 获得反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseCodeTemplate() {
		return PARSE_CODE_TEMPLATE;
	}

	/**
	 * 为数组或集合类使用的序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getWriteCodeForArrayTemplate() {
		return WRITE_CODE_ARRAY_TEMPLATE;
	}

	protected String getJsonCodeForArrayTemplate() {
		return JSON_OBJECT_CODE_ARRAY_TEMPLATE;
	}

	/**
	 * 为数组或集合类使用的序列化长度计算代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getSizeCodeForArrayTemplate() {
		return SIZE_CODE_ARRAY_TEMPLATE;
	}

	/**
	 * 为数组或集合类使用的反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseCodeForArrayTemplate() {
		return PARSE_CODE_ARRAY_TEMPLATE;
	}

}
