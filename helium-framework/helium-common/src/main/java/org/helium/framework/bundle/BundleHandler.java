package org.helium.framework.bundle;

import org.helium.framework.entitys.BundleConfiguration;

/**
 * 处理Bundle的启动加载等事宜
 * Created by Coral on 6/30/15.
 */
public interface BundleHandler {
	/**
	 * 在BundleManager中的唯一标识
	 * @return
	 */
	String getLocation();

	/**
	 * 获取Bundle名称
	 * @return
	 */
	String getName();

	/**
	 * 获取Bundle版本
	 * @return
	 */
	String getVersion();

	/**
	 * 是否AppBundle
	 * @return
	 */
	boolean isAppBundle();

	/**
	 * 获取Bundle的配置信息
	 * @return
	 */
	BundleConfiguration getConfiguration();

	/**
	 * 获取运行状态
	 * @return
	 */
	BundleState getState();

	/**
	 * 如果处于错误状态时, 获取错误信息
	 * @return
	 */
	Throwable getLastError();

	/**
	 * 分析一个Bundle的内容
	 * @return
	 */
	boolean resolve();

	/**
	 * 启动Bundle及内部包含的Beans
	 */
	boolean start();

	/**
	 * 停止Bundle及内部包含的Beans
	 * @return
	 */
	boolean stop();

	/**
	 * 卸载一个Bundle
	 * @return
	 */
	boolean uninstall();
}
