package org.helium.http.servlet;

import org.helium.framework.servlet.ServletDescriptor;
import org.helium.framework.servlet.ServletModule;

/**
 * HttpModule
 * Create by Lei Gao on 7/7/15.
 */
public interface HttpModule extends ServletModule<HttpServletContext> {
	default ServletDescriptor getServletDescriptor() {
		return HttpServletDescriptor.INSTANCE;
	}

	String getContextPath();
}
