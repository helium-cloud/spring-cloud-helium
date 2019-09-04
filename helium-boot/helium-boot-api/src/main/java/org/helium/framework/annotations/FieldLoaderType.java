package org.helium.framework.annotations;

import org.helium.framework.configuration.FieldLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在Bean, TaskImplementation, Servlet的使用Setter注入进行初始化的字段类型上,
 * 用于特殊的注入操作
 * @see org.helium.framework.annotations.FieldSetter
 * Created by Coral on 5/5/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldLoaderType {
	Class<? extends FieldLoader> loaderType();
}
