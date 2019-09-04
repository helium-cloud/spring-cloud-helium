package org.helium.dashboard;

import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.framework.entitys.dashboard.WorkerJson;

import org.helium.framework.route.center.CentralizedService;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServletContext;
import org.helium.http.servlet.extension.DataSourceServlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/28/15.
 */
@ServletImplementation(id = "h2c:GetRoutersServlet")
@HttpMappings(contextPath = "/dashboard", urlPattern = "/workers.json")
public class GetWorkersServlet extends DataSourceServlet<WorkerJson> {
	@Override
	protected List<WorkerJson> readData(HttpServletContext context) {
		CentralizedService service = BeanContext.getContextService().getCentralizedService();
		if (service != null) {
			return service.getWorkers();
		} else {
			return new ArrayList<>();
		}
	}
}
