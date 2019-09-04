package com.feinno.superpojo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于描述集合类型的序列化格式
 * 
 * @author lvmingwei
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Childs {
	/**
	 * 字段索引，为必选字段<br>
	 * 1. 在PB序列化时用于索引当前字段位置<br>
	 * 2. 在XML序列化时用于排列当前字段位置<br>
	 * 3. 在JSON序列化时用于排列当前字段位置<br>
	 * PS:该值在同一个args中不可重复<br>
	 */
	int id();

	/**
	 * 父元素名，非必选字段，默认无父原素<br>
	 * 1. 在PB序列化时无用 <br>
	 * 2. 在XML序列化时用于标识父原素，如果不为空，则创建一个此名称的父元素将其包裹进来<br>
	 * 3. 在JSON序列化时无用<br>
	 */
	String parent() default "";

	/**
	 * 节点名称，非必选字段，默认为当前字段名 <br>
	 * 1. 在PB序列化时无用 <br>
	 * 2. 在XML序列化时用于标识集合类型中每一个子元素得名称,默认为当前字段名称<br>
	 * 3. 在JSON序列化时无用<br>
	 */
	String child() default "";

	/**
	 * 在节点为Map<String,Object>时，是否使用key作为节点名称，默认为不启用 <br>
	 * 1. 在PB序列化时无用 <br>
	 * 2. 在XML序列化时,在节点为Map<String,Object>时，是否使用key作为节点名称<br>
	 * 3. 在JSON序列化时无用 <br>
	 */
	boolean useKeyName() default false;

}
