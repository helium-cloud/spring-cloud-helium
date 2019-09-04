package org.helium.framework.bundle;


import org.helium.framework.BeanContextService;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.util.ErrorList;

import java.util.List;

/**
 * 实现BundleManager的
 * Created by Coral on 7/18/15.
 */
@ServiceInterface(id = "helium:BundleManager")
public interface BundleManager {
	/**
	 * 获取全部已经安装,或正在安装的Bundle
	 * 包含Shared, App两种
	 * @return
	 * @see BundleHandler
	 */
	List<BundleHandler> getBundles();

	/**
	 *
	 * @param location Bundle的唯一地址, 在本系统中为"mvn:org.helium/rcs-as-group/2.0.3.07201840"
	 * @return
	 * @see BundleHandler
	 */
	BundleHandler getBundle(String location);

	/**
	 * 安装一个Bundle, 可安装普通Bundle与AppBundle
	 * @param location Bundle的唯一地址, 在本系统支持Osgi时可为"mvn:org.helium/rcs-as-group/2.0.3.07201840"
	 * @return 返回可操作的BundleState, Bundle的启动, 等操作均在BundleState上完成
	 * @see BundleHandler
	 */
	BundleHandler installBundle(String location) throws Exception;

	/**
	 * 安装一个Bundle
	 * @param bundle
	 */
	void addBundle(BundleHandler bundle);

	/**
	 * 卸载一个Bundle, 并将其移除
	 * @param location
	 */
	void uninstallBundle(String location) throws Exception;

	/**
	 * 在所有的Bundle中寻找一个Class
	 * @param className
	 * @param allowAmbiguous 如果存在多个同样的类则抛出异常
	 * @return 返回第一个命中的类
	 */
	Class<?> findClass(String className, boolean allowAmbiguous);

	/**
	 *
	 * @param className
	 * @return
	 */
	List<Class<?>> findAllClass(String className);

	/**
	 * 启动Bundle
	 * @return
	 */
	ErrorList startBundles();

	ErrorList assembleBundles(BeanContextService contextService);

	ErrorList updateBundles(BeanContextService contextService);

	ErrorList registerBundles(BeanContextService contextService);

	ErrorList stopBundles();

	ErrorList resolveBundles();
}
