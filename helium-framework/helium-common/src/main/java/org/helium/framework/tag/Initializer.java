package org.helium.framework.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在Bean, TaskImplementation, Servlet的方法上, 用于标记初始化方法, 初始化方法将由容器在完成Setter注入后调用
 * 方法必须为void func()类型
 *
 * Created by Coral on 5/6/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TagImplementationClass(InitializerTag.class)
public @interface Initializer {
}
