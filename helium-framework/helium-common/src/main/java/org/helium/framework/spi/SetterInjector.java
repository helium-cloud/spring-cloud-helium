package org.helium.framework.spi;

import org.helium.framework.annotations.FieldLoaderType;
import org.helium.framework.configuration.FieldLoader;
import org.helium.framework.entitys.SetterNode;
import org.helium.util.Action;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * 对象Setter注入器
 *
 * Created by Coral
 */
public class SetterInjector {
	private static final Logger LOGGER = LoggerFactory.getLogger(SetterInjector.class);

	/**
	 *
	 * @param obj
	 * @param setters
	 */
	public static void injectSetters(Object obj, List<SetterNode> setters, boolean skipUnknownFields) {
		if (obj == null) {
			throw new IllegalArgumentException("injectSetters obj=null");
		}
		if (setters == null || setters.size() == 0) {
			return;
		}
		for (SetterNode node : setters) {
			try {
				if (!injectFieldSetter(obj, node) && !skipUnknownFields) {
					throw new IllegalArgumentException("Unknown fieldSetters:" + node.getField());
				}
			} catch (Exception ex) {
				LOGGER.error("Setter execute field=" + node.getField() + "\n{}", ex);
				throw new IllegalArgumentException("Setter execute field=" + node.getField(), ex);
			}
		}
	}

	public static void injectSetters(Object obj, List<SetterNode> setters, Action<SetterNode> unknownProc) {
		if (obj == null) {
			throw new IllegalArgumentException("injectSetters obj=null");
		}
		if (setters == null || setters.size() == 0) {
			return;
		}
		for (SetterNode node : setters) {
			try {
				if (!injectFieldSetter(obj, node)) {
					unknownProc.run(node);
				}
			} catch (Exception ex) {
				LOGGER.error("Setter execute field=" + node.getField() + "\n{}", ex);
				throw new IllegalArgumentException("Setter execute field=" + node.getField(), ex);
			}
		}
	}

	/**
	 * 使用<setter/>节点完成一个field的注入
	 * @param obj
	 * @param node
	 * @return true表示成功, false表示Field类型未知
	 */
	public static boolean injectFieldSetter(Object obj, SetterNode node) {
		//
		// 目前使用的时private字段注入, 貌似也没有问题, 不知道有没有必要改成setter
		String fieldName = node.getField();
		Field field = getField(obj.getClass(), fieldName);
		if (field == null) {
			throw new IllegalArgumentException("Field not found: " + fieldName);
		}

		//
		// 如果存在loader的配置或者批注, 优先使用loader
		// 1. <setter loader="com.sample.MyLoader"></>
		// 2. field类型上的SetterFieldType标注
		Class<?> loaderClazz = null;
		Class<?> fieldClazz = field.getType();
		if (!StringUtils.isNullOrEmpty(node.getLoader())) {
			try {
				loaderClazz = Class.forName(node.getLoader());
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("loader ClassNotFound:" + node.getLoader());
			}
		} else {
			FieldLoaderType anno = getAnnotation(fieldClazz, FieldLoaderType.class);
			if (anno != null) {
				loaderClazz = anno.loaderType();
			}
		}

		if (loaderClazz != null) {
			LOGGER.info("use FieldLoader:<{}> field=" + fieldName, loaderClazz);
			FieldLoader loader = null;
			try {
				loader = (FieldLoader)loaderClazz.newInstance();
			} catch (Exception e) {
				throw new IllegalArgumentException("loader create failed:" + loaderClazz.getName(), e);
			}
			Object v = loader.loadField(node, field);
			setField(obj, field, v);
			return true;
		}

		//
		// 处理已知类型
		SetterFieldType fieldType = SetterFieldType.valueOf(fieldClazz);
		if (fieldType != null) {
			LOGGER.info("setKnowndType:<{}> field=" + fieldName, fieldType);
			Object fieldlValue = fieldType.convertFrom(fieldClazz, node);
			setField(obj, field, fieldlValue);
			return true;
		}

		//
		// 未知类型
		return false;
	}

	private static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annoClazz) {
		for (; clazz != null && clazz != Object.class ; clazz = clazz.getSuperclass()) {
			A anno = clazz.getAnnotation(annoClazz);
			if (anno != null) {
				return anno;
			}
		}
		return null;
	}


	/**
	 * 通过反射设置字段值
	 * @param obj
	 * @param field
	 * @param value
	 */
	public static void setField(Object obj, String field, Object value) {
		Field f = getField(obj.getClass(), field);
		if (f == null) {
			throw new IllegalArgumentException("Field not found:" + field);
		}
		setField(obj, f, value);
	}



	/**
	 * 通过反射设置字段值
	 *
	 * @param obj
	 * @param field
	 * @param value
	 */
	public static void setField(Object obj, Field field, Object value) {
		try {
			field.setAccessible(true);
			field.set(obj, value);
			field.setAccessible(false);
		} catch (IllegalAccessException e) {
			LOGGER.error("Invoke failed.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 根据名称从object对象中取出Field,因为允许object中布存在这个Field，所以异常均被忽略
	 *
	 * @param clazz
	 * @param fieldName
	 * @return
	 */
	public static Field getField(Class<?> clazz, String fieldName) {
		Field field = null;
		for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
			try {
				field = clazz.getDeclaredField(fieldName);
				return field;
			} catch (NoSuchFieldException ex) {
			} catch (Exception ex) {
				LOGGER.error("getField failed:<" + fieldName + "> {}", ex);
			}
		}
		return null;
	}
}
