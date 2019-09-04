package com.feinno.superpojo.generator;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feinno.superpojo.BuilderFactory;
import com.feinno.superpojo.ClassTemplate;
import com.feinno.superpojo.Config;
import com.feinno.superpojo.annotation.Entity;
import com.feinno.superpojo.generator.Field.ArrayFieldInterpreter;
import com.feinno.superpojo.generator.Field.FieldInformation;
import com.feinno.superpojo.generator.Field.FieldInterpreter;
import com.feinno.superpojo.io.WireFormat;
import com.feinno.superpojo.util.ClassUtil;
import com.feinno.superpojo.util.FileUtil;
import com.feinno.superpojo.util.JavaEval;
import com.feinno.superpojo.util.JavaEvalException;
import com.feinno.superpojo.util.ProtoGenericsUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CodeGenerator<T> {

	/** 已经过自动生成过proto对象的class列表，如果某一个类路径存在于这个列表中，代表此proto的class已经被生成过了，无需再次生成 */
	public static final Map<ClassLoader, Set<Class<?>>> generatedClassCache = Collections
			.synchronizedMap(new HashMap<ClassLoader, Set<Class<?>>>());

	/**
	 * 将要生成序列化辅助类的类型
	 */
	private Class<T> clazz;
	/**
	 * 这是一个用于占位标识的Set，功能与generatedClassSet很类似，区别是它只代表当前代码生成器对象都在处理哪些类，防止在递归时重复处理
	 */
	private Set<Class<?>> codeSet = null;

	/** ProtoCode生成器自己维护的已生成源码的列表 */
	private List<String> sourceList = null;

	private static boolean isDebug = false;

	private static String sourcePath = null;

	private static final Logger logger = LoggerFactory.getLogger(CodeGenerator.class);

	private CodeGenerator(Class<T> clazz) {
		this.clazz = clazz;
	}

	public static <T extends Object> CodeGenerator<T> newInstance(Class<T> clazz) {
		return new CodeGenerator<T>(clazz);
	}

	/**
	 * 构建一个指定JavaBean文件类型的BuilderFactory类
	 *
	 * @return
	 */
	public BuilderFactory build() throws CodeGeneratorException, JavaEvalException {
		if (sourceList == null) {
			codeSet = new HashSet<Class<?>>();
			sourceList = new ArrayList<String>();
			processProtoCode(clazz);
		}

		if (sourceList.size() > 0) {
			String[] sourceArrays = new String[sourceList.size()];
			JavaEval.compile(clazz.getClassLoader(), sourceList.toArray(sourceArrays));
		}
		// 编译并且返回对应的ProtoBuilderFactory实例
		return JavaEval.newClassInstance(clazz.getClassLoader(), BuilderFactory.class,
				ClassUtil.getBuilderFactoryClassFullName(clazz));
	}

	/**
	 * 通过一个类获得他的序列化辅助类源码，这个方法是生成序列化辅助类代码的核心部分<br>
	 * 1. 首先寻找到将要序列化的类的全部字段，为了支持继承结构，这些字段包含了父类的字段<br>
	 * 2. 遍历这些字段<br>
	 * 3. 对每一个字段判断是否有序列化注解ProtoMember,如果有此注解，那么继续，否则放弃这个字段<br>
	 * 4. 如果满足第3跳，那么继续获得该字段对应的字段解释器FieldInterpreter，通过解释器翻译出该字段的序列化、反序列化等代码 <br>
	 * 5. 处理特殊字段类型，例如ProtoEntity、List、Array、Map (因为这些类型可能存在递归的序列化代码生成操作) <br>
	 * 6. 将前几步得到的结果存入JavaBean对象，并通过freemarker将其填充入代码模板中 <br>
	 * 注：第5步可能会产生递归操作，会最终递归到以上步骤的第1步
	 * 
	 * @param clazz
	 * @return
	 */
	private void processProtoCode(Class<?> clazz) throws CodeGeneratorException {
		logger.info("Began to generate the ProtoBuilder and ProtoBuilderFactory source code of the [{}].",
				clazz.getName());
		// 这个为占位符，在递归时使用，代表此类(clazz)已经有人处理了，递归时发现这个类，不需要管他了，一个类只需要生成一次源代码既可
		codeSet.add(clazz);
		try {
			// 寻找到该类及其父类的全部字段
			List<Field> fields = new ArrayList<Field>();
			for (Class<?> clazzTemp = clazz; !clazzTemp.equals(Object.class); clazzTemp = clazzTemp.getSuperclass()) {
				for (Field field : clazzTemp.getDeclaredFields()) {
					fields.add(field);
				}
			}
			Entity classEntity = clazz.getAnnotation(Entity.class);
			String xmlRoot = null;
			if (classEntity != null && classEntity.name() != null) {
				xmlRoot = classEntity.name();
			} else {
				xmlRoot = clazz.getSimpleName();
			}

			// 这里放置模板需要使用的字段，此字段通过freemarker最终写入到模板中
			ProtoTemplateParam templateParam = new ProtoTemplateParam();
			// 这个list用于存放这个序列化类对应的所有字段信息
			templateParam.setPackageName(ClassUtil.getBuilderPackage(clazz));
			templateParam.setClassName(ClassUtil.processClassName(clazz.getName()));
			templateParam.setBuilderClassName(clazz.getSimpleName() + Config.SUPER_POJO_BUILD_SUFFIX);
			templateParam.setBuilderFactoryClassName(clazz.getSimpleName() + Config.SUPER_POJO_BUILD_FACTORY_SUFFIX);
			templateParam.setXmlRoot(xmlRoot);
			// 开始逐个字段的解析工作
			for (Field field : fields) {
				// 通过注解找到相关字段的详细信息
				com.feinno.superpojo.annotation.Field annoField = field
						.getAnnotation(com.feinno.superpojo.annotation.Field.class);
				com.feinno.superpojo.annotation.Childs annoChild = field
						.getAnnotation(com.feinno.superpojo.annotation.Childs.class);
				if (annoField != null || annoChild != null) {
					// 找到此类型对应的ProtoType枚举类型以及对应的字段解释器
					ProtoFieldType protoFieldType = ProtoFieldType.valueOf(field.getType());
					FieldInterpreter fieldInterpreter = protoFieldType.getFieldInterpreter();
					// 拼装这个字段的信息到FieldInformation对象中，后面的各种字段解释器，都是用这里存储的字段信息进行生成序列化与反序列化代码
					FieldInformation fieldInformation = new FieldInformation(clazz, field);
					// 组装模板需要用到的字段内容，再将这个内容填充到模板上，最终形成一个完整的源文件
					// 这个内容就是具体的这个字段应该如何序列化、如何反序列化、如何计算长度的具体代码
					ProtoTemplateParam.FieldParam fieldParam = ProtoTemplateParam.newFieldParam();
					fieldParam.setValue(fieldInformation.getNumber());
					fieldParam.setFieldName(field.getName());
					fieldParam.setTag(WireFormat.makeTag(fieldInformation.getNumber(),
							fieldInterpreter.getTagType(fieldInformation)));
					fieldParam.setParseCode(fieldInterpreter.getParseCode(fieldInformation));
					fieldParam.setParseNodeXmlCode(fieldInterpreter.getParseNodeXmlCode(fieldInformation));
					fieldParam.setParseAttrXmlCode(fieldInterpreter.getParseAttrXmlCode(fieldInformation));
					fieldParam.setWriteCode(fieldInterpreter.getWriteCode(fieldInformation));
					fieldParam.setWriteAttrXmlCode(fieldInterpreter.getWriteAttrXmlCode(fieldInformation));
					fieldParam.setWriteNodeXmlCode(fieldInterpreter.getWriteNodeXmlCode(fieldInformation));
					fieldParam.setJsonCode(fieldInterpreter.getJsonCode(fieldInformation));
					fieldParam.setSizeCode(fieldInterpreter.getSizeCode(fieldInformation));
					// 如果此字段为必输项，则对外输出判空代码
					if (annoField != null && annoField.isRequired()) {
						fieldParam.setRequiredCode(fieldInterpreter.getRequiredCode(fieldInformation));
					}
					// 调用这个方法,处理了几种特殊的protoFieldType类型,因为这些类型有可能有嵌套的应该递归创建序列化辅助类的类型
					processSpecialField(templateParam, fieldParam, fieldInformation, protoFieldType);
					templateParam.addGlobalCode(fieldInterpreter.getGlobalCode(fieldInformation));
					templateParam.addFieldParam(fieldParam);
				}
			}
			// 开始通过模板生成对应的源码，并且将源码放入源码列表中
			sourceList.add(generatedCode(ClassTemplate.PROTO_BUILDER_TEMPLATE, templateParam));
			sourceList.add(generatedCode(ClassTemplate.PROTO_BUILDER_FACTORY_TEMPLATE, templateParam));
			// 将已经生成源码的类的名字放入已经生成过的列表中,下次遇到他就不需要再次生成了
			putGeneratedClassCache(clazz);
		} catch (Exception e) {
			throw new CodeGeneratorException(String.format("Generate %s error.", clazz.getName()), e);
		}
	}

	/**
	 * 添加自动生成的class到cache中
	 * 
	 * @param value
	 */
	private void putGeneratedClassCache(Class<?> value) {
		ClassLoader classLoader = clazz.getClassLoader();
		Set<Class<?>> classSet = generatedClassCache.get(classLoader);
		if (classSet == null) {
			classSet = new HashSet<Class<?>>();
			generatedClassCache.put(classLoader, classSet);
		}
		classSet.add(value);
	}

	/**
	 * 判断一个Class是否在Cache中
	 * 
	 * @param value
	 * @return
	 */
	public boolean containsGeneratedClass(Class<?> value) {
		ClassLoader classLoader = clazz.getClassLoader();
		Set<Class<?>> classSet = generatedClassCache.get(classLoader);
		if (classSet == null) {
			return false;
		} else {
			return classSet.contains(value);
		}
	}

	/**
	 * 处理了几种特殊的ProtoFieldType类型,因为这些类型有可能有嵌套的应该创建序列化辅助类的类型，我们需要找出这样的类型，
	 * 对他创建序列化辅助代码(ProtoBuilder以及ProtoBuilderFactory)
	 * 
	 * @param templateParam
	 * @param fieldParam
	 * @param fieldInformation
	 * @param protoFieldType
	 * @throws CodeGeneratorException
	 */
	private void processSpecialField(ProtoTemplateParam templateParam, ProtoTemplateParam.FieldParam fieldParam,
			FieldInformation fieldInformation, ProtoFieldType protoFieldType) throws CodeGeneratorException {
		switch (protoFieldType) {
		case MESSAGE:
			// 判断是否需要递归生成动态解析代码(Message本身就是需要递归创建对应动态代码的类型,下面的判断只是判断这个具体的MESSAGE类型是否已经创建过动态代码)
			if (!containsGeneratedClass(fieldInformation.getField().getType())
					&& !codeSet.contains(fieldInformation.getField().getType())) {
				// 如果需要创建动态代码的类不存在已生成列表中，并且本次也没人处理他，则需要处理他了
				processProtoCode(fieldInformation.getField().getType());
			}
			break;
		case LIST:
		case SET:
			// 取List中泛型所代表的具体类型，查看此类型是否需要递归生成源码的类型
			Class<?> classType1 = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
			if (ProtoFieldType.valueOf(classType1) == ProtoFieldType.MESSAGE) {// 如果集合中的泛型又是一个消息类型，则需要创建这个类型的序列化辅助类
				if (!containsGeneratedClass(classType1) && !codeSet.contains(classType1)) {
					// 如果需要创建动态代码的类不存在已生成列表中，并且本次也没人处理他，则需要处理他了
					processProtoCode(classType1);
				}
			}
			break;
		case ARRAY:
			// 这次不是List了，他有可能是数组，但是方法一样
			Class<?> classType2 = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
			if (ProtoFieldType.valueOf(classType2) == ProtoFieldType.MESSAGE) {// 如果数组中的类型又是一个消息类型，则需要创建这个类型的序列化辅助类
				if (!containsGeneratedClass(classType2) && !codeSet.contains(classType2)) {
					// 如果需要创建动态代码的类不存在已生成列表中，并且本次也没人处理他，则需要处理他了
					processProtoCode(classType2);
				}
			}
			// 数组类型需要在反序列化时将缓存在MAP<Integer,List<Object>>的List转换成数组再赋值到相应的字段上，这么做是为了提升数组的反序列化速度
			fieldParam.setParseFooter(((ArrayFieldInterpreter) protoFieldType.getFieldInterpreter())
					.getConverArrayCode(fieldInformation));
			break;
		case MAP:
			// 这次有可能是MAP了，但是方法一样，不过他要分别对Key和Value进行泛型类型检查以及判断这两种类型是否递归进行动态编译
			Class<?> classKey = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 0);
			if (ProtoFieldType.valueOf(classKey) == ProtoFieldType.MESSAGE) {
				if (!containsGeneratedClass(classKey) && !codeSet.contains(classKey)) {
					// 如果需要创建动态代码的类不存在已生成列表中，并且本次也没人处理他，则需要处理他了
					processProtoCode(classKey);
				}
			}
			Class<?> classValue = ProtoGenericsUtils.getGenericsClass(fieldInformation.getField(), 1);
			if (ProtoFieldType.valueOf(classValue) == ProtoFieldType.MESSAGE) {
				if (!containsGeneratedClass(classValue) && !codeSet.contains(classValue)) {
					// 如果需要创建动态代码的类不存在已生成列表中，并且本次也没人处理他，则需要处理他了
					processProtoCode(classValue);
				}
			}
			templateParam.setParseFooterExists(true);
			break;
		default:
			break;
		}
	}

	/**
	 * 通过指定的模板，填充指定MAP数据，返回填充后的内容
	 * 
	 * @param templateName
	 * @param templateParam
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	private String generatedCode(String templateName, ProtoTemplateParam templateParam) throws IOException,
			TemplateException {
		Configuration cfg = new Configuration();
		cfg.setTemplateLoader(new StringTemplateLoader(templateName));
		cfg.setDefaultEncoding("UTF-8");
		Template template = cfg.getTemplate("");
		StringWriter writer = new StringWriter();
		template.process(templateParam, writer);
		String source = writer.toString();

		// 开启测试时将生成的内容写成文件保存至某一位置
		if (isDebug) {
			if (sourcePath == null) {
				throw new RuntimeException("isDebug is true,but sourcePath is null.");
			}
			if (templateName.equals(ClassTemplate.PROTO_BUILDER_TEMPLATE)) {
				FileUtil.write(source, sourcePath + File.separator + templateParam.getBuilderClassName() + ".java");
			} else if (templateName.equals(ClassTemplate.PROTO_BUILDER_FACTORY_TEMPLATE)) {
				FileUtil.write(source, sourcePath + File.separator + templateParam.getBuilderFactoryClassName()
						+ ".java");
			} else if (templateName.equals(ClassTemplate.PROTO_NATIVE_ENTITY_CODE_TEMPLATE)) {
				FileUtil.write(source, sourcePath + File.separator + templateParam.getBuilderClassName()
						+ "Native.java");
			}

		}
		return source;
	}

	public static void setDebug(boolean isEnableDebug) {
		isDebug = isEnableDebug;
	}

	public static void setSourcePath(String sourceSavePath) {
		sourcePath = sourceSavePath;
	}

}
