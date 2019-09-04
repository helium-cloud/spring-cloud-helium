package org.helium.framework.configuration;

import com.feinno.superpojo.SuperPojo;

/**
 * 启动过程中的加载器
 * Created by Coral on 8/27/15.
 */
public interface BeanContextProvider extends ConfigProvider {
	/**
	 * 加载类
	 * @param className
	 * @return
	 */
	Class<?> loadClass(String className);

	/**
	 * 加载一个BeanConfiguration, 或BundleConfiguration
	 * @param path
	 * @return
	 */
	<E extends SuperPojo> E loadContentXml(String path, Class<E> clazz);

	/**
	 * 创建一个对象
	 * @param className
	 * @return
	 */
	default Object createObject(String className) {
		try {
			Class<?> clazz = loadClass(className);
			return clazz.newInstance();
		} catch (Exception ex) {
			throw new IllegalArgumentException("createObject() failed:" + className, ex);
		}
	}
}
