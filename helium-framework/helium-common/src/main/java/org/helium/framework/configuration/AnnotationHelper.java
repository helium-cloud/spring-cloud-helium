package org.helium.framework.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AnnotationHelper
{
    public static <T> T getAnnotation(Class<T> tClass, Class clazz)
    {
        return getAnnotation(tClass, clazz.getAnnotations(), tClass.getName(), false);
    }

    public static <T> T getAnnotation(Class<T> tClass, Method method)
    {
        return getAnnotation(tClass, method.getAnnotations(), method.getName(), false);
    }

    //
    public static <T> T getAnnotation(Class<T> tClass, Field field)
    {
        return getAnnotation(tClass, field.getAnnotations(), field.getName(), false);
    }

    public static <T> T tryGetAnnotation(Class<T> tClass, Field field)
    {
        return getAnnotation(tClass, field.getAnnotations(), field.getName(), true);
    }

    public static <T> T tryGetAnnotation(Class<T> tClass)
    {
        return getAnnotation(tClass, tClass.getAnnotations(), tClass.getName(), true);
    }

    private static <T> T getAnnotation(Class<T> tClass, Object[] attrs, String hostName, boolean isTry)
    {
        if (attrs.length == 0) {
            if (isTry)
                try {
                    return (T) tClass.newInstance();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    return null;
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    return null;
                }
            else
				/*
				 * throw new Exception(String.format(
				 * "Annotation %1$s not found in %2$s", tClass.getName(),
				 * hostName));
				 */
                return null;

        }
        if (attrs.length > 1)
            throw new RuntimeException(String.format("More than 1 Annotation %1$s found in %2$s", tClass.getName(),
                    hostName));

        if (tClass.isAssignableFrom(attrs[0].getClass()))
            return (T) tClass.cast(attrs[0]);
        else
            // throw new Exception("Unknown Type:" + tClass.getName());
            return null;
    }
}

