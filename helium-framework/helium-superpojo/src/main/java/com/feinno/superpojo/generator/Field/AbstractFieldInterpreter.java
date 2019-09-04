package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.annotation.ProtoType;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.util.StringUtils;

/**
 * 
 * <b>描述:
 * </b>当使用Protobuf格式进行序列化或反序列化时，会先生成对应类的序列化辅助类代码，在生成辅助代码时，会逐个遍历待处理类的待序列化字段，
 * 根据字段类型及 注释{@link ProtoMember}信息 ，生成每个字段的序列化处理代码，再将全部字段的序列化处理代码组合起来，创建出序列化辅助类
 * ，该类就是提供每种字段类型应该如何进行序列化处理的代码的抽象类父类,它提供了在处理每一种字段类型时的基础方法与公共实现.
 * <p>
 * <b>功能: </b>用于序列化组件在生成辅助代码时使用，由它来决定每一种字段类型应该如何序列化、如何反序列化以及如何获取序列化的长度
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public abstract class AbstractFieldInterpreter implements FieldInterpreter {

	/** 变量名称的命名,待序列化的数据对象命名 */
	public static final String VARIABLE_NAME_DATA = "data";

	/** 变量名称的命名,输入流的命名 */
	public static final String VARIABLE_NAME_INPUTSTREAM = "input";

	/** 变量名称的命名,输出流的命名 */
	public static final String VARIABLE_NAME_OUTPUTSTREAM = "output";

	/** 变量名称的命名,JsonObject的名称 */
	public static final String VARIABLE_NAME_JSON_OBJECT = "jsonObject";

	/** 持有的字段类型的枚举引用 */
	protected ProtoFieldType protoFieldType;

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_NODE_TEMPLATE = "${output}.writeStartElement(\"${fieldName}\");${output}.${writeType}(${data}.${getterName}());${output}.writeEndElement(\"${fieldName}\");";

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_ATTR_TEMPLATE = "${output}.writeAttribute(\"${fieldName}\",${data}.${getterName}());";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>if(data.getGuid() == null) return false;</code><br>
	 * 的输出
	 */
	private static final String REQUIRED_CODE_TEMPLATE = "if( ${data}.${getterName}() == null) return false;";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>jsonObject.addProperty("name",data.getName() != null ? "Feinno" : null);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_TEMPLATE = "if(com.feinno.superpojo.generator.ObjectUtils.isNull(${data}.${getterName}()))${jsonObject}.addProperty(\"${fieldName}\",  ${data}.${getterName}() != null ? ${data}.${getterName}().toString() : null);";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value.toString());jsonArray.add(jsonPrimitive);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_ARRAY_TEMPLATE = "com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value.toString());jsonArray.add(jsonPrimitive);";

	/**
	 * 表示反序列化XML代码的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){input.nextEvent();data.setId(input.readInt());}</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${input}.nextEvent(); ${data}.${setterName}(${input}.read${xmlType}());}";

	/**
	 * 表示反序列化代码XML的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){data.setId(input.readInt());}</code> <br>
	 * 的输出
	 */
	private static final String PARSE_ATTR_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${data}.${setterName}(${input}.read${xmlType}());}";

	/**
	 * 表示反序列化XML代码的格式,用于数组，期待一个类似于<br>
	 * <code>fieldValue = input.readInt()</code> <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_ARRAY_TEMPLATE = "fieldValue = ${input}.read${xmlType}();";

	/**
	 * 构造方法要求将正确的枚举对象类型传入
	 * 
	 * @param protoFieldType
	 */
	public AbstractFieldInterpreter(ProtoFieldType protoFieldType) {
		this.protoFieldType = protoFieldType;
	}

	/**
	 * 获取作用域为当前实例的代码
	 * 
	 * @param fieldInformation
	 * @return
	 */
	@Override
	public String getGlobalCode(FieldInformation fieldInformation) {
		return null;
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
		String writeXmlCode = getWriteNodeXmlCodeTemplate();
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		if (fieldInformation.getAnnoField() != null && fieldInformation.getAnnoField().isCDATA()) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "writeCDATA");
		} else {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "write");
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
		String writeXmlCode = getWriteAttrXmlCodeTemplate();
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeXmlCode = writeXmlCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		if (fieldInformation.getAnnoField() != null && fieldInformation.getAnnoField().isCDATA()) {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "writeCDATA");
		} else {
			writeXmlCode = writeXmlCode.replaceAll("\\$\\{writeType\\}", "write");
		}
		return writeXmlCode;
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
		String requiredCode = REQUIRED_CODE_TEMPLATE;
		requiredCode = requiredCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		requiredCode = requiredCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		return requiredCode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.feinno.serialization.protobuf.generator.field.FieldInterpreter#
	 * getJsonCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	public String getJsonCode(FieldInformation fieldInformation) {
		String jsonObjectCode = JSON_OBJECT_CODE_TEMPLATE;
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{jsonObject\\}", VARIABLE_NAME_JSON_OBJECT);
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		jsonObjectCode = jsonObjectCode.replaceAll("\\$\\{fieldName\\}", fieldInformation.getField().getName());

		return jsonObjectCode;
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
		return parseCode;
	}

	public NodeType getNodeType(FieldInformation fieldInformation) {
		if (fieldInformation.getAnnoField() != null) {
			return fieldInformation.getAnnoField().type();
		}
		return NodeType.NODE;
	}

	/**
	 * 这是为了针对数组或集合了而准备的，通过集合类适配器，将该方法适配成正常的WriteCode方法<br>
	 * 这个方法子类可以不用实现，如果不事先该方法，代表此类型不支持数组操作
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public String getWriteCodeForArray(FieldInformation fieldInformation) {
		throw new UnsupportedOperationException(this.getClass() + " unsupported WriteCodeForArray");
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
		return parseCode;
	}

	/**
	 * 这是为了针对数组或集合了而准备的，通过集合类适配器，将该方法适配成正常的JsonCode方法<br>
	 * 这个方法子类可以不用实现，如果不事先该方法，代表此类型不支持数组操作
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public String getJsonCodeForArray(FieldInformation fieldInformation) {
		return JSON_OBJECT_CODE_ARRAY_TEMPLATE;
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

	public String getWriteNodeXmlCodeTemplate() {
		return WRITE_XML_CODE_NODE_TEMPLATE;
	}

	public String getWriteAttrXmlCodeTemplate() {
		return WRITE_XML_CODE_ATTR_TEMPLATE;
	}

	/**
	 * 这是为了针对数组或集合了而准备的，通过集合类适配器，将该方法适配成正常的SizeCode方法<br>
	 * 这个方法子类可以不用实现，如果不事先该方法，代表此类型不支持数组操作
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public String getSizeCodeForArray(FieldInformation fieldInformation) {
		throw new UnsupportedOperationException(this.getClass() + " unsupported SizeCodeForArray");
	}

	/**
	 * 这是为了针对数组或集合了而准备的，通过集合类适配器，将该方法适配成正常的ParseCode方法<br>
	 * 这个方法子类可以不用实现，如果不事先该方法，代表此类型不支持数组操作
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public String getParseCodeForArray(FieldInformation fieldInformation) {
		throw new UnsupportedOperationException(this.getClass() + " unsupported ParseCodeForArray");
	}

	/**
	 * 这是一个获取当前类型对象的Tag值，这个Tag值是Google protobuf协议中重要的一部分，用于反序列化时case时使用
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public int getTagType(FieldInformation fieldInformation) {
		return protoFieldType.tagType;
	}

	/**
	 * 通过此处返回序列化或反序列化时写入的流类型(用字符串标识，例如Int32或Fixed32)
	 * 
	 * @param fieldInformation
	 * @return
	 */
	protected String getStreamTypeString(FieldInformation fieldInformation) {
		ProtoType protoType = fieldInformation.getAnnoField() != null ? fieldInformation.getAnnoField().protoType()
				: ProtoType.AUTOMATIC;
		protoType = ProtoFieldType.processProtoType(protoFieldType, protoType);
		if (protoType == null || protoType == ProtoType.AUTOMATIC) {
			return protoFieldType.pbType;
		} else {
			return protoType.getProtoStreamString();
		}
	}

	/**
	 * 获得一个类的Getter方法名称
	 * 
	 * @param fieldInformation
	 * @return
	 */
	@Override
	public String getGetterName(FieldInformation fieldInformation) {

		StringBuilder sb = new StringBuilder();
		if (fieldInformation.getCurrentType() == boolean.class) {

			// 我们的JavaBean的get/set方法不是按照标准的命名方法来写，针对boolean不是用isBoolean()而是用getBoolean(),
			// 所以提供了isXXX和getXXX两种的自适应
			StringBuffer method1 = new StringBuffer();
			method1.append("get").append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
					.append(fieldInformation.getField().getName().substring(1));
			// method2是正常JavaBean规范应该书写的格式
			StringBuffer method2 = new StringBuffer();
			String fieldName = fieldInformation.getField().getName();
			if (fieldName.length() > 1 && (fieldName.substring(0, 2).startsWith("is"))) {

				int threeChar = fieldInformation.getField().getName().charAt(2);
				// 如果FieldName = isBoolean , 则 GetterName = isBoolean
				if (threeChar >= 65 && threeChar <= 90) {
					method2.append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toLowerCase())
							.append(fieldInformation.getField().getName().substring(1));
				} else {
					// 如果FieldName = isboolean , 则 GetterName = isIsboolean
					method2.append("is")
							.append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
							.append(fieldInformation.getField().getName().substring(1));
				}

			} else {
				// 否则 isBoolean
				method2.append("is")
						.append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
						.append(fieldInformation.getField().getName().substring(1));
			}
			java.lang.reflect.Method[] methods = fieldInformation.getOutterClass().getMethods();
			for (java.lang.reflect.Method method : methods) {
				if (method.getName().equals(method1.toString())) {
					return method1.toString();
				} else if (method.getName().equals(method2.toString())) {
					return method2.toString();
				}
			}
			throw new RuntimeException(String.format("Not Found Getter method in [Class: %s , FieldName : %s]",
					fieldInformation.getOutterClass().toString(), fieldName));
		} else {
			sb.append("get").append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
					.append(fieldInformation.getField().getName().substring(1));
		}
		return sb.toString();
	}

	/**
	 * 获得一个类的Setter方法名称
	 * 
	 * @param fieldInformation
	 * @return
	 */
	@Override
	public String getSetterName(FieldInformation fieldInformation) {

		StringBuilder sb = new StringBuilder();
		if (fieldInformation.getCurrentType() == boolean.class) {

			StringBuffer method1 = new StringBuffer();
			method1.append("set").append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
					.append(fieldInformation.getField().getName().substring(1));

			StringBuffer method2 = new StringBuffer();
			String fieldName = fieldInformation.getField().getName();
			if (fieldName.length() > 1 && (fieldName.substring(0, 2).startsWith("is"))) {

				int threeChar = fieldInformation.getField().getName().charAt(2);
				if (threeChar >= 65 && threeChar <= 90) {
					method2.append("set").append(fieldInformation.getField().getName().substring(2));
				} else {
					method2.append("set")
							.append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
							.append(fieldInformation.getField().getName().substring(1));
				}

			} else {
				method2.append("set")
						.append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
						.append(fieldInformation.getField().getName().substring(1));
			}
			java.lang.reflect.Method[] methods = fieldInformation.getOutterClass().getMethods();
			for (java.lang.reflect.Method method : methods) {
				if (method.getName().equals(method1.toString())) {
					return method1.toString();
				} else if (method.getName().equals(method2.toString())) {
					return method2.toString();
				}
			}
			throw new RuntimeException(String.format("Not Found Setter method in [Class: %s , FieldName : %s]",
					fieldInformation.getOutterClass().toString(), fieldName));

		} else {
			sb.append("set").append(String.valueOf(fieldInformation.getField().getName().charAt(0)).toUpperCase())
					.append(fieldInformation.getField().getName().substring(1));
		}
		return sb.toString();
	}

	protected String getXmlFieldName(FieldInformation fieldInformation) {
		String fieldName = null;
		if (fieldInformation.getAnnoField() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoField().name())) {
			fieldName = fieldInformation.getAnnoField().name();
		} else if (fieldInformation.getAnnoChilds() != null
				&& !StringUtils.isNullOrEmpty(fieldInformation.getAnnoChilds().child())) {
			fieldName = fieldInformation.getAnnoChilds().child();
		} else {
			fieldName = fieldInformation.getField().getName();
		}
		return fieldName;
	}

}
