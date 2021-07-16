package org.helium.superpojo.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class BeanUtils {

	/**
	 * 获取JavaBeans中一个属性的Get方法
	 *
	 * @param clazz
	 * @param propertyType
	 * @param propertyName
	 * @return
	 */
	public static Method findGetMethod(Class<?> clazz, Type propertyType, String propertyName) {
		if (propertyType == boolean.class) {
			String methodName1 = "get" + propertyName;
			String methodName2 = boolMethodName(propertyName);
			for (Method method : clazz.getMethods()) {
				if (method.getName().equalsIgnoreCase(methodName1) || method.getName().equalsIgnoreCase(methodName2)) {
					return method;
				}
			}
		} else {
			String methodName = "get" + propertyName;
			for (Method method : clazz.getMethods()) {
				if (method.getName().equalsIgnoreCase(methodName)) {
					return method;
				}
			}
		}
		throw new RuntimeException(String.format("Not found getter method .Class:[%s],propertyName:[%s]",
				clazz.getName(), propertyName));
	}

	/**
	 * 获取JavaBeans中一个属性的Set方法
	 *
	 * @param clazz
	 * @param propertyType
	 * @param propertyName
	 * @return
	 */
	public static Method findSetMethod(Class<?> clazz, Type propertyType, String propertyName) {
		if (propertyType == boolean.class) {
			String methodName1 = "set" + propertyName;
			String methodName2 = boolMethodName(propertyName);
			for (Method method : clazz.getMethods()) {
				if (method.getName().equalsIgnoreCase(methodName1) || method.getName().equalsIgnoreCase(methodName2)) {
					return method;
				}
			}
		} else {
			String methodName = "set" + propertyName;
			for (Method method : clazz.getMethods()) {
				if (method.getName().equalsIgnoreCase(methodName)) {
					return method;
				}
			}
		}
		throw new RuntimeException(String.format("Not found getter method .Class:[%s],propertyName:[%s]",
				clazz.getName(), propertyName));
	}

	/**
	 * 用于封装了boolean类型方法名称不同的逻辑
	 *
	 * @param propertyName
	 * @return
	 */
	private static String boolMethodName(String propertyName) {
		StringBuffer methodStr = new StringBuffer();
		if (propertyName.length() > 1 && (propertyName.substring(0, 2).startsWith("is"))) {

			int threeChar = propertyName.charAt(2);
			// 如果FieldName = isBoolean , 则 GetterName = isBoolean
			if (threeChar >= 65 && threeChar <= 90) {
				methodStr.append(String.valueOf(propertyName.charAt(0)).toLowerCase())
						.append(propertyName.substring(1));
			} else {
				// 如果FieldName = isboolean , 则 GetterName = isIsboolean
				methodStr.append("is").append(String.valueOf(propertyName.charAt(0)).toUpperCase())
						.append(propertyName.substring(1));
			}

		} else {
			// 否则 isBoolean
			methodStr.append("is").append(String.valueOf(propertyName.charAt(0)).toUpperCase())
					.append(propertyName.substring(1));
		}
		return methodStr.toString();
	}
}
