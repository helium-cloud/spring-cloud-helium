package org.helium.http.test;

import org.helium.framework.annotations.ModuleImplementation;
import org.helium.http.servlet.HttpBasicAuthModule;

/**
 * Created by Coral on 10/29/15.
 */
@ModuleImplementation
public class SampleAuthModule extends HttpBasicAuthModule {
	@Override
	protected boolean doAuthentication(String user, String passwd) {
		return "user".equals(user) && "bb111".equals(passwd);
	}
}
