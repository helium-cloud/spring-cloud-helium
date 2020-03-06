package com.feinno.superpojo.annotation;

import java.lang.annotation.*;

/**
 * @author Lv.Mingwei
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Entity {

	/**
	 * 当前类型名，非必选字段,默认以类名称为XML根名称 <br>
	 * 1. 在PB序列化时该字段无用<br>
	 * 2. 在XML序列化时用于标注根字段名称，当前值为空时，使用类名称作为根名称<br>
	 * 3. 在JSON序列化时该字段无用<br>
	 * PS:当前对象为另一个对象的一个字节点时，该标识被
	 */
	String name() default "";

	/**
	 * 编码格式，非必选字段<br>
	 * 1. 在PB序列化时该字段无用<br>
	 * 2. 在XML序列化时用于标注编码格式<br>
	 * 3. 在JSON序列化时该字段无用<br>
	 * PS：此字段会影响到XML文本的编码格式
	 * 
	 * @return
	 */
	String encoding() default "UTF-8";

	/**
	 * xml的头信息，非必选字段，默认为空 <br>
	 * 1. 在PB序列化时该字段无用<br>
	 * 2. 在XML序列化时用于放置头信息，例如version="1.0" encoding="UTF-8"<br>
	 * 3. 在JSON序列化时该字段无用<br>
	 */
	String version() default "";

	/**
	 * xml的头信息，非必选字段，默认为空 <br>
	 * 1. 在PB序列化时该字段无用<br>
	 * 2. 在XML序列化时用于放置头信息，例如version="1.0" encoding="UTF-8"<br>
	 * 3. 在JSON序列化时该字段无用<br>
	 */
	boolean standalone() default false;

}
