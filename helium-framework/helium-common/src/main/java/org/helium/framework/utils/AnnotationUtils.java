package org.helium.framework.utils;

import java.lang.annotation.Annotation;

/**
 * Created by Coral on 12/1/15.
 */
public class AnnotationUtils {
	public static <E extends Annotation> E getAnnotation(Class<?> clazz, Class<E> annoClazz) {
		Annotation anno = clazz.getAnnotation(annoClazz);
		if (anno == null) {
			return null;
		} else {
			return (E)anno;
		}
	}
}
