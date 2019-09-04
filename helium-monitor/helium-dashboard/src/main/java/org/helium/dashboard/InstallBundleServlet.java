package org.helium.dashboard;

import org.helium.framework.BeanContextService;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.bundle.BundleManager;
import org.helium.framework.entitys.dashboard.BeanJson;
import org.helium.http.servlet.HttpServletContext;
import org.helium.http.servlet.extension.DataSourceServlet;

import java.util.List;

/**
 * Created by Coral on 7/18/15.
 */
public class InstallBundleServlet extends DataSourceServlet<BeanJson> {
	@ServiceSetter
	private BundleManager bundleManager;

	@ServiceSetter
	private BeanContextService contextService;

	@Override
	protected List<BeanJson> readData(HttpServletContext context) {
		String location = context.getRequest().getParameter("location");
		return null;
	}
}
