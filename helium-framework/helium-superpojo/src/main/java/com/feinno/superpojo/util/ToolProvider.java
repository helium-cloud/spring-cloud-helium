package com.feinno.superpojo.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;

/**
 * Provides methods for locating tool providers, for example, providers of
 * compilers. This class complements the functionality of
 * {@link java.util.ServiceLoader}. <br>
 * 这个是从JDK1.6中拷贝过来的，因为这个类中Class.forName方法把类加载器传为空，导致拷贝进来的tools.jar无法加载，所以把这个类拷出来
 * ，修改了一下
 * 
 * @author Peter von der Ah&eacute;
 * @since 1.6
 */
public class ToolProvider {

	/**
	 * Gets the Java&trade; programming language compiler provided with this
	 * platform.
	 * 
	 * @return the compiler provided with this platform or {@code null} if no
	 *         compiler is provided
	 */
	public static JavaCompiler getSystemJavaCompiler() {
		if (Lazy.compilerClass == null)
			return null;
		try {
			return Lazy.compilerClass.newInstance();
		} catch (Throwable e) {
			return null;
		}
	}

	/**
	 * Returns the class loader for tools provided with this platform. This does
	 * not include user-installed tools. Use the
	 * {@linkplain java.util.ServiceLoader service provider mechanism} for
	 * locating user installed tools.
	 * 
	 * @return the class loader for tools provided with this platform or
	 *         {@code null} if no tools are provided
	 */
	public static ClassLoader getSystemToolClassLoader() {
		if (Lazy.compilerClass == null)
			return null;
		return Lazy.compilerClass.getClassLoader();
	}

	/**
	 * This class will not be initialized until one of the above methods are
	 * called. This ensures that searching for the compiler does not affect
	 * platform start up.
	 */
	static class Lazy {
		private static final String defaultJavaCompilerName = "com.sun.tools.javac.api.JavacTool";
		private static final String[] defaultToolsLocation = { "lib", "tools.jar" };
		static final Class<? extends JavaCompiler> compilerClass;
		static {
			Class<? extends JavaCompiler> c = null;
			try {
				c = findClass().asSubclass(JavaCompiler.class);
			} catch (Throwable t) {
				// ignored
			}
			compilerClass = c;
		}

		private static Class<?> findClass() throws MalformedURLException, ClassNotFoundException {
			try {
				// Modify by Lv.Mingwei 修改的位置是这里，增加了默认使用当前类的加载器
				return enableAsserts(Class.forName(defaultJavaCompilerName, false, ToolProvider.class.getClassLoader()));
			} catch (ClassNotFoundException e) {
				// ignored, try looking else where
			}
			File file = new File(System.getProperty("java.home"));
			if (file.getName().equalsIgnoreCase("jre"))
				file = file.getParentFile();
			for (String name : defaultToolsLocation)
				file = new File(file, name);
			URL[] urls = { file.toURI().toURL() };
			ClassLoader cl = URLClassLoader.newInstance(urls);
			cl.setPackageAssertionStatus("com.sun.tools.javac", true);
			return Class.forName(defaultJavaCompilerName, false, cl);
		}

		private static Class<?> enableAsserts(Class<?> cls) {
			try {
				ClassLoader loader = cls.getClassLoader();
				if (loader != null)
					loader.setPackageAssertionStatus("com.sun.tools.javac", true);
			} catch (SecurityException ex) {
				// ignored
			}
			return cls;
		}
	}
}
