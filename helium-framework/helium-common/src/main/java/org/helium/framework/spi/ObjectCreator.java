package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.entitys.ObjectWithSettersNode;
import org.helium.util.ErrorList;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.function.Function;

/**
 * Created by Coral on 7/20/15.
 */
public class ObjectCreator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObjectCreator.class);
	/**
	 * 加载一个类
	 * @param className
	 * @return
	 */
	public static Class<?> loadClass(String className) {
		return DEFAULT_LOADER.apply(className);
	}

	/**
	 * 使用固定的类加载器加载一个类
	 * @param className
	 * @param loader
	 * @return
	 */
	public static Class<?> loadClass(String className, Function<String, Class> loader) {
		return loader.apply(className);
	}

	/**
	 * 使用<object/>节点通过Injector创建一个对象, 对不支持的SetterField报错, 默认的类加载器
	 * @param node
	 * @return
	 */
	public static Object createObject(ObjectWithSettersNode node, BeanContextService contextService, Function<String, Class> loader) {
		Object object = createObject(node.getClassName(), loader);
		node.setSetters(AnnotationResolver.resolveSetters(object.getClass(), node.getSetters(), null));
		SetterInjector.injectSetters(object, node.getSetters(), (sn) -> {
			BeanContext bc = contextService.getBean(sn.getInnerText());
			SetterInjector.setField(object, sn.getField(), bc.getBean());
		});
		return object;
	}

	/**
	 * 使用<object/>节点通过Injector创建一个对象, 对不支持的SetterField报错, 默认的类加载器
	 * @param node
	 * @return
	 */
	public static Object createObject(ObjectWithSettersNode node) {
		return createObject(node, DEFAULT_LOADER);
	}

	/**
	 * 使用<object/>节点通过Injector创建一个对象, 对不支持的SetterField报错, 默认的类加载器
	 * @param node
	 * @return
	 */
	public static Object createObject(ObjectWithSettersNode node, Function<String, Class> loader) {
		Object object;
		if (StringUtils.isNullOrEmpty(node.getParams())) {
			object = createObject(node.getClassName(), loader);
		} else {
			object = createObjectWithParams(node.getClassName(), node.getParams(), loader);
		}

		SetterInjector.injectSetters(object, node.getSetters(), false);
		return object;
	}



	/**
	 * 创建一个对象, 使用默认类加载器
	 * @param className
	 * @return
	 */
	public static Object createObject(String className) {
		return createObject(className, DEFAULT_LOADER);
	}

	public static Object createObject(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception ex) {
			throw new IllegalArgumentException("createObject() failed:" + clazz.getName(), ex);
		}
	}

	/**
	 * 创建一个对象, 使用指定类加载器
	 * @param className
	 * @param loader
	 * @return
	 */
	public static Object createObject(String className, Function<String, Class> loader) {
		Class<?> clazz = loader.apply(className);
		return createObject(clazz);
	}

	/**
	 * 使用构造函数创建一个类, 不支持转义
	 * @param className
	 * @param loader
	 * @param params
	 * @return
	 */
	public static Object createObjectWithParams(String className, String params, Function<String, Class> loader) {
		Class<?> clazz = loader.apply(className);
		String[] ss = StringUtils.split(params, ",");
		Object[] args = new Object[ss.length];
		for (Constructor<?> ctor: clazz.getConstructors()) {
			if (ctor.getParameterCount() != ss.length) {
				continue;
			}

			String ctorInfo = "ctor";
			for (int i = 0; i < ss.length; i++) {
				ctorInfo = ctorInfo + ":" + ctor.getParameterTypes()[i].getSimpleName();
			}

			ErrorList errorList = new ErrorList();
			for (int i = 0; i < ss.length; i++) {
				Class<?> argType = ctor.getParameterTypes()[i];
				SetterFieldType fieldType = SetterFieldType.valueOf(argType);
				if (fieldType == null) {
					break;
				}
				try {
					if (StringUtils.isNullOrEmpty(ss[i])) {
						args[i] = null;
					} else {
						args[i] = fieldType.convertFrom(argType, ss[i]);
					}
				} catch (Exception ex) {
					String label = String.format(ctorInfo + " args:%d can't assign with", i, ss[i]);
					throw new IllegalArgumentException(label, ex);
				}
			}
			try {
				return ctor.newInstance(args);
			} catch (Exception ex) {
				throw new IllegalArgumentException(ctorInfo + " failed with (" + params + ")", ex);
			}
		}
		throw new IllegalArgumentException("Can't find creator for :" + className + "(" + params + ")");
	}

	public static final Function<String, Class> DEFAULT_LOADER = className -> {
		try {
			return Class.forName(className);
		} catch (Exception ex) {
			throw new IllegalArgumentException("Class not found:" + className);
		}
	};

	public static Function<String, Class> getClassLoader(Class<?> beanType) {
		return name -> {
			try {
				return beanType.getClassLoader().loadClass(name);
			} catch (Exception ex) {
				throw new IllegalArgumentException("Class not found:" + name);
			}
		};
	}
}
