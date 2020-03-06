package org.helium.framework.annotations;

import org.helium.framework.servlet.ServletMappings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Coral on 7/8/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface ServletMappingsClass {
	Class<? extends ServletMappings> value();
}
