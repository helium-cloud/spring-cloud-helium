package com.feinno.superpojo.generator;

import com.feinno.superpojo.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>描述: </b>以protobuf格式进行序列化时会生成对应的{@link ProtoBuilder}和
 * {@link ProtoBuilderFactory}类，在生成这个类时会先获得对应的模板，再对模板进行填充，而此类就是为了这些模板提供的填充参数
 * <p>
 * <b>功能: </b>{@link ProtoBuilder}和 {@link ProtoBuilderFactory}模板的填充参数
 * <p>
 * <b>用法: </b>由序列化组件直接调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ProtoTemplateParam {

	/** 待生成的类包路径 */
	private String packageName = null;

	/** supojo核心类的路径 */
	private String packageCoreRoot = Config.PACKAGE_ROOT;

	/** supojo核心IO类的路径 */
	private String packageCoreIo = Config.PACKAGE_IO;

	/** 待生成的类名称(全限定名称) */
	private String className = null;

	/** ProtoBuilder 的类名称 */
	private String builderClassName = null;

	/** ProtoBuilderFactory 的类名称 */
	private String builderFactoryClassName = null;

	private String xmlRoot = null;

	/** 类的全局代码，此列表存储的代码，将被装填到类的全局作用域中 */
	private List<String> globalCodeList = new ArrayList<String>();

	/** 用于存放这个序列化类对应的所有待序列化字段信息 */
	private List<FieldParam> fieldList = new ArrayList<FieldParam>();

	/** 字段中是否有使用了parseFooter的地方，会在添加字段时自动选择，无需手动设置 */
	private boolean parseFooterExists = false;

	/**
	 * 创建一个用于存储待序列化信息的类
	 * 
	 * @return
	 */
	public static FieldParam newFieldParam() {
		return new FieldParam();
	}

	public void addFieldParam(FieldParam fieldParam) {
		fieldList.add(fieldParam);
		if (fieldParam.getParseFooter() != null && fieldParam.getParseFooter().length() > 0) {
			parseFooterExists = true;
		}
	}

	public void addGlobalCode(String globalCode) {
		if (globalCode != null && globalCode.length() > 0) {
			globalCodeList.add(globalCode);
		}
	}

	public static class FieldParam {

		/** 当前字段的value值 */
		private int value;

		/** 当前字段在流中的标识符 */
		private int tag;

		/** 当前field的名字 */
		private String fieldName;

		/** 当前字段的序列化代码 */
		private String writeCode;

		/** 以XML_NODE方式序列化当前字段的代码 */
		private String writeNodeXmlCode;

		/** 以XML_ATTR方式序列化当前字段的代码 */
		private String writeAttrXmlCode;

		/** 当前字段toJson的Code */
		private String jsonCode;

		/** 当前字段的反序列化代码 */
		private String parseCode;

		/** 当前字段的反序列化XML代码 */
		private String parseAttrXmlCode;

		/** 当前字段的反序列化XML代码 */
		private String parseNodeXmlCode;

		/** 反序列化的尾部是否需要特别处理，如果有特别的处理，则将代码写于此处 */
		private String parseFooter;

		/** 当前字段的长度计算代码 */
		private String sizeCode;

		/** 判断当前字段是否为必输字段的代码 */
		private String requiredCode;

		public String getValue() {
			// 此方法改为输出String,因为int会被自动格式化为XX,XXX,例如56,138
			return String.valueOf(value);
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getTag() {
			// 此方法改为输出String,因为int会被自动格式化为XX,XXX,例如56,138
			return String.valueOf(tag);
		}

		public void setTag(int tag) {
			this.tag = tag;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getWriteCode() {
			return writeCode;
		}

		public void setWriteCode(String writeCode) {
			this.writeCode = writeCode;
		}

		public String getWriteNodeXmlCode() {
			return writeNodeXmlCode;
		}

		public void setWriteNodeXmlCode(String writeNodeXmlCode) {
			this.writeNodeXmlCode = writeNodeXmlCode;
		}

		public String getWriteAttrXmlCode() {
			return writeAttrXmlCode;
		}

		public void setWriteAttrXmlCode(String writeAttrXmlCode) {
			this.writeAttrXmlCode = writeAttrXmlCode;
		}

		public String getJsonCode() {
			return jsonCode;
		}

		public void setJsonCode(String jsonCode) {
			this.jsonCode = jsonCode;
		}

		public String getParseCode() {
			return parseCode;
		}

		public void setParseCode(String parseCode) {
			this.parseCode = parseCode;
		}

		public String getParseAttrXmlCode() {
			return parseAttrXmlCode;
		}

		public void setParseAttrXmlCode(String parseAttrXmlCode) {
			this.parseAttrXmlCode = parseAttrXmlCode;
		}

		public String getParseNodeXmlCode() {
			return parseNodeXmlCode;
		}

		public void setParseNodeXmlCode(String parseNodeXmlCode) {
			this.parseNodeXmlCode = parseNodeXmlCode;
		}

		public String getParseFooter() {
			return parseFooter;
		}

		public void setParseFooter(String parseFooter) {
			this.parseFooter = parseFooter;
		}

		public String getSizeCode() {
			return sizeCode;
		}

		public void setSizeCode(String sizeCode) {
			this.sizeCode = sizeCode;
		}

		public String getRequiredCode() {
			return requiredCode;
		}

		public void setRequiredCode(String requiredCode) {
			this.requiredCode = requiredCode;
		}

	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getPackageCoreRoot() {
		return packageCoreRoot;
	}

	public void setPackageCoreRoot(String packageCoreRoot) {
		this.packageCoreRoot = packageCoreRoot;
	}

	public String getPackageCoreIo() {
		return packageCoreIo;
	}

	public void setPackageCoreIo(String packageCoreIo) {
		this.packageCoreIo = packageCoreIo;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getBuilderClassName() {
		return builderClassName;
	}

	public void setBuilderClassName(String builderClassName) {
		this.builderClassName = builderClassName;
	}

	public String getBuilderFactoryClassName() {
		return builderFactoryClassName;
	}

	public void setBuilderFactoryClassName(String builderFactoryClassName) {
		this.builderFactoryClassName = builderFactoryClassName;
	}

	public String getXmlRoot() {
		return xmlRoot;
	}

	public void setXmlRoot(String xmlRoot) {
		this.xmlRoot = xmlRoot;
	}

	public List<String> getGlobalCodeList() {
		return globalCodeList;
	}

	public List<FieldParam> getFieldList() {
		return fieldList;
	}

	public final boolean isParseFooterExists() {
		return parseFooterExists;
	}

	public final void setParseFooterExists(boolean parseFooterExists) {
		this.parseFooterExists = parseFooterExists;
	}

}
