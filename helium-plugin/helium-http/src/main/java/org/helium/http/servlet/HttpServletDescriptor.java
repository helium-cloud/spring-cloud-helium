package org.helium.http.servlet;

import org.helium.framework.entitys.ServletMappingsNode;
import org.helium.framework.servlet.ServletDescriptor;
import org.helium.framework.servlet.ServletMappings;

/**
 * Created by Lei Gao on 7/23/15.
 */
public class HttpServletDescriptor implements ServletDescriptor {
	public static final String PROTOCOL = "http";
	public static final ServletDescriptor INSTANCE = new HttpServletDescriptor();

	@Override
	public String getProtocol() {
		return PROTOCOL;
	}

	@Override
	public ServletMappings parseMappings(ServletMappingsNode node) {
		HttpServletMappings mappings = new HttpServletMappings();
		mappings.initWithConfig(node);
		return mappings;
	}

	@Override
	public boolean isServlet(Object servlet) {
		return false;
	}

//	@Override
//	public ServletMappings getServletMappings(BeanContext context) {
//		return null;
//	}
}
