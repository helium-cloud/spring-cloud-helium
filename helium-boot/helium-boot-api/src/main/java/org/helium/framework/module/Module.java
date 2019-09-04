package org.helium.framework.module;

/**
 * Module的基础模型
 * 1. 允许Servlet的逻辑
 * Created by Coral on 7/6/15.
 */
public interface Module<E extends ModuleContext> {
	/**
	 *
	 * @param context
	 * @return
	 */
	ModuleState processModule(E context);

	/**
	 *
	 * @param context
	 * @return
	 */
	boolean isMatch(E context);
}
