package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.ProtoType;
import com.feinno.superpojo.generator.ProtoFieldType;
import com.feinno.superpojo.io.WireFormat;

/**
 * <b>描述: </b>用于序列化组件，基本类型的包装类型的字段解释器的抽象类，因为基本类型的包装类型的序列化方式大同小异，因此创建了这个抽象了，
 * 封装了基本类型的包装类型的公共实现
 * <p>
 * <b>功能: </b>为基本类型的包装类型提供protobuf格式的序列化与反序列化方法
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时遇到基本类型的包装类型时调用,外部无需调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public abstract class WrapsFieldInterpreter extends AbstractFieldInterpreter {

	/**
	 * 表示序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.writeString(1, data.getEmail());</code>
	 * <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) ${output}.write${streamType}(${number}, ${data}.${getterName}());";

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_NODE_TEMPLATE = "if( ${data}.${getterName}() != null) { ${output}.writeStartElement(\"${fieldName}\");${output}.${writeType}(${data}.${getterName}());${output}.writeEndElement(\"${fieldName}\");}";

	/**
	 * 表示XML序列化代码的格式，期待一个类似<br>
	 * <code>if(data.getEmail() != null) output.write(data.getEmail());</code> <br>
	 * 的输出
	 */
	private static final String WRITE_XML_CODE_ATTR_TEMPLATE = "if( ${data}.${getterName}() != null) { ${output}.writeAttribute(\"${fieldName}\",${data}.${getterName}());}";

	/**
	 * 表示获取序列化长度的代码格式，期待一个类似<br>
	 * <code>if(data.getName() != null) size += com.feinno.serialization.protobuf.CodedOutputStream.computeStringSize(1,data.getName());</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_TEMPLATE = "if( ${data}.${getterName}() != null) size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},${data}.${getterName}());";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>data.setName(input.readString());</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(${input}.read${streamType}());";

	/**
	 * 用于数组或集合类时序列化代码的格式，期待一个类似<br>
	 * <code>output.writeString(1, value);</code> <br>
	 * 的输出
	 */
	private static final String WRITE_CODE_ARRAY_TEMPLATE = "${output}.write${streamType}(${number}, value);";

	/**
	 * 表示反序列化代码的格式，期待一个类似于<br>
	 * <code>com.google.gson.JsonPrimitive jsonPrimitive = new com.google.gson.JsonPrimitive(value.toString());jsonArray.add(jsonPrimitive);</code>
	 * <br>
	 * 的输出
	 */
	private static final String JSON_OBJECT_CODE_ARRAY_TEMPLATE = "com.google.gson.JsonElement element = null; if (value == null) { element = new com.google.gson.JsonNull(); }else { element = new com.google.gson.JsonPrimitive(value); } jsonArray.add(element);";

	/**
	 * 用于数组或集合类时取序列化长度的代码格式，期待一个类似<br>
	 * <code>size += com.feinno.serialization.protobuf.CodedOutputStream.computeStringSize(1,value);</code>
	 * <br>
	 * 的输出
	 */
	private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ${package_io}.CodedOutputStream.compute${streamType}Size(${number},value);";

	/**
	 * 用于数组或集合类时反序列化代码的格式，期待一个类似于<br>
	 * <code>fieldValue = input.readString();</code><br>
	 * 的输出
	 */
	private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = ${input}.read${streamType}();";

	/**
	 * 构造方法必须要求持有字段枚举类型的引用
	 * 
	 * @param protoFieldType
	 */
	public WrapsFieldInterpreter(ProtoFieldType protoFieldType) {
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
	 * getSizeCode
	 * (com.feinno.serialization.protobuf.generator.field.FieldInformation)
	 */
	@Override
	public String getSizeCode(FieldInformation fieldInformation) {
		String sizeCode = getSizeCodeTemplate();
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
		sizeCode = sizeCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
		sizeCode = sizeCode.replaceAll("\\$\\{streamType\\}", getStreamTypeString(fieldInformation));
		sizeCode = sizeCode.replaceAll("\\$\\{number\\}", String.valueOf(fieldInformation.getCurrentNumber()));
		sizeCode = sizeCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
		sizeCode = sizeCode.replaceAll("\\$\\{getterName\\}", getGetterName(fieldInformation));
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
		parseCode = parseCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
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
	 * #
	 * getTagType(com.feinno.serialization.protobuf.generator.field.FieldInformation
	 * )
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

	public String getWriteNodeXmlCodeTemplate() {
		return WRITE_XML_CODE_NODE_TEMPLATE;
	}

	public String getWriteAttrXmlCodeTemplate() {
		return WRITE_XML_CODE_ATTR_TEMPLATE;
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
	 * 为数组或集合类使用的反序列化代码的模板，如果子类中对于这个模板有修改，则重写此方法既可
	 * 
	 * @return
	 */
	protected String getParseCodeForArrayTemplate() {
		return PARSE_CODE_ARRAY_TEMPLATE;
	}

	/** 下面的代码是获取各种类型解释器的方法，因为这些解释器是无状态的，所以可以使用单例模式 */
	/** int包装类型的解释器 */
	private static FieldInterpreter integerFieldInterpreter = null;

	/** long包装类型的解释器 */
	private static FieldInterpreter longFieldInterpreter = null;

	/** float包装类型的解释器 */
	private static FieldInterpreter floatFieldInterpreter = null;

	/** double包装类型的解释器 */
	private static FieldInterpreter doubleFieldInterpreter = null;

	/** boolean包装类型的解释器 */
	private static FieldInterpreter booleanFieldInterpreter = null;

	/** byte包装类型的解释器 */
	private static FieldInterpreter byteFieldInterpreter = null;

	/** byte包装类型的解释器,这是一个经过优化过的解释器，主要用于byte数组时的存储，可以将包装类型的数组也一次性的写入六种 */
	private static FieldInterpreter byteOptimizationFieldInterpreter = null;

	/** character包装类型的解释器 */
	private static FieldInterpreter characterFieldInterpreter = null;

	/** short包装类型的解释器 */
	private static FieldInterpreter shortFieldInterpreter = null;

	/**
	 * 获得一个int包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getIntegerFieldInterpreter() {
		if (integerFieldInterpreter == null) {
			integerFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.INTEGER_OBJECT) {
			};
		}
		return integerFieldInterpreter;
	}

	/**
	 * 获得一个long包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getLongFieldInterpreter() {
		if (longFieldInterpreter == null) {
			longFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.LONG_OBJECT) {
			};
		}
		return longFieldInterpreter;
	}

	/**
	 * 获得一个float包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getFloatFieldInterpreter() {
		if (floatFieldInterpreter == null) {
			floatFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.FLOAT_OBJECT) {
			};
		}
		return floatFieldInterpreter;
	}

	/**
	 * 获得一个double包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getDoubleFieldInterpreter() {
		if (doubleFieldInterpreter == null) {
			doubleFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.DOUBLE_OBJECT) {
			};
		}
		return doubleFieldInterpreter;
	}

	/**
	 * 获得一个boolean包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getBooleanFieldInterpreter() {
		if (booleanFieldInterpreter == null) {
			booleanFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.BOOLEAN_OBJECT) {
			};
		}
		return booleanFieldInterpreter;
	}

	/**
	 * 获得一个byte包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getOptimizationByteFieldInterpreter() {
		if (byteOptimizationFieldInterpreter == null) {
			byteOptimizationFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.BYTE_OBJECT) {

				/** 因为这个输出和基类中的输出有一些不同，需要取到int后Byte.valueOf一下使之转换为Byte类型 */
				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(java.lang.Byte.valueOf((byte)${input}.read${streamType}()));";

				/**
				 * byte的数组输出是在太特别了,有直接的input.readBytes方法把所有的数组都存储进去，
				 * 相比一个一个存储效率要高很多，因此这里重写模板，实现这样的功能
				 */
				private static final String WRITE_CODE_ARRAY_TEMPLATE = "${output}.writeBytes(${number}, ${package_io}.ByteString.copyFrom(${package_util}.ArrayUtil.wrapsToPrimitive(${data}.${getterName}())));break;";

				private static final String SIZE_CODE_ARRAY_TEMPLATE = "size += ${package_io}.CodedOutputStream.computeBytesSize(${number},${package_io}.ByteString.copyFrom(${package_util}.ArrayUtil.wrapsToPrimitive(${data}.${getterName}())));break;";

				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = null; ${data}.${setterName}(${package_util}.ArrayUtil.primitiveToWraps(${input}.readBytes().toByteArray()));";

				/** 针对list中放置的Byte类型的处理方式 */
				private static final String PARSE_CODE_LIST_TEMPLATE = "fieldValue = null; byte bytes[] = ${input}.readBytes().toByteArray(); for (int i = 0; i < bytes.length; i++) { list.add(bytes[i]); }";

				/** 针对list中放置的Byte类型的处理方式,非常特别 */
				@Override
				public String getParseCodeForArray(FieldInformation fieldInformation) {
					String parseCode;
					if (fieldInformation.getField().getType().isArray()) {
						parseCode = getParseCodeForArrayTemplate();
					} else {
						parseCode = getParseCodeForListTemplate();
					}
					parseCode = parseCode.replaceAll("\\$\\{data\\}", VARIABLE_NAME_DATA);
					parseCode = parseCode.replaceAll("\\$\\{setterName\\}", getSetterName(fieldInformation));
					parseCode = parseCode.replaceAll("\\$\\{input\\}", VARIABLE_NAME_INPUTSTREAM);
					parseCode = parseCode.replaceAll("\\$\\{streamType\\}", protoFieldType.pbType);
					parseCode = parseCode.replaceAll("\\$\\{package_io\\}", Config.PACKAGE_IO);
					parseCode = parseCode.replaceAll("\\$\\{package_util\\}", Config.PACKAGE_UTIL);
					return parseCode;
				}

				@Override
				protected String getParseCodeTemplate() {
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

				protected String getParseCodeForListTemplate() {
					return PARSE_CODE_LIST_TEMPLATE;
				}
			};
		}
		return byteOptimizationFieldInterpreter;
	}

	/**
	 * 获得一个byte包装类型的解释器，这个解释器是一个简单的byte解释器，对序列化和反序列化不做任何优化，例如对List中的Byte进行序列化时，
	 * 直接逐个取出BYTE并逐个写入
	 * ，而没有取出所有的BYTE再调用writeBytes进行统一写入，反序列化时也同样，逐个取出，再逐个的装填到List中
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getByteFieldInterpreter() {
		if (byteFieldInterpreter == null) {
			byteFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.BYTE_OBJECT) {

				/**
				 * 
				 * 因为这个输出和基类中的输出有一些不同，需要取到int后<code>Byte.valueOf</code>
				 * 一下使之转换为Byte类型
				 */
				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(java.lang.Byte.valueOf((byte)${input}.read${streamType}()));";

				/**
				 * 因为这个输出和基类中的输出有一些不同，需要取到int后<code>Byte.valueOf</code>
				 * 一下使之转换为Byte类型(数组同理)
				 */
				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = java.lang.Byte.valueOf((byte)${input}.read${streamType}());";

				@Override
				protected String getParseCodeTemplate() {
					return PARSE_CODE_TEMPLATE;
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
	 * 获得一个char包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getCharacterFieldInterpreter() {
		if (characterFieldInterpreter == null) {
			characterFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.CHARACTER_OBJECT) {

				/**
				 * 
				 * 因为这个输出和基类中的输出有一些不同，需要取到int后<code>Character.valueOf</code>
				 * 一下使之转换为Character类型
				 */
				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(java.lang.Character.valueOf((char)${input}.read${streamType}()));";

				/**
				 * 因为这个输出和基类中的输出有一些不同，需要取到int后<code>Character.valueOf</code>
				 * 一下使之转换为Character类型(数组同理)
				 */
				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = java.lang.Character.valueOf((char)${input}.read${streamType}());";

				@Override
				protected String getParseCodeTemplate() {
					return PARSE_CODE_TEMPLATE;
				}

				@Override
				protected String getParseCodeForArrayTemplate() {
					return PARSE_CODE_ARRAY_TEMPLATE;
				}
			};
		}
		return characterFieldInterpreter;
	}

	/**
	 * 获得一个short包装类型的解释器
	 * 
	 * @return
	 */
	public synchronized static FieldInterpreter getShortFieldInterpreter() {
		if (shortFieldInterpreter == null) {
			shortFieldInterpreter = new WrapsFieldInterpreter(ProtoFieldType.SHORT_OBJECT) {

				/** 因为这个输出和基类中的输出有一些不同，需要取到int后Short.valueOf一下使之转换为Short类型 */
				private static final String PARSE_CODE_TEMPLATE = "${data}.${setterName}(java.lang.Short.valueOf((short)${input}.read${streamType}()));";

				/** 因为这个输出和基类中的输出有一些不同，需要取到int后Short.valueOf一下使之转换为Short类型(数组同理) */
				private static final String PARSE_CODE_ARRAY_TEMPLATE = "fieldValue = java.lang.Short.valueOf((short)${input}.read${streamType}());";

				@Override
				protected String getParseCodeTemplate() {
					return PARSE_CODE_TEMPLATE;
				}

				@Override
				protected String getParseCodeForArrayTemplate() {
					return PARSE_CODE_ARRAY_TEMPLATE;
				}
			};
		}
		return shortFieldInterpreter;
	}
}
