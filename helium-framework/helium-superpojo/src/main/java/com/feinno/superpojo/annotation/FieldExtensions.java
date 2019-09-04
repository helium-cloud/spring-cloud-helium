package com.feinno.superpojo.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 此注解用于扩展序列化描述注解{@link com.feinno.superpojo.annotation.Field}<br>
 * 为Field提供了一些便捷工具，例如快速创建一个XML节点
 * 
 * @author Lv.Mingwei
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FieldExtensions {

	/**
	 * 这是一个工具注解，通过该注解，可以快速的创建一个Xml节点做为当前节点的父节点<br>
	 */
	String newParentNode() default "";

	/**
	 * 在节点为Map<String,Object>时，是否使用key作为节点名称，默认为不启用 <br>
	 * 1. 在PB序列化时无用 <br>
	 * 2. 在XML序列化时,在节点为Map<String,Object>时，是否使用key作为节点名称<br>
	 * 3. 在JSON序列化时无用 <br>
	 */
	boolean useKeyName() default false;
}
