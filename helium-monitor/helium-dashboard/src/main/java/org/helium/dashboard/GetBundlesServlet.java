package org.helium.dashboard;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceSetter;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.framework.bundle.BundleManager;
import org.helium.framework.entitys.dashboard.BeanJson;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServletContext;
import org.helium.http.servlet.extension.DataSourceServlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/28/15.
 */
@ServletImplementation(id = "h2c:GetBundlesServlet")
@HttpMappings(contextPath = "/console", urlPattern = "beans.json")
public class GetBundlesServlet extends DataSourceServlet<BeanJson> {
	@ServiceSetter
	private BundleManager bundleManager;

	@Override
	protected List<BeanJson> readData(HttpServletContext context) {
		List<BeanJson> list = new ArrayList<>();
		for (BeanContext bc: BeanContext.getContextService().getBeans()) {
			BeanJson bj = new BeanJson();
			bj.setId(bc.getId().toString());
			bj.setType(bc.getType().toString());
			bj.setState(bc.getState().toString());
			list.add(bj);
		}
		return list;
	}
}
