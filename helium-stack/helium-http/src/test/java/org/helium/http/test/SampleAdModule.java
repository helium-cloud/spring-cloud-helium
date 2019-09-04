package org.helium.http.test;

import org.helium.framework.module.ModuleState;
import org.helium.http.servlet.HttpModule;
import org.helium.http.servlet.HttpServletContext;

/**
 * Created by Coral on 10/29/15.
 */
public class SampleAdModule implements HttpModule {
	@Override
	public String getContextPath() {
		return null;
	}

	@Override
	public ModuleState processModule(HttpServletContext context) {
		return null;
	}

	@Override
	public boolean isMatch(HttpServletContext context) {
		return false;
	}
}
