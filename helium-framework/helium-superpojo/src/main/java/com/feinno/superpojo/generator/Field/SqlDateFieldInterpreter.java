package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.annotation.TimeZone;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.util.ProtoGenericsUtils;

/**
 * <b>描述:
 * </b>用于序列化组件，这是一个java.sql.Date类型的解释器，在自动生成源码的过程中发现类某一个字段为java.sql.Date类型时
 * ，会自动调用此解释器， 用于在创建源码时解释此字段如何序列化( write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * <b>功能: </b>java.sql.Date类型的解释器,解释java.sql.Date类型字段如何序列化(
 * write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到java.sql.Date类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class SqlDateFieldInterpreter extends AbstractFieldInterpreter {

	private static FieldInterpreter INSTANCE = null;

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>
	 * if(data.getData_obj() != null){
	 * 		java.sql.Date date = data.getData_obj();
	 * 		if(protoMember.timezone().equalsIgnoreCase("UTC")){
	 * 			date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);
	 * 		}
	 * 		Long millisecond = (date.getTime() + TimeZone.getDefault().getOffset()) * 10000;
	 * 		if (millisecond >= 2534022719990000000L)
	 * 			millisecond = 2534023007999999999L;
	 * 		else if (millisecond < -621355968000000000L)
	 * 			millisecond = -621355968000000000L;
	 * 		output.writeFixed64(fieldNumber, millisecond);
	 * }
	 * </code> <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null){ java.sql.Date date = ${data}.${getterName}(); ${getGMTDate_code} Long millisecond = (date.getTime() + java.util.TimeZone.getDefault().getOffset(date.getTime())) * 10000;if (millisecond >= 2534022719990000000L)millisecond = 2534023007999999999L;else if (millisecond < -621355968000000000L)millisecond = -621355968000000000L;${output}.write${streamType}(${number}, millisecond);}";

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_NODE_TEMPLATE = "${output}.writeStartElement(\"${fieldName}\");${output}.${writeType}(${data}.${getterName}(),\"${format}\");${output}.writeEndElement(\"${fieldName}\");";

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_ATTR_TEMPLATE = "${output}.writeAttribute(\"${fieldName}\",${data}.${getterName}(),\"${format}\");";
	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>jsonObject.addProperty("time",data.getName() != null ? "2012-10-10 10:10:00" : null);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_TEMPLATE = "if(com.feinno.superpojo.generator.ObjectUtils.isNull(${data}.${getterName}()))${jsonObject}.addProperty(\"${fieldName}\",  ${data}.${getterName}() != null ? ${package_util}.Formater.createDateFormat().format(${data}.${getterName}()) : null);";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>
	 * * if(data.getData_obj() != null){
	 * 		java.sql.Date date = data.getData_obj();
	 * 		if(protoMember.timezone().equalsIgnoreCase("UTC")){
	 * 			date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);
	 * 		}
	 * 		Long millisecond = (date.getTime() + TimeZone.getDefault().getOffset()) * 10000;
	 * 		if (millisecond >= 2534022719990000000L)
	 * 			millisecond = 2534023007999999999L;
	 * 		else if (millisecond < -621355968000000000L)
	 * 			millisecond = -621355968000000000L;
	 * 		size += com.feinno.serialization.protobuf.CodedOutputStream.computeFixed64Size(fieldNumber, millisecond);
	 * }
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null){ java.sql.Date date = ${data}.${getterName}(); ${getGMTDate_code} Long millisecond = (date.getTime() + java.util.TimeZone.getDefault().getOffset(date.getTime())) * 10000;if (millisecond >= 2534022719990000000L)millisecond = 2534023007999999999L;else if (millisecond < -621355968000000000L)millisecond = -621355968000000000L;size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},millisecond);}";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * long l = input.readFixed64();
	 * 	if (l > 2534022719990000000L)
	 * 		l = 2534022719990000000L / 10000;
	 * 	else if (l == -621355968000000000L) {
	 * 		l = -621357696000000000L;
	 * 		l = l / 10000 - TimeZone.getDefault().getOffset(l / 10000);
	 * 	} else {
	 * 		l = l / 10000 - TimeZone.getDefault().getOffset(l / 10000);
	 * 	}
	 * 	java.sql.Date date = new java.sql.Date(l);
	 * 	if(protoMember.timezone().equals("UTC")){
	 * 		date = com.feinno.superpojo.util.DateUtil.getUTCDate(date);
	 * 	}
	 * data.setDate_obj(date);</code> <br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "long l = ${input}.read${streamType}();if (l > 2534022719990000000L)l = 2534022719990000000L / 10000;else if (l == -621355968000000000L) {l = -621357696000000000L;l = l / 10000 - java.util.TimeZone.getDefault().getOffset(l / 10000);} else {l = l / 10000 - java.util.TimeZone.getDefault().getOffset(l / 10000);}java.sql.Date date = new java.sql.Date(l); ${getGMTDate_code} ${data}.${setterName}(date);";

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>
	 * java.sql.Date date = value;
	 * 	if(protoMember.timezone().equalsIgnoreCase("UTC")){
	 * 		date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);
	 * 	}
	 * 	Long millisecond = (date.getTime() + TimeZone.getDefault().getOffset(date.getTime())) * 10000;
	 * 	if (millisecond >= 2534022719990000000L)
	 * 		millisecond = 2534023007999999999L;
	 * 	else if (millisecond < -621355968000000000L)
	 * 		millisecond = -621355968000000000L;
	 * 	output.writeFixed64(fieldNumber, millisecond);
	 * </code> <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_ARRAY_TEMPLATE = " java.sql.Date date = value; ${getGMTDate_code} Long millisecond = (date.getTime() + java.util.TimeZone.getDefault().getOffset(date.getTime())) * 10000;if (millisecond >= 2534022719990000000L)millisecond = 2534023007999999999L;else if (millisecond < -621355968000000000L)millisecond = -621355968000000000L;${output}.write${streamType}(${number}, millisecond);";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value.toString());jsonArray.add(jsonPrimitive);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_ARRAY_TEMPLATE = "com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(${package_util}.Formater.createDateFormat().format(value));jsonArray.add(jsonPrimitive);";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>
	 * java.sql.Date date = value;
	 * 	if(protoMember.timezone().equalsIgnoreCase("UTC")){
	 * 		date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);
	 * 	}
	 * 	Long millisecond = (date.getTime() + TimeZone.getDefault().getOffset(date.getTime())) * 10000;
	 * 	if (millisecond >= 2534022719990000000L)
	 * 		millisecond = 2534023007999999999L;
	 * 	else if (millisecond < -621355968000000000L)
	 * 		millisecond = -621355968000000000L;
	 * 	size += com.feinno.serialization.protobuf.CodedOutputStream.computeFixed64Size(fieldNumber, millisecond);
	 * </code> <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_ARRAY_TEMPLATE = "java.sql.Date date = value; ${getGMTDate_code} Long millisecond = (date.getTime() + java.util.TimeZone.getDefault().getOffset(date.getTime())) * 10000;if (millisecond >= 2534022719990000000L)millisecond = 2534023007999999999L;else if (millisecond < -621355968000000000L)millisecond = -621355968000000000L;size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},millisecond);";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * long l = input.readFixed64();
	 * 	if (l > 2534022719990000000L)
	 * 		l = 2534022719990000000L / 10000;
	 * 	else if (l == -621355968000000000L) {
	 * 		l = -621357696000000000L;
	 * 		l = l / 10000 - timeZone.getOffset(l / 10000);
	 * 	} else {
	 * 		l = l / 10000 - timeZone.getOffset(l / 10000);
	 * 	}
	 * 	java.sql.Date date = new java.sql.Date(l);
	 * 	if(protoMember.timezone().equals("UTC")){
	 * 		date = com.feinno.superpojo.util.DateUtil.getUTCDate(date);
	 * 	}
	 * fieldValue = date;
	 * </code> <br>
	 * 的输出
	 */
	private static final String PARSE_CODE_ARRAY_TEMPLATE = "long l = ${input}.read${streamType}();if (l > 2534022719990000000L)l = 2534022719990000000L / 10000;else if (l == -621355968000000000L) {l = -621357696000000000L;l = l / 10000 - java.util.TimeZone.getDefault().getOffset(l / 10000);} else {l = l / 10000 - java.util.TimeZone.getDefault().getOffset(l / 10000);}java.sql.Date date = new java.sql.Date(l); ${getGMTDate_code} fieldValue = date;";

	/**
	 * 表示反序列化XML代码的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){input.nextEvent();data.setId(input.readInt());}</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${input}.nextEvent(); ${data}.${setterName}(${input}.read${xmlType}(\"${format}\"));}";

	/**
	 * 表示反序列化代码XML的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){data.setId(input.readInt());}</code> <br>
	 * 的输出
	 */
	private static final String PARSE_ATTR_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${data}.${setterName}(${input}.read${xmlType}(\"${format}\"));}";

	/**
	 * 表示反序列化XML代码的格式,用于数组，期待一个类似于<br>
	 * <code>fieldValue = input.readInt()</code> <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_ARRAY_TEMPLATE = "fieldValue = ${input}.read${xmlType}(\"${format}\");";

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SqlDateFieldInterpreter(ProtoFieldType.SQLDATE);
		}
		return INSTANCE;
	}

	private SqlDateFieldInterpreter(ProtoFieldType protoFieldType) {
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
		if (fieldInformation.getAnnoField().timeZone() == TimeZone.UTC) {
			writeCode = writeCode.replaceAll("\\$\\{getGMTDate_code\\}",
					"date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);");
		} else {
			writeCode = writeCode.replaceAll("\\$\\{getGMTDate_code\\}", "");
		}
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
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		if (fieldInformation.getAnnoField() != null && fieldInformation.getAnnoField().isCDATA()) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "writeCDATA");
		} else {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "write");
		}
		if (fieldInformation.getAnnoField() != null) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{format\\}", fieldInformation.getAnnoField().format());
		}
		return writeXmlCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.superpojo.generator.Field.protobuf.FieldInterpreter#
	 * getWriteAttrXmlCode
	 * (com.feinno.superpojo.generator.Field.protobuf.FieldInformation)
	 */
	public String getWriteAttrXmlCode(FieldInformation fieldInformation) {
		if (getNodeType(fieldInformation) != NodeType.ATTR) {
			return "";
		}
		String writeXmlCode = WRITE_XML_CODE_ATTR_TEMPLATE;
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		if (fieldInformation.getAnnoField() != null && fieldInformation.getAnnoField().isCDATA()) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "writeCDATA");
		} else {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "write");
		}
		if (fieldInformation.getAnnoField() != null) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{format\\}", fieldInformation.getAnnoField().format());
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
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
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
		if (fieldInformation.getAnnoField().timeZone() == TimeZone.UTC) {
			sizeCode = sizeCode.replaceAll("\\$\\{getGMTDate_code\\}",
					"date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);");
		} else {
			sizeCode = sizeCode.replaceAll("\\$\\{getGMTDate_code\\}", "");
		}
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
		if (fieldInformation.getAnnoField().timeZone() == TimeZone.UTC) {
			parseCode = parseCode.replaceAll("\\$\\{getGMTDate_code\\}",
					"date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);");
		} else {
			parseCode = parseCode.replaceAll("\\$\\{getGMTDate_code\\}", "");
		}
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
		if (fieldInformation.getAnnoField().timeZone() == TimeZone.UTC) {
			writeCode = writeCode.replaceAll("\\$\\{getGMTDate_code\\}",
					"date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);");
		} else {
			writeCode = writeCode.replaceAll("\\$\\{getGMTDate_code\\}", "");
		}
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
		String JsonCode = getJsonCodeForArrayTemplate();
		JsonCode = JsonCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		return JsonCode;
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
		if (fieldInformation.getAnnoField().timeZone() == TimeZone.UTC) {
			sizeCode = sizeCode.replaceAll("\\$\\{getGMTDate_code\\}",
					"date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);");
		} else {
			sizeCode = sizeCode.replaceAll("\\$\\{getGMTDate_code\\}", "");
		}
		return sizeCode;
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
		String parseCode = getParseNodeXmlCodeForTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		if (fieldInformation.getAnnoField() != null) {
			parseCode = parseCode.replaceAll("\\$\\{format\\}", fieldInformation.getAnnoField().format());
		}
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
		String parseCode = getParseAttrXmlCodeForTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		if (fieldInformation.getAnnoField() != null) {
			parseCode = parseCode.replaceAll("\\$\\{format\\}", fieldInformation.getAnnoField().format());
		}
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
		if (fieldInformation.getAnnoField() != null) {
			parseCode = parseCode.replaceAll("\\$\\{format\\}", fieldInformation.getAnnoField().format());
		}
		return parseCode;
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
		parseCode = parseCode.replaceAll("\\$\\{enum_class\\}",
				ProtoGenericsUtils.getClassFullName(fieldInformation.getCurrentType()));
		if (fieldInformation.getAnnoField().timeZone() == TimeZone.UTC) {
			parseCode = parseCode.replaceAll("\\$\\{getGMTDate_code\\}",
					"date = com.feinno.superpojo.util.DateUtil.getGMTDate(date);");
		} else {
			parseCode = parseCode.replaceAll("\\$\\{getGMTDate_code\\}", "");
		}
		return parseCode;
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

	/**
	 * 为XML使用的反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseNodeXmlCodeForTemplate() {
		return PARSE_NODE_XML_CODE_TEMPLATE;
	}

	/**
	 * 为XML使用的反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseAttrXmlCodeForTemplate() {
		return PARSE_ATTR_XML_CODE_TEMPLATE;
	}

	/**
	 * 为数组或集合类使用的序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getJsonCodeForArrayTemplate() {
		return JSON_OBJECT_CODE_ARRAY_TEMPLATE;
	}

	protected String getSizeCodeForArrayTemplate() {
		return SIZE_CODE_ARRAY_TEMPLATE;
	}

	protected String getParseCodeForArrayTemplate() {
		return PARSE_CODE_ARRAY_TEMPLATE;
	}
}
