package org.helium.framework.route;

import org.helium.framework.BeanContext;

/**
 * Created by Coral on 7/25/15.
 */
public interface BeanContextRemote extends BeanContext {
	@Override
	default boolean isLocal() {
		return false;
	}

	/**
	 * 获取ServerRouter
	 * @return
	 */
	ServerRouter getRouter();
}
