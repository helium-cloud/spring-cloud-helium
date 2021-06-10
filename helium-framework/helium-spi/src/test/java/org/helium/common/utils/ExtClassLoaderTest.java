package org.helium.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author wuhao
 * @createTime 2021-06-04 17:31:00
 */
public class ExtClassLoaderTest {
    @Test
    public void testClassLoader(){
        ClassLoaderExt classLoaderExt = new ClassLoaderExt();
        ClassUtils.setExtClassLoader(classLoaderExt);
        assertEquals(classLoaderExt, ClassUtils.getClassLoader());
        assertEquals(classLoaderExt, ClassUtils.getExtClassLoader());
        ClassUtils.setExtClassLoader(null);
    }
    static class ClassLoaderExt extends ClassLoader{

    }

}
