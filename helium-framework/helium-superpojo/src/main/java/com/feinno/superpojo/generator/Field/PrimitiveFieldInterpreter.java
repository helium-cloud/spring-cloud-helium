package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.NodeType;
import com.feinno.superpojo.annotation.ProtoType;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.io.WireFormat;
import com.feinno.superpojo.util.ProtoGenericsUtils;

/**
 * <b>描述:
 * </b>用于序列化组件，原始数据类型的字段解释器的抽象类，因为原始字段类型的序列化方式大同小异，因此创建了这个抽象了，封装了原始数据类型的公共实现
 * <p>
 * <b>功能: </b>为原始数据类型提供protobuf格式的序列化与反序列化方法
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到原始数据类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public abstract class PrimitiveFieldInterpreter extends AbstractFieldInterpreter {

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>output.writeInt32(1, data.getId());</code><br>
	 * 的输出
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${data}.${getterName}() != 0 || ${data}.hasValue(${number})) ${output}.write${streamType}(${number}, ${data}.${getterName}());";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>jsonObject.addProperty("age",1);</code><br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_TEMPLATE = "if(com.feinno.superpojo.generator.ObjectUtils.isNull(${data}.${getterName}()))jsonObject.addProperty(\"${fieldName}\", ${data}.${getterName}());";

	/** 必须输入的值不管是否为0,都是要写入 */
	private static final String WRITE_CODE_REQ_TEMPLATE = "${output}.write${streamType}(${number}, ${data}.${getterName}());";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>size += com.feinno.serialization.protobuf.CodedOutputStream.computeInt32Size(1,data.getId());</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_TEMPLATE = "if(${data}.${getterName}() != 0 || ${data}.hasValue(${number})) size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},${data}.${getterName}());";
	/** 必须输入的值不管是否为0,都是要写入 */
	private static final String SIZE_CODE_REQ_TEMPLATE = "size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},${data}.${getterName}());";

	/**
	 * 用于数组或集合类时序列化代码的格式，期待一个类似<br>
	 * <code>output.writeInt32(1, value);</code> <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_ARRAY_TEMPLATE = "${output}.write${streamType}(${number}, value);";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value.toString());jsonArray.add(jsonPrimitive);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_ARRAY_TEMPLATE = "com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value);jsonArray.add(jsonPrimitive);";

	/**
	 * 用于数组或集合类时取序列化长度的代码格式，期待一个类似<br>
	 * <code>size += com.feinno.serialization.protobuf.CodedOutputStream.computeInt32Size(1,value);</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},value);";


	/**
	 * 表示反序列化XML代码的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){input.nextEvent();data.setId(input.readInt());}</code>
	 * <br>
	 * 的输出
	 */
	private static final String PARSE_NODE_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) {${input}.nextEvent(); ${wrap_java_type} fieldValue = ${input}.read${xmlType}(); if(fieldValue != null) ${data}.${setterName}(fieldValue);}";

	/**
	 * 表示反序列化代码XML的格式，期待一个类似于<br>
	 * <code>else if(name.equals("id")){data.setId(input.readInt());}</code> <br>
	 * 的输出
	 */
	private static final String PARSE_ATTR_XML_CODE_TEMPLATE = "else if(name.equals(\"${fieldName}\")) { ${wrap_java_type} fieldValue = ${input}.read${xmlType}(); if(fieldValue != null) ${data}.${setterName}(fieldValue);}";

	
	/**
	 * 用于数组或集合类时反序列化代码的格式，期待一个类似于<br>
	 * <code>fieldValue = input.readString();</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = ${input}.read${streamType}();";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>data.setId(input.readInt32());</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(${input}.read${streamType}());";

	/**
	 * 构造方法必须要求持有字段枚举类型的引用
	 * 
	 * @param protoFieldType
	 */
	public PrimitiveFieldInterpreter(ProtoFieldType protoFieldType) {
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
		String writeCode;
		if (fieldInformation.getAnnoField().isRequired()) {
			writeCode = getWriteCodeReqTemplate();
		} else {
			writeCode = getWriteCodeTemplate();
		}

		writeCode = writeCode.replaceAll("\\$\\{output\\}", VARIABLE_NAME_OUTPUTSTREAM);
		writeCode = writeCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
		writeCode = writeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
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
		String sizeCode;
		if (fieldInformation.getAnnoField().isRequired()) {
			sizeCode = getSizeCodeReqTemplate();
		} else {
			sizeCode = getSizeCodeTemplate();
		}
		sizeCode = sizeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
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
		parseCode = parseCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
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
		writeCode = writeCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
		writeCode = writeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		writeCode = writeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		writeCode = writeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
		writeCode = writeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
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
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
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
		parseCode = parseCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
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
		String parseCode = getParseNodeXmlCodeForTemplate();
		parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
		parseCode = parseCode.replaceAll("\\$\\{xmlType\\}", protoFieldType.xmlType);
		parseCode = parseCode.replaceAll("\\$\\{fieldName\\}", getXmlFieldName(fieldInformation));
		parseCode = parseCode.replaceAll("\\$\\{wrap_java_type\\}",
				ProtoGenericsUtils.getWrapsType(ProtoGenericsUtils.getClass(fieldInformation.getField())).getName());
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
		parseCode = parseCode.replaceAll("\\$\\{wrap_java_type\\}",
				ProtoGenericsUtils.getWrapsType(ProtoGenericsUtils.getClass(fieldInformation.getField())).getName());
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

	/**
	 * 这是一个获取当前类型对象所的Tag值，用于反序列化时case时使用
	 * 
	 * @param field
	 * @return
	 */
	@Override
	public int getTagType(FieldInformation fieldInformation) {
		ProtoType protoType = ProtoType.AUTOMATIC;
		if (fieldInformation.getAnnoField() != null) {
			protoType = fieldInformation.getAnnoField().protoType();
		}
		protoType = ProtoFieldType.processProtoType(protoFieldType, protoType);
		// 如果是int或者long之后，且未特别指定类型，那么还是使用默认类型
		if (protoType == null || protoType == ProtoType.AUTOMATIC) {
			// 如果没有指定或要求自动选择，那么就使用默认的类型
			return protoFieldType.tagType;
		} else {
			switch (protoType) {
			case FIXED32:
				return WireFormat.WIRETYPE_FIXED32;
			case FIXED64:
				return WireFormat.WIRETYPE_FIXED64;
			case SFIXED32:
				return WireFormat.WIRETYPE_FIXED32;
			case SFIXED64:
				return WireFormat.WIRETYPE_FIXED64;
			default:
				return protoFieldType.tagType;
			}
		}
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
	 * 获得序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getWriteCodeReqTemplate() {
		return WRITE_CODE_REQ_TEMPLATE;
	}

	/**
	 * 获得序列化长度计算代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getSizeCodeReqTemplate() {
		return SIZE_CODE_REQ_TEMPLATE;
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
	 * 为数组或集合类使用的序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getWriteCodeForArrayTemplate() {
		return WRITE_CODE_ARRAY_TEMPLATE;
	}

	/**
	 * 为数组或集合类使用的序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
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
	 * 为数组或集合类使用的反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseCodeForArrayTemplate() {
		return PARSE_CODE_ARRAY_TEMPLATE;
	}

	/**
	 * 获得反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseCodeTemplate() {
		return PARSE_CODE_TEMPLATE;
	}

	/** 下面的代码是获取各种类型解释器的方法，因为这些解释器是无状态的，所以可以使用单例模式 */
	/** 顾名思义，一个int类型的解释器，因为这个类是负责原始数据类型，所以不包括int类的包装类型 */
	private static FieldInterpreter intFieldInterpreter = null;

	/** 顾名思义，一个long类型的解释器，因为这个类是负责原始数据类型，所以不包括long类的包装类型 */
	private static FieldInterpreter longFieldInterpreter = null;

	/** 顾名思义，一个float类型的解释器，因为这个类是负责原始数据类型，所以不包括float类的包装类型 */
	private static FieldInterpreter floatFieldInterpreter = null;

	/** 顾名思义，一个double类型的解释器，因为这个类是负责原始数据类型，所以不包括double类的包装类型 */
	private static FieldInterpreter doubleFieldInterpreter = null;

	/** 顾名思义，一个boolean类型的解释器，因为这个类是负责原始数据类型，所以不包括boolean类的包装类型 */
	private static FieldInterpreter booleanFieldInterpreter = null;

	/** 顾名思义，一个byte类型的解释器，因为这个类是负责原始数据类型，所以不包括byte类的包装类型 */
	private static FieldInterpreter byteFieldInterpreter = null;

	/** 顾名思义，一个char类型的解释器，因为这个类是负责原始数据类型，所以不包括char类的包装类型 */
	private static FieldInterpreter charFieldInterpreter = null;

	/** 顾名思义，一个short类型的解释器，因为这个类是负责原始数据类型，所以不包括short类的包装类型 */
	private static FieldInterpreter shortFieldInterpreter = null;

	/**
	 * 获得一个int类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getIntFieldInterpreter() {
		if (intFieldInterpreter == null) {
			intFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.INT) {
			};
		}
		return intFieldInterpreter;
	}

	/**
	 * 获得一个long类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getLongFieldInterpreter() {
		if (longFieldInterpreter == null) {
			longFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.LONG) {
			};
		}
		return longFieldInterpreter;
	}

	/**
	 * 获得一个float类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getFloatFieldInterpreter() {
		if (floatFieldInterpreter == null) {
			floatFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.FLOAT) {
			};
		}
		return floatFieldInterpreter;
	}

	/**
	 * 获得一个double类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getDoubleFieldInterpreter() {
		if (doubleFieldInterpreter == null) {
			doubleFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.DOUBLE) {
			};
		}
		return doubleFieldInterpreter;
	}

	/**
	 * 获得一个boolean类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getBooleanFieldInterpreter() {
		if (booleanFieldInterpreter == null) {
			booleanFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.BOOLEAN) {

				/** boolean无路如何都是要写入的 */
				private static final String WRITE_CODE_TEMPLATE = "${output}.write${streamType}(${number}, ${data}.${getterName}());";

				/** boolean无路如何都是要写入的 */
				private static final String SIZE_CODE_TEMPLATE = "size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},${data}.${getterName}());";

				@Override
				protected String getWriteCodeTemplate() {
					return WRITE_CODE_TEMPLATE;
				}

				@Override
				protected String getSizeCodeTemplate() {
					return SIZE_CODE_TEMPLATE;
				}

			};
		}
		return booleanFieldInterpreter;
	}

	/**
	 * 获得一个byte类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getByteFieldInterpreter() {
		if (byteFieldInterpreter == null) {
			byteFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.BYTE) {

				/**
				 * byte的数组输出是在太特别了,有直接的input.readBytes方法把所有的数组都存储进去，
				 * 相比一个一个存储效率要高很多，因此这里重写模板，实现这样的功能
				 */
				private static final String WRITE_CODE_ARRAY_TEMPLATE = "${output}.writeBytes(${number}, ${package_io}.ByteString.copyFrom(${data}.${getterName}()));break;";

				private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ${package_io}.CodedOutputStream.computeBytesSize(${number},${package_io}.ByteString.copyFrom(${data}.${getterName}()));break;";

				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = null; ${data}.${setterName}(${input}.readBytes().toByteArray());";

				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}((byte)${input}.read${streamType}());";

				@Override
				public String getParseCodeTemplate() {
					return PARSE_CODE_TEMPLATE;
				}

				@Override
				protected String getWriteCodeForArrayTemplate() {
					return WRITE_CODE_ARRAY_TEMPLATE;
				}

				@Override
				protected String getSizeCodeForArrayTemplate() {
					return SIZE_CODE_ARRAY_TEMPLATE;
				}

				@Override
				protected String getParseCodeForArrayTemplate() {
					return PARSE_CODE_ARRAY_TEMPLATE;
				}

			};
		}
		return byteFieldInterpreter;
	}

	/**
	 * 获得一个char类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getCharFieldInterpreter() {
		if (charFieldInterpreter == null) {
			charFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.CHAR) {
				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}((char)${input}.read${streamType}());";

				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = java.lang.Character.valueOf((char)${input}.read${streamType}());";

				@Override
				public String getParseCodeTemplate() {
					return PARSE_CODE_TEMPLATE;
				}

				@Override
				public String getParseCodeForArrayTemplate() {
					return PARSE_CODE_ARRAY_TEMPLATE;
				}
			};
		}
		return charFieldInterpreter;
	}

	/**
	 * 获得一个short类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getShortFieldInterpreter() {
		if (shortFieldInterpreter == null) {
			shortFieldInterpreter = new PrimitiveFieldInterpreter(ProtoFieldType.SHORT) {
				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}((short)${input}.read${streamType}());";
				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = java.lang.Short.valueOf((short)${input}.read${streamType}());";

				@Override
				public String getParseCodeTemplate() {
					return PARSE_CODE_TEMPLATE;
				}

				@Override
				public String getParseCodeForArrayTemplate() {
					return PARSE_CODE_ARRAY_TEMPLATE;
				}
			};
		}
		return shortFieldInterpreter;
	}
}
