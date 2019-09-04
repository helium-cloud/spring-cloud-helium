package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.util.ProtoGenericsUtils;
import com.feinno.superpojo.util.StringUtils;

/**
 * <b>描述:
 * </b>用于序列化组件，这个ListFieldInterpreter是List类型的解释器，List因为泛型的关系，其中只能存储某一指定类型，
 * 因此本类作为一种适配器的角色出现，他可以适配任何类型为集合类型，只要该种类型的解释器实现了ForArray的方法既可。
 * 
 * <p>
 * <b>功能: </b>是List类型的解释器，对任何实现ForArray方法的解释器提供集合类序列化功能的实现
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到List类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ListFieldInterpreter extends AbstractFieldInterpreter {

	private static ListFieldInterpreter INSTANCE = null;

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>
	 * if (data.getString_List() != null){
	 * 		for (java.lang.String value : data.getString_List()) {
	 * 			if (value != null){
	 * 				output.writeString(11, value);
	 * 			}
	 * 		}
	 * 	}
	 * </code><br>
	 * 的输出
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) { for (${java_type} value : ${data}.${getterName}()) { if (value != null){ ${write_array} }}}";

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_NODE_TEMPLATE = "if( ${data}.${getterName}() != null) { for (${java_type} value : ${data}.${getterName}()) { if (value != null){ ${output}.writeStartElement(\"${fieldName}\");${output}.${writeType}(value);${output}.writeEndElement(\"${fieldName}\"); }}}";

	private static final String WRITE_XML_CODE_NODE_EXTENSIONS_START_TEMPLATE = "if( ${data}.${getterName}() != null) { ${output}.writeStartElement(\"${newParentNode}\"); }";

	private static final String WRITE_XML_CODE_NODE_EXTENSIONS_END_TEMPLATE = "if( ${data}.${getterName}() != null) {  ${output}.writeEndElement(\"${newParentNode}\"); }";

	/**
	 * 将当前对象变为Json的代码的格式，期待一个类似<br>
	 * <code>
	 * if (data.getString_Array() != null){
	 * 		com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray();
	 * 		for (java.lang.String value : data.getString_Array()) {
	 * 			if (value != null){
	 * 				com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value);
	 * 				jsonArray.add(jsonPrimitive);
	 * 			}
	 * 		}
	 * 		jsonObject.add("array",jsonArray);
	 * 	}
	 * </code><br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) { com.google.gson.JsonArray jsonArray = new com.google.gson.JsonArray(); for (${java_type} value : ${data}.${getterName}()) { if (value != null){ ${jsonObject_array} }}${jsonObject}.add(\"${fieldName}\",jsonArray);}";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>
	 * if (data.getString_List() != null){
	 * 		for (java.lang.String value : data.getString_List()) {
	 * 			if (value != null)
	 * 				size += com.feinno.serialization.protobuf.CodedOutputStream.computeStringSize(11, value);
	 * 		}
	 * 	}
	 * 	</code> <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) { for (${java_type} value : ${data}.${getterName}()) { if (value != null){ ${size_array} }}}";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * java.util.List<java.lang.String> list = data.getString_Array();
	 * 			if (list == null){
	 * 				list = new java.util.ArrayList<java.lang.String>();
	 * 				data.setString_List(list);
	 * 			}
	 * 			String fieldValue = null;
	 * 			fieldValue = input.readString();
	 * 			if(fieldValue != null){
	 * 	 			list.add(fieldValue);
	 * 			 }
	 * </code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "java.util.List<${java_type}> list = ${data}.${getterName}(); if (list == null){ list = new java.util.ArrayList<${java_type}>();  ${data}.${setterName}(list); } ${java_type} fieldValue = null; ${parse_array} if(fieldValue != null) { list.add(fieldValue);} ";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * else if(name.equals(\"${fieldName}\")) {
	 * 	java.util.List<${java_type}> list = ${data}.${getterName}(); 
	 * 	if (list == null){ 
	 * 		list = new java.util.ArrayList<${java_type}>();  
	 * 		${data}.${setterName}(list); 
	 * 	} 
	 * 	${java_type} fieldValue = null; 
	 * 	${parse_array} 
	 * if(fieldValue != null) {
	 *  list.add(fieldValue);} 
	 * }
	 * </code><br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {java.util.List<${java_type}> list = ${data}.${getterName}(); if (list == null){ list = new java.util.ArrayList<${java_type}>();  ${data}.${setterName}(list); } ${java_type} fieldValue = null; ${parse_array} if(fieldValue != null) { list.add(fieldValue);} }";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * else if(name.equals(\"${newParentNode}\")){parentName = name;} 
	 * else if(parentName.equals(\"${newParentNode}\") && name.equals(\"${fieldName}\")) {
	 * 	java.util.List<${java_type}> list = ${data}.${getterName}(); 
	 * 	if (list == null){ 
	 * 		list = new java.util.ArrayList<${java_type}>();  
	 * 		${data}.${setterName}(list); 
	 * 	} 
	 * 	${java_type} fieldValue = null; 
	 * 	${parse_array} 
	 * if(fieldValue != null) {
	 *  list.add(fieldValue);} 
	 * }
	 * </code><br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_EXTENSIONS_CODE_TEMPLATE = "else if(name.equals(\"${newParentNode}\")){parentName = name;} else if(parentName.equals(\"${newParentNode}\") && name.equals(\"${fieldName}\")) {java.util.List<${java_type}> list = ${data}.${getterName}(); if (list == null){ list = new java.util.ArrayList<${java_type}>();  ${data}.${setterName}(list); } ${java_type} fieldValue = null; ${parse_array} if(fieldValue != null) { list.add(fieldValue);} }";

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ListFieldInterpreter(ProtoFieldType.LIST);
		}
		return INSTANCE;
	}

	private ListFieldInterpreter(ProtoFieldType protoFieldType) {
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
		// 找到想要适配的字段类型，通过字段类型来找出相应的解释器
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
		String writeCode = getWriteCodeTemplate();
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeCode = writeCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		writeCode = writeCode.replaceAll(
				"\\$\\{write_array\\}",
				ProtoFieldType
						.valueOf(fieldType)
						.getFieldInterpreter()
						.getWriteCodeForArray(
								fieldInformation.setCurrentType(ProtoGenericsUtils.getGenericType(
										fieldInformation.getField(), 0))));

		return writeCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getWriteNodeXmlCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getWriteNodeXmlCode(FieldInformation fieldInformation) {
		if (getNodeType(fieldInformation) != NodeType.NODE) {
			return "";
		}
		// 找到想要适配的字段类型，通过字段类型来找出相应的解释器
		String writeCode = WRITE_XML_CODE_NODE_TEMPLATE;
		String newParentNode = null;
		if (fieldInformation.getAnnoFieldExtensions() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoFieldExtensions().newParentNode())) {
			writeCode = WRITE_XML_CODE_NODE_EXTENSIONS_START_TEMPLATE + writeCode
					+ WRITE_XML_CODE_NODE_EXTENSIONS_END_TEMPLATE;
			newParentNode = fieldInformation.getAnnoFieldExtensions().newParentNode();
		}
		if (fieldInformation.getAnnoChilds() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoChilds().parent())) {
			writeCode = WRITE_XML_CODE_NODE_EXTENSIONS_START_TEMPLATE + writeCode
					+ WRITE_XML_CODE_NODE_EXTENSIONS_END_TEMPLATE;
			newParentNode = fieldInformation.getAnnoChilds().parent();
		}
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeCode = writeCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));

		writeCode = writeCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		if (fieldInformation.getAnnoField() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoField().name())) {
			writeCode = writeCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getAnnoField().name());
		} else if (fieldInformation.getAnnoChilds() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoChilds().child())) {
			writeCode = writeCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getAnnoChilds().child());
		} else {
			writeCode = writeCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getField().getName());
		}
		if (fieldInformation.getAnnoField() != null && fieldInformation.getAnnoField().isCDATA()) {
			writeCode = writeCode.replaceAll("\\$\\{writeType\\}", "writeCDATA");
		} else {
			writeCode = writeCode.replaceAll("\\$\\{writeType\\}", "write");
		}
		if (newParentNode != null) {
			writeCode = writeCode.replaceAll("\\$\\{newParentNode\\}", newParentNode);
		}

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
		// 找到想要适配的字段类型，通过字段类型来找出相应的解释器
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
		// 如果是Byte类型的，则使用优化的过的Byte类型解释器
		FieldInterpreter fieldInterpreter = fieldType == java.lang.Byte.class ? WrapsFieldInterpreter
				.getOptimizationByteFieldInterpreter() : ProtoFieldType.valueOf(fieldType).getFieldInterpreter();
		String jsonObjectCode = getJsonObjectCodeTemplate();
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{jsonObject\\}", VARIABLE_NAME_JSON_OBJECT);
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getField().getName());
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{jsonObject_array\\}", fieldInterpreter
				.getJsonCodeForArray(fieldInformation.setCurrentType(ProtoGenericsUtils.getGenericType(
						fieldInformation.getField(), 0))));

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
		// 找到想要适配的字段类型，通过字段类型来找出相应的解释器
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
		String sizeCode = getSizeCodeTemplate();
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		sizeCode = sizeCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		sizeCode = sizeCode.replaceAll(
				"\\$\\{size_array\\}",
				ProtoFieldType
						.valueOf(fieldType)
						.getFieldInterpreter()
						.getSizeCodeForArray(
								fieldInformation.setCurrentType(ProtoGenericsUtils.getGenericType(
										fieldInformation.getField(), 0))));
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
		// 找到想要适配的字段类型，通过字段类型来找出相应的解释器
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
		String parseCode = getParseCodeTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		parseCode = parseCode.replaceAll(
				"\\$\\{parse_array\\}",
				ProtoFieldType
						.valueOf(fieldType)
						.getFieldInterpreter()
						.getParseCodeForArray(
								fieldInformation.setCurrentType(ProtoGenericsUtils.getGenericType(
										fieldInformation.getField(), 0))));
		return parseCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getParseNodeXmlCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getParseNodeXmlCode(FieldInformation fieldInformation) {
		if (getNodeType(fieldInformation) != NodeType.NODE) {
			return "";
		}
		// 找到想要适配的字段类型，通过字段类型来找出相应的解释器
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
		String parseCode = PARSE_NODE_XML_CODE_TEMPLATE;
		String newParentNode = null;
		if (fieldInformation.getAnnoFieldExtensions() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoFieldExtensions().newParentNode())) {
			parseCode = PARSE_NODE_XML_EXTENSIONS_CODE_TEMPLATE;
			newParentNode = fieldInformation.getAnnoFieldExtensions().newParentNode();
		}
		if (fieldInformation.getAnnoChilds() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoChilds().parent())) {
			parseCode = PARSE_NODE_XML_EXTENSIONS_CODE_TEMPLATE;
			newParentNode = fieldInformation.getAnnoChilds().parent();
		}
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		parseCode = parseCode.replaceAll(
				"\\$\\{parse_array\\}",
				ProtoFieldType
						.valueOf(fieldType)
						.getFieldInterpreter()
						.getParseNodeXmlCodeForArray(
								fieldInformation.setCurrentType(ProtoGenericsUtils.getGenericType(
										fieldInformation.getField(), 0))));
		if (newParentNode != null) {
			parseCode = parseCode.replaceAll("\\$\\{newParentNode\\}", newParentNode);
		}
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
	 * 此时必须覆盖这个方法,因为当前的tag类型，以其泛型所表示的类型相同
	 * 
	 * @return
	 */
	public int getTagType(FieldInformation fieldInformation) {
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getCurrentType(), 0);
		return ProtoFieldType.valueOf(fieldType).getFieldInterpreter().getTagType(fieldInformation);

	}
}
