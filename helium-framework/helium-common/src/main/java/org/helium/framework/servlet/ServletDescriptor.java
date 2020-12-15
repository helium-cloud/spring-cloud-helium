package org.helium.framework.servlet;

import org.helium.framework.BeanContext;
import org.helium.framework.entitys.ServletMappingsNode;

/**
 * 用于描述一种类型Servlet的帮助类
 * Created by Coral on 7/23/15.
 */
public interface ServletDescriptor {
	String CONTEXT_KEY_PROTOCOL = "ServletProtocol";

	/**
	 *
	 * @return
	 */
	String getProtocol();

	/**
	 *
	 * @param node
	 * @return
	 */
	ServletMappings parseMappings(ServletMappingsNode node);

	/**
	 *
	 * @param bc
	 * @return
	 */
	default boolean isBeanContext(BeanContext bc) {
		return (bc instanceof ServletContext) && getProtocol().equals(((ServletContext) bc).getProtocol());
	}

	/**
	 * 是否
	 * @param servlet
	 * @return
	 */
	boolean isServlet(Object servlet);
}
