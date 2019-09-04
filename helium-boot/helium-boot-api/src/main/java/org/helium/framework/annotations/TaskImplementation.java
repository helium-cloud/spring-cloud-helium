package org.helium.framework.annotations;



import org.helium.framework.task.TaskStorageType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注一个Task实现
 * TaskImplementation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskImplementation {
	String id() default "";
	String event();
	String storage() default TaskStorageType.MEMORY_TYPE;
}
