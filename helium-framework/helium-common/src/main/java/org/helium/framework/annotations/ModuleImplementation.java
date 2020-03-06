package org.helium.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个Module的实现
 * 通过反射创建的Module可以为空, 由系统随机分配
 * Created by Coral on 7/4/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModuleImplementation {
	String id() default "";
}
