package org.helium.framework.annotations;

import org.helium.framework.configuration.FieldLoader;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用反射标记一个Setter注入器
 * Created by Coral on 7/4/15.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldSetter {
	String value();
	String group() default "cloud";
	boolean dynamic() default true;
	Class<? extends FieldLoader> loader() default FieldLoader.Null.class;
}
