package org.helium.framework.annotations;

import java.lang.annotation.*;

/**
 * Created by Coral on 7/29/15.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ModuleCollection.class)
public @interface Module {
	Class<? extends org.helium.framework.module.Module> value();
}
