package org.helium.framework.servlet;

import org.helium.framework.module.Module;
import org.helium.framework.module.ModuleContext;

/**
 * Created by Coral on 7/7/15.
 */
public interface ServletModule<E extends ModuleContext> extends Module<E> {
	/**
	 *
	 * @return
	 */
	ServletDescriptor getServletDescriptor();
}
