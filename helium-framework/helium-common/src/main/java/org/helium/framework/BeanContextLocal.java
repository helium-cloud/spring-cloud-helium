package org.helium.framework;

import org.helium.framework.configuration.FieldDependency;

import java.util.List;
import java.util.concurrent.Executor;

/**
 *
 * Created by Coral on 7/25/15.
 */
public interface BeanContextLocal extends BeanContext {
	@Override
	default boolean isLocal() {
		return true;
	}

	/**
	 * 是否可以导出
	 * @return
	 */
	// boolean exportable();

	/**
	 * 获取线程池配置
	 * @return
	 */
	Executor getExecutor();

	/**
	 * 注册
	 * @param contextService
	 * @return
	 */
	boolean register(BeanContextService contextService);

	/**
	 * 装配
	 * @see BeanContextState
	 * @return
	 */
	boolean assemble(BeanContextService contextService);

	/**
	 * 启动
	 * @see BeanContextState
	 * @return
	 */
	boolean start();

	/**
	 * 停止
	 * @see BeanContextState
	 */
	boolean stop();

	/**
	 * 获取依赖的Beans
	 * @return
	 */
	List<FieldDependency> getReferences();
}
