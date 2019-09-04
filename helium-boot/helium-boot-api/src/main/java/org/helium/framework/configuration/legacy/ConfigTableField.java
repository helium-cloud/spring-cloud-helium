/*
 * FAE, Feinno App Engine
 *  
 * Create by lichunlei 2010-11-26
 * 
 * Copyright (c) 2010 北京新媒传信科技有限公司
 */
package org.helium.framework.configuration.legacy;

import java.lang.annotation.*;

/**
 * 用于标注一个配置表的字段名
 * 
 * @author lichunlei
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ConfigTableField
{
	String value();
	boolean trim() default true;
	boolean required() default true;
	boolean isKeyField() default false;
}
