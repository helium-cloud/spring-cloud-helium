package com.feinno.superpojo.util;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <b>描述: </b>用于序列化组件,具有Java泛型的类型探测功能的类，与{@link GenericsUtils}的区别是该类具有更深层的泛型探测功能
 * <p>
 * <b>功能: </b>Java泛型的类型探测
 * <p>
 * <b>用法: </b>正常静态方法调用
 * 
 * <pre>
 * 例如获取当前类的第一个字段的泛型信息
 * ProtoGenericsUtils.getClass(this.getClass().getFields()[0]);
 * </pre>
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class ProtoGenericsUtils {

	/**
	 * 获得某一个字段的真实类型<br>
	 * Java的泛型为假泛型，Class中不包含其泛型信息，想取得其类型信息以及泛型类型信息，请用field.getGenericType()方法
	 * 
	 * @param field
	 * @return
	 */
	public static Class<?> getClass(Field field) {
		return getClass(field.getGenericType());
	}

	/**
	 * 获得某一个Type的真实类型<br>
	 * Java的泛型为假泛型，Class中不包含其泛型信息，想取得其类型信息以及泛型类型信息，请用field.getGenericType()方法
	 * 
	 * @param type
	 * @return
	 */
	public static Class<?> getClass(Type type) {
		if (type instanceof ParameterizedType) {
			return getClass(((ParameterizedType) type).getRawType());
		} else if (type instanceof GenericArrayType) {
			return getClass(((GenericArrayType) type).getGenericComponentType());
		} else if (type instanceof Class<?>) {
			if (((Class<?>) type).isArray()) {
				return ((Class<?>) type).getComponentType();
			} else {
				return (Class<?>) type;
			}
		}
		return type.getClass();
	}

	/**
	 * 获得某一个字段的泛型的真实类型<br>
	 * Java的泛型为假泛型，Class中不包含其泛型信息，想取得其类型信息以及泛型类型信息，请用field.getGenericType()方法
	 * 
	 * @param field
	 * @return
	 */
	public static Class<?> getGenericsClass(Field field, int index) {
		return getClass(getGenericType(field, index));
	}

	/**
	 * 获得某一个类型的泛型的真实类型<br>
	 * Java的泛型为假泛型，Class中不包含其泛型信息，想取得其类型信息以及泛型类型信息，请用field.getGenericType()方法
	 * 
	 * @param field
	 * @return
	 */
	public static Class<?> getGenericsClass(Type type, int index) {
		return getClass(getGenericType(type, index));
	}

	/**
	 * 获取父类型中的泛型类型
	 * 
	 * @param field
	 * @return
	 */
	public static Class<?> getSuperClassGenricClass(Field field, int index) {
		return getGenericsClass(field.getType().getGenericSuperclass(), index);
	}

	/**
	 * 获取父类型中的泛型类型
	 * 
	 * @param field
	 * @return
	 */
	public static Class<?> getSuperClassGenricClass(Type type, int index) {
		return getGenericsClass(getClass(type).getGenericSuperclass(), index);
	}

	/**
	 * 获得一个字段的完整类型名称，<br>
	 * 请注意，如果该字段中有泛型，那么该名称为类型加泛型的名称，例如针对List它会返回
	 * <code>java.util.List<String></code>
	 * 
	 * @param field
	 * @return
	 */
	public static String getClassFullName(Field field) {
		return getClassFullName(field.getGenericType());
	}

	/**
	 * 获得一个类型的完整类型名称，<br>
	 * 请注意，如果该字段中有泛型，那么该名称为类型加泛型的名称，例如针对List它会返回
	 * <code>java.util.List<String></code>
	 * 
	 * @param type
	 * @return
	 */
	public static String getClassFullName(Type type) {
		String returnStr = Object.class.getName();
		if (type instanceof ParameterizedType) {
			returnStr = ((ParameterizedType) type).toString();
		} else if (type instanceof GenericArrayType) {
			returnStr = ((GenericArrayType) type).getGenericComponentType().toString();
		} else if (type instanceof Class<?>) {
			if (((Class<?>) type).isArray()) {
				returnStr = ((Class<?>) type).getComponentType().getName();
			} else {
				returnStr = getWrapsType((Class<?>) type).getName();
			}
		}

		return ClassUtil.processClassName(returnStr);
	}

	/**
	 * 获得一个字段的第index位泛型的完整类型名称，<br>
	 * 请注意，如果该类型中有泛型，那么该名称为类型加泛型的名称，例如针对List它会返回
	 * <code>java.util.List<String></code>
	 * 
	 * @param field
	 * @return
	 */
	public static String getGenericsClassFullName(Field field, int index) {
		return getGenericsClassFullName(field.getGenericType(), index);
	}

	/**
	 * 获得一个类型的第index位泛型的完整类型名称，<br>
	 * 请注意，如果该字段中有泛型，那么该名称为类型加泛型的名称，例如针对List它会返回
	 * <code>java.util.List<String></code>
	 * 
	 * @param type
	 * @return
	 */
	public static String getGenericsClassFullName(Type type, int index) {
		String fullName = getClassFullName(getGenericType(type, index));
		return ClassUtil.processClassName(fullName);
	}

	/**
	 * 获得某一个类型的泛型的第index位真实Type类型<br>
	 * 提供这个方法是因为JAVA的泛型是假泛型，返回的class无法保存类型的泛型信息，而JDK1.5后提供完善的
	 * {@link java.lang.reflect.Type}类型，它其中可以存储着泛型信息，是
	 * {@link Field#getGenericType()}方法的返回值
	 * 
	 * @param field
	 * @param index
	 * @return
	 */
	public static Type getGenericType(Field field, int index) {
		return getGenericType(field.getGenericType(), index);
	}

	/**
	 * 获得某一个Type类型的泛型的第index位真实Type类型<br>
	 * 提供这个方法是因为JAVA的泛型是假泛型，返回的class无法保存类型的泛型信息，而JDK1.5后提供完善的
	 * {@link java.lang.reflect.Type}类型，它其中可以存储着泛型信息，是
	 * {@link Field#getGenericType()}方法的返回值
	 * 
	 * @param type
	 * @param index
	 * @return
	 */
	public static Type getGenericType(Type type, int index) {
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type[] childTypes = parameterizedType.getActualTypeArguments();
			if (childTypes.length > index) {
				return childTypes[index];
			} else {
				return Object.class;
			}
		} else if (type instanceof GenericArrayType) {
			GenericArrayType genericArrayType = (GenericArrayType) type;
			return genericArrayType.getGenericComponentType();
		} else if (type instanceof Class<?>) {
			if (((Class<?>) type).isArray()) {
				// 数组也想想成是一种泛型，一种只可以存储指定类型的集合
				return ((Class<?>) type).getComponentType();
			} else {
				// 他就不是一个泛型，所以此处可以是任何类型
				return Object.class;
			}
		}
		return Object.class;
	}

	/**
	 * 通过包装类型返回他的原始类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?> getPrimitive(Class<?> clazz) {
		if (clazz == java.lang.Integer.class) {
			clazz = int.class;
		} else if (clazz == java.lang.Long.class) {
			clazz = long.class;
		} else if (clazz == java.lang.Float.class) {
			clazz = float.class;
		} else if (clazz == java.lang.Double.class) {
			clazz = double.class;
		} else if (clazz == java.lang.Boolean.class) {
			clazz = boolean.class;
		} else if (clazz == java.lang.Byte.class) {
			clazz = byte.class;
		} else if (clazz == java.lang.Character.class) {
			clazz = char.class;
		} else if (clazz == java.lang.Short.class) {
			clazz = short.class;
		}
		return clazz;
	}

	/**
	 * 通过原始类型返回他的包装类型
	 * 
	 * @param clazz
	 * @return
	 */
	public static Class<?> getWrapsType(Class<?> clazz) {
		if (clazz == int.class) {
			clazz = java.lang.Integer.class;
		} else if (clazz == long.class) {
			clazz = java.lang.Long.class;
		} else if (clazz == float.class) {
			clazz = java.lang.Float.class;
		} else if (clazz == double.class) {
			clazz = java.lang.Double.class;
		} else if (clazz == boolean.class) {
			clazz = java.lang.Boolean.class;
		} else if (clazz == byte.class) {
			clazz = java.lang.Byte.class;
		} else if (clazz == char.class) {
			clazz = java.lang.Character.class;
		} else if (clazz == short.class) {
			clazz = java.lang.Short.class;
		}
		return clazz;
	}
}
