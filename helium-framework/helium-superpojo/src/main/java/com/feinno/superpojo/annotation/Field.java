package com.feinno.superpojo.annotation;

import java.lang.annotation.*;

/**
 * 此注解用于描述字段得序列化细节
 * 
 * @author Lv.Mingwei
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Field {

	/**
	 * 字段索引，为必选字段<br>
	 * 1. 在PB序列化时用于索引当前字段位置<br>
	 * 2. 在XML序列化时用于排列当前字段位置<br>
	 * 3. 在JSON序列化时用于排列当前字段位置<br>
	 * PS:该值在同一个args中不可重复
	 */
	int id();

	/**
	 * 字段名称，非必选字段，默认为或为空则使用当前字段名 <br>
	 * 1. 在PB序列化时无用 <br>
	 * 2. 在XML序列化时用于标识字段名称，默认为当前字段名<br>
	 * 3. 在JSON序列化时用于标识字段名称，默认为当前字段名
	 */
	String name() default "";

	/**
	 * 节点类型，非必选字段，仅在XML序列化时有效，默认为node(既为当前节点的子集)<br>
	 * 1. 在PB序列化时无用<br>
	 * 2. 在XML序列化时用于选择将当前节点放置在Attribute上还是字节点上<br>
	 * 3. 在JSON序列化时无用<br>
	 * PS:选择Attrbute类型时需要谨慎，该字段如果挂着附属内容，会得到警告<br>
	 */
	NodeType type() default NodeType.NODE;

	/**
	 * 格式化类型，非必选字段，仅在XML序列化时有效，默认日期为yyyy-MM-dd HH:mm:ss.SSS<br>
	 * 1. 在PB序列化时无用<br>
	 * 2. 在XML序列化时用于格式化float、double、Date等类型<br>
	 * 3. 在JSON序列化时同XML <br>
	 * PS:在不能format的字段做此标注，运行时将收到一条警告
	 */
	String format() default "";

	/**
	 * 是否使用<![CDATA[]]>对XML内容进行标注，非必选字段 <br>
	 * 1. 在PB序列化时无用<br>
	 * 2. 在XML序列化时用于使用<![CDATA[]]>对文字进行标注<br>
	 * 3. 在JSON序列化时无用 <br>
	 * PS:仅限String类型使用，在不能<![CDATA[]]>的字段做此标注，运行时将收到一条警告
	 */
	boolean isCDATA() default false;

	/**
	 * 是否为必须传输，非必选字段，默认为false<br>
	 * 序列化时，默认当值为初始值(int、long等为0，Object为null)不进行传输<br>
	 * 当该字段设置为true时，即使是初始值，也会进行传输
	 * 
	 * @return
	 */
	boolean isRequired() default false;

	/**
	 * 时区设置，非必选字段<br>
	 * 该字段用于标识UTC时间等有时区区别的消息
	 */
	TimeZone timeZone() default TimeZone.DEFAULT;

	/**
	 * 字段序列化类型，非必选字段，若对Protobuf协议不了解，建议使用默认<br>
	 * 1. 在PB序列化时，用于指定某一字段具体编码方式，例如可以具体指定int使用Int32、UInt32、SInt32、
	 * Fixed32或SFixed32等方式编码<br>
	 * 2. 在XML序列化时无用 <br>
	 * 3. 在JSON序列化时无用 <br>
	 */
	ProtoType protoType() default ProtoType.AUTOMATIC;

}
