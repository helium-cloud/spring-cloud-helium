package org.helium.framework.tag;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在Bean, TaskImplementation, Servlet的方法上, 用于标记终结方法, 在容器销毁一个Bean的时候调用，
 * 方法必须为void func()类型
 *
 * MetaTags
 *  @Initializer
 *  @Finalizer
 *  @RpcService
 *  @
 * Created by Coral on 5/6/15.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TagImplementationClass(FinalizerTag.class)
public @interface Finalizer {
}
