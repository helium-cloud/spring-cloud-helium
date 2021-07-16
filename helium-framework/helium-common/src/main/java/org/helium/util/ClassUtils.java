package org.helium.util;


import org.helium.superpojo.SuperPojo;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * <b>描述:
 * </b>序列化组件中使用，这个类的主要功能是创建一个类型的实例，但是原始数据类型和包装类型因为特殊原因，无法使用newInstance来创建对象
 * ，该类的作用就是包装了对类型分析的部分，如果发现类型为基本类型或包装类型，则自动返回相应类型的实例
 * <p>
 * <b>功能: </b>创建一个类型的实例,包括基本类型及包装类型
 * <p>
 * <b>用法: </b>
 * 
 * <pre>
 * int i = (int)ClassUtils.newClassInstance(int.class)
 * </pre>
 * 
 * 看来方法很幼稚,但是在某些我们未知类型时却要创建这个类型的对象时,非常有用，主要用来解决序列化组件中创建陌生类型时使用.
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ClassUtils {

	/** 这个MAP中放置了Class到JAVA对象的映射 **/
	private static Map<Class<?>, Object> classToObjMap = new HashMap<Class<?>, Object>();

	static {
		classToObjMap.put(int.class, new Integer(0));
		classToObjMap.put(long.class, new Long(0));
		classToObjMap.put(float.class, new Float(0));
		classToObjMap.put(double.class, new Double(0));
		classToObjMap.put(boolean.class, Boolean.FALSE);
		classToObjMap.put(byte.class, new Byte((byte) 0));
		classToObjMap.put(char.class, new Character((char) 0));
		classToObjMap.put(short.class, new Short((short) 0));

		classToObjMap.put(java.lang.Integer.class, new Integer(0));
		classToObjMap.put(java.lang.Long.class, new Long(0));
		classToObjMap.put(java.lang.Float.class, new Float(0));
		classToObjMap.put(java.lang.Double.class, new Double(0));
		classToObjMap.put(java.lang.Boolean.class, Boolean.FALSE);
		classToObjMap.put(java.lang.Byte.class, new Byte((byte) 0));
		classToObjMap.put(java.lang.Character.class, new Character((char) 0));
		classToObjMap.put(java.lang.Short.class, new Short((short) 0));

		classToObjMap.put(java.sql.Date.class, new java.sql.Date(System.currentTimeMillis()));
	}

	/**
	 * 判断一个类是否存在，需要类的完全限定名，如果存在就讲类返回，不存在则返回空
	 * 
	 * @param className
	 *            类的完全限定名称
	 * @return
	 */
	public static Class<?> newClassInstance(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * 创建一个指定类型的实例对象,包括基本数据类型,例如int或byte
	 * 
	 * @param clazz
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T newClassInstance(Class<T> clazz) throws IllegalArgumentException, IllegalAccessException,
			InstantiationException {
		T t = null;
		if (SuperPojo.class.isAssignableFrom(clazz)) {
			// return
			// (T)ProtoEntityEnhancer.getEnhanceProtoEntityClass((Class<ProtoEntity>)clazz).newInstance();
			return clazz.newInstance();
		} else if (clazz.isArray()) {
			return (T) Array.newInstance(clazz.getComponentType(), 1);
		} else if (clazz.isEnum()) {
			return (T) clazz.getEnumConstants()[0];
		} else {
			t = (T) classToObjMap.get(clazz);
			if (t == null) {
				return clazz.newInstance();
			} else {
				return t;
			}
		}
	}

	/**
	 * 创建一个指定类路径的实例，此类路径为全路径
	 * 
	 * @param clazz
	 * @param classPath
	 * @return
	 */
	public static <T> T newClassInstance(Class<T> clazz, String classPath) {
		try {
			Class<?> classTemp = Class.forName(classPath);
			@SuppressWarnings("unchecked")
			T instance = (T) classTemp.newInstance();
			return instance;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("JavaEval.newClassInstance() Class.forName() found error:", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("JavaEval.newClassInstance() classTemp.newInstance() found error:", e);
		} catch (InstantiationException e) {
			throw new RuntimeException("JavaEval.newClassInstance() classTemp.newInstance() found error:", e);
		}
	}

	/**
	 * 处理类名字中$符号的问题
	 * 
	 * @param className
	 * @return
	 */
	public static String processClassName(String className) {
		StringBuffer result = new StringBuffer();
		StringBuffer memory = new StringBuffer();
		for (char c : className.toCharArray()) {
			if (c != '$') {
				if (memory.length() > 0) {
					if (memory.length() > 1) {
						result.append(memory);
					} else {
						result.append(".");
					}
					result.append(c);
					memory.delete(0, memory.length());
				} else {
					result.append(c);
				}
			} else {
				memory.append(c);
			}
		}
		return result.toString();
	}
}
