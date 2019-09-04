package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.io.WireFormat;
import com.feinno.superpojo.util.ClassUtil;
import com.feinno.superpojo.util.ProtoGenericsUtils;
import com.feinno.superpojo.util.StringUtils;

/**
 * 
 * <b>描述:
 * </b>用于序列化组件，这个ArrayFieldInterpreter是数组类型的解释器，数组因为可以包含任何类型，所以本类作为一种适配器的角色出现，
 * 他可以适配任何类型为数组类型，只要该种类型的解释器实现了ForArray的方法。
 * <p>
 * <b>功能: </b>ArrayFieldInterpreter是数组类型的解释器
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到数组类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ArrayFieldInterpreter extends AbstractFieldInterpreter {

	private static ArrayFieldInterpreter INSTANCE = null;

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>
	 * if (data.getString_Array() != null){
	 * 		for (java.lang.String value : data.getString_Array()) {
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

	private static final String WRITE_XML_CODE_NODE_EXTENSIONS_START_TEMPLATE = "${output}.writeStartElement(\"${newParentNode}\"); ";

	private static final String WRITE_XML_CODE_NODE_EXTENSIONS_END_TEMPLATE = " ${output}.writeEndElement(\"${newParentNode}\");";

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
	 * if (data.getString_Array() != null){
	 * 		for (java.lang.String value : data.getString_Array()) {
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
	 * List<?> listTemp = arrayTypetMap.get(1);
	 * 			if (listTemp == null){
	 * 				listTemp = new java.util.ArrayList<?>();
	 * 				arrayTypetMap.put(1,listTemp);
	 * 			}
	 * 			String fieldValue = null;
	 * 			fieldValue = input.readString();
	 * 			if(fieldValue != null){
	 * 				listTemp.add(fieldValue);
	 * 			}
	 * </code><br>
	 * 的输出,这样的输出是为了能够将没一个数组首先以List的方式存储到MAP中，在所有数组中的内容都反序列化完毕后，再将这个List转换为数组
	 */
	private static final String PARSE_CODE_TEMPLATE = "@SuppressWarnings(\"unchecked\") java.util.List<${java_type}> listTemp = (java.util.List<${java_type}>)arrayTypetMap.get(${number}); if (listTemp == null){ listTemp = new java.util.LinkedList<${java_type}>();  arrayTypetMap.put(${number},listTemp); } ${java_type} fieldValue = null; ${parse_array} if(fieldValue != null){listTemp.add(fieldValue);}";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * else if(name.equals(\"${fieldName}\")) {
	 * 	List<?> listTemp = arrayTypetMap.get(1);
	 * 	if (listTemp == null){
	 * 				listTemp = new java.util.ArrayList<?>();
	 * 				arrayTypetMap.put(1,listTemp);
	 * 	}
	 * 	${java_type} fieldValue = null; 
	 * 	${parse_array} 
	 * if(fieldValue != null) {
	 *  list.add(fieldValue);} 
	 * }
	 * </code><br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {java.util.List<${java_type}> list = (java.util.List<${java_type}>)arrayTypetMap.get(${number}); if (list == null){ list = new java.util.ArrayList<${java_type}>(); arrayTypetMap.put(${number},list);} ${java_type} fieldValue = null; ${parse_array} if(fieldValue != null) { list.add(fieldValue);} }";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>
	 * else if(name.equals(\"${newParentNode}\")){parentName = name;} 
	 * else if(parentName.equals(\"${newParentNode}\") && name.equals(\"${fieldName}\")) {
	 * 	List<?> listTemp = arrayTypetMap.get(1);
	 * 	if (listTemp == null){
	 * 				listTemp = new java.util.ArrayList<?>();
	 * 				arrayTypetMap.put(1,listTemp);
	 * 	}
	 * 	${java_type} fieldValue = null; 
	 * 	${parse_array} 
	 * if(fieldValue != null) {
	 *  list.add(fieldValue);} 
	 * }
	 * </code><br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_EXTENSIONS_CODE_TEMPLATE = "else if(name.equals(\"${newParentNode}\")){parentName = name;} else if(parentName.equals(\"${newParentNode}\") && name.equals(\"${fieldName}\")) {java.util.List<${java_type}> list = (java.util.List<${java_type}>)arrayTypetMap.get(${number});   if (list == null){ list = new java.util.ArrayList<${java_type}>(); arrayTypetMap.put(${number},list);}  ${java_type} fieldValue = null; ${parse_array} if(fieldValue != null) { list.add(fieldValue);} }";

	/**
	 * 在所有数组中的内容都反序列化完毕后，再将这个List转换为数组<br>
	 * <code>
	 * if (arrayTypetMap.get(513) != null) {
	 * 		java.lang.Boolean[] array = new java.lang.Boolean[arrayTypetMap.get(513).size()];
	 * 		${package_util}.ArrayUtil.listToArray(arrayTypetMap.get(513),array);
	 * 		data.setBoolean_Object_Array(array);
	 * 	}
	 * </code>
	 */
	private static final String CONVER_ARRAY_CODE_TEMPLATE = "if(arrayTypetMap.get(${number}) != null && arrayTypetMap.get(${number}).size() > 0){ ${java_type}[] array = new ${java_type}[arrayTypetMap.get(${number}).size()];${package_util}.ArrayUtil.listToArray(arrayTypetMap.get(${number}),array); ${data}.${setterName}(array);}";

	/**
	 * 单例模式，获得当前对象
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ArrayFieldInterpreter(ProtoFieldType.ARRAY);
		}
		return INSTANCE;
	}

	private ArrayFieldInterpreter(ProtoFieldType protoFieldType) {
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
		// 如果是Byte类型的，则使用优化的过的Byte类型解释器
		FieldInterpreter fieldInterpreter = fieldType == java.lang.Byte.class ? WrapsFieldInterpreter
				.getOptimizationByteFieldInterpreter() : ProtoFieldType.valueOf(fieldType).getFieldInterpreter();
		String writeCode = getWriteCodeTemplate();
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeCode = writeCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		writeCode = writeCode.replaceAll("\\$\\{write_array\\}", fieldInterpreter.getWriteCodeForArray(fieldInformation
				.setCurrentType(ProtoGenericsUtils.getGenericType(fieldInformation.getField(), 0))));
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
		// 如果是Byte类型的，则使用优化的过的Byte类型解释器
		FieldInterpreter fieldInterpreter = fieldType == java.lang.Byte.class ? WrapsFieldInterpreter
				.getOptimizationByteFieldInterpreter() : ProtoFieldType.valueOf(fieldType).getFieldInterpreter();
		String sizeCode = getSizeCodeTemplate();
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		sizeCode = sizeCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		sizeCode = sizeCode.replaceAll("\\$\\{size_array\\}", fieldInterpreter.getSizeCodeForArray(fieldInformation
				.setCurrentType(ProtoGenericsUtils.getGenericType(fieldInformation.getField(), 0))));
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
		// 如果是Byte类型的，则使用优化的过的Byte类型解释器
		FieldInterpreter fieldInterpreter = fieldType == java.lang.Byte.class ? WrapsFieldInterpreter
				.getOptimizationByteFieldInterpreter() : ProtoFieldType.valueOf(fieldType).getFieldInterpreter();
		String ParseCode = getParseCodeTemplate();
		ParseCode = ParseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		ParseCode = ParseCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		ParseCode = ParseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		ParseCode = ParseCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		ParseCode = ParseCode.replaceAll("\\$\\{java_type\\}",
				ProtoGenericsUtils.getGenericsClassFullName(fieldInformation.getField(), 0));
		ParseCode = ParseCode.replaceAll("\\$\\{parse_array\\}", fieldInterpreter.getParseCodeForArray(fieldInformation
				.setCurrentType(ProtoGenericsUtils.getGenericType(fieldInformation.getField(), 0))));
		return ParseCode;
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
		parseCode = parseCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
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
	 * 获得数组类型的转换代码，因为数组的长度是定长，在存储时为了节省效率以及代码简洁，我们通常使用List进行存储，后再转换成数组
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public String getConverArrayCode(FieldInformation fieldInformation) {
		Class<?> fieldType = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
		String converArrayCode = getConverArrayCodeTemplate();
		converArrayCode = converArrayCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		converArrayCode = converArrayCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
		converArrayCode = converArrayCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		converArrayCode = converArrayCode.replaceAll("\\$\\{java_type\\}",
				ClassUtil.processClassName(fieldType.getName()));
		converArrayCode = converArrayCode.replaceAll("\\$\\{number\\}",
				String.valueOf(fieldInformation.getCurrentNumber()));
		return converArrayCode;
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
	 * 获得数组转换的模板
	 * 
	 * @return
	 */
	protected String getConverArrayCodeTemplate() {
		return CONVER_ARRAY_CODE_TEMPLATE;
	}

	/**
	 * 此时必须覆盖这个方法,因为当前的tag类型，以其泛型所表示的类型相同
	 * 
	 * @return
	 */
	public int getTagType(FieldInformation fieldInformation) {
		Class<?> fieldType = ProtoGenericsUtils.getClass(fieldInformation.getCurrentType());

		if ((fieldType == byte.class || fieldType == java.lang.Byte.class)) {
			// byte数组类型太特殊了。。。byte数组类型可以一次性写入流中，而在流中对象表示是WireFormat.WIRETYPE_LENGTH_DELIMITED,Google规定
			return WireFormat.WIRETYPE_LENGTH_DELIMITED;
		} else {
			return ProtoFieldType.valueOf(fieldType).getFieldInterpreter().getTagType(fieldInformation);
		}
	}
}
