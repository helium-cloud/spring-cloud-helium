package com.feinno.superpojo.generator.Field;

import com.feinno.superpojo.generator.ProtoFieldType;

/**
 * <b>描述: </b>这是一个用于protobuf格式序列化时字段类型的解释器的接口<br>
 * 当使用protobuf格式进行序列化或反序列化时，会先生成对应类的序列化辅助类代码，在生成辅助代码时，会逐个遍历待处理类的待序列化字段， 根据字段类型及
 * 注释{@link ProtoMember}信息 ，生成每个字段的序列化处理代码，再将全部字段的序列化处理代码组合起来，创建出序列化辅助类
 * ，该类就是提供每种字段类型应该如何进行序列化处理的代码的抽象类父类,它提供了在处理每一种字段类型时的基础方法与公共实现.<br>
 * 这个解释器主要针对JavaBean中某一个字段类型在生成ProtoBuilder的源码时使用，代表了这个类型字段在序列化和反序列化时应该生成的代码，
 * 它规定了几种方法，是实现类必须提供的， 因为这些方法是在生成ProtoBuilder时必须使用的
 * <p>
 * 例如{@link EnumFieldInterpreter}
 * 这是一个枚举类型的解释器，在自动生成源码的过程中发现类某一个字段为枚举类型时，会自动调用此解释器，
 * 用于在创建源码时解释此字段如何序列化(write方法)、如何反序列化(parse方法)以及如何计算序列化长度
 * <p>
 * 如果有新的想要序列化的类型加入，可以自己写实现一个此接口的类型解释器，并把类型写入{@link ProtoFieldType}枚举中，
 * {@link ProtoFieldType}枚举的valueOf会自动的将这个字段类型与相应的解释器关联上
 * <p>
 * <b>功能: </b>为protobuf提供每一种序列化字段类型的解释器的接口
 * <p>
 * <b>用法: </b>该类由序列化组件在遍历类中的字段时调用
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public interface FieldInterpreter {

	/**
	 * 获得序列化类的全局自定义代码，可以是符合JAVA编程规范的任意全局变量代码段或方法代码段
	 * 
	 * @param fieldInformation
	 * @return
	 */
	public String getGlobalCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getWriteCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在XML方式node序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getWriteNodeXmlCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在XML方式attribute序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getWriteAttrXmlCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段转换为Json的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getJsonCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在序列化时判断字段所占空间的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getSizeCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在反序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在XML方式node反序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseNodeXmlCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在XML方式attribute反序列化时的代码
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseAttrXmlCode(FieldInformation fieldInformation);

	/**
	 * 获得该字段在判空时使用的代码<br>
	 * 因为ProtoMember中可以设置{@link ProtoMember#required()}的值，当值为<code>true</code>
	 * 时，要求该字段内容不能为空，否则将抛出异常
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getRequiredCode(FieldInformation fieldInformation);

	/**
	 * 这是针对数组或集合类而准备的，不可以单独使用，需要配合集合类解释器或数组解释器可以将该方法适配成正常的WriteCode方法才能输出正确的结果
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getWriteCodeForArray(FieldInformation fieldInformation);

	/**
	 * 这是针对数组或集合类而准备的，不可以单独使用，需要配合集合类解释器或数组解释器可以将该方法适配成正常的JsonCode方法才能输出正确的结果
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getJsonCodeForArray(FieldInformation fieldInformation);

	/**
	 * 这是针对数组或集合类而准备的，不可以单独使用，需要配合集合类解释器或数组解释器可以将该方法适配成正常的SizeCode方法才能输出正确的结果
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getSizeCodeForArray(FieldInformation fieldInformation);

	/**
	 * 这是针对数组或集合类而准备的，不可以单独使用，需要配合集合类解释器或数组解释器可以将该方法适配成正常的ParseCode方法才能输出正确的结果
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseCodeForArray(FieldInformation fieldInformation);

	/**
	 * 获得该字段在XML方式node反序列化时的代码，用于集合类
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getParseNodeXmlCodeForArray(FieldInformation fieldInformation);

	/**
	 * 这是一个获取当前类型对象所的Tag值，用于反序列化时case xxx时使用
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public int getTagType(FieldInformation fieldInformation);

	/**
	 * 获得该字段的get方法名称,例如<code>getName</code>
	 * 
	 * @param field
	 * @return
	 */
	public String getGetterName(FieldInformation fieldInformation);

	/**
	 * 获得该字段的set方法名称,例如<code>setName</code>
	 * 
	 * @param fieldInformation
	 *            字段的详细描述对象
	 * @return
	 */
	public String getSetterName(FieldInformation fieldInformation);
}
