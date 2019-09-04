package org.helium.dashboard;

import com.feinno.superpojo.util.StringUtils;
import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServletImplementation;
import org.helium.framework.entitys.dashboard.BeanJson;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.route.center.ServletReferenceCombo;
import org.helium.framework.spi.BeanReference;
import org.helium.http.logging.LogUtils;
import org.helium.http.servlet.HttpMappings;
import org.helium.http.servlet.HttpServletContext;
import org.helium.http.servlet.extension.DataSourceServlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 7/28/15.
 */
@ServletImplementation(id = "h2c:GetBeansServlet")
@HttpMappings(contextPath = "/dashboard", urlPattern = "/beans.json")
public class GetBeansServlet extends DataSourceServlet<BeanJson> {
	@Override
	protected List<BeanJson> readData(HttpServletContext context) {
		List<BeanJson> list = new ArrayList<>();
		for (BeanContext bc: BeanContext.getContextService().getBeans()) {
			BeanJson bean = new BeanJson();
			Object bundleInfo = bc.getAttachment("bundle");
			bean.setBundle(bundleInfo != null ? bundleInfo.toString() : "NULL");
			bean.setId(bc.getId().toString());
			bean.setType(bc.getType().toString());

			if (bc.getLastError() != null) {
				String msg = bc.getState().toString() + " ERROR:" +
						LogUtils.formatError(bc.getLastError());
				bean.setState(msg);
			} else {
				bean.setState(bc.getState().toString());
			}
			if (bc instanceof BeanReference) {
				BeanReference ref = (BeanReference)bc;
				String urls = combineUrls(ref.getRouter().getAllUrls());
				bean.setServiceUrls(urls);
			} else if (bc instanceof ServletReferenceCombo) {
				ServletReferenceCombo sr = (ServletReferenceCombo)bc;
				String urls = combineUrls(sr.getServiceUrls());
				bean.setServiceUrls(urls);
			} else {
				bean.setServiceUrls("local://");
			}
			list.add(bean);
		}

		list.sort((l, r) -> {
			boolean lLocal = l.getBundle().endsWith("LOCAL");
			boolean rLocal = r.getBundle().endsWith("LOCAL");

			if (lLocal & !rLocal) {
				return -1;
			}
			if (!lLocal & rLocal) {
				return 1;
			}
			int r1 = l.getBundle().compareTo(r.getBundle());
			return r1 != 0 ? r1 : l.getId().compareTo(r.getId());
		});
		return list;
	}

	private String combineUrls(List<ServerUrl> urls) {
		StringBuilder str = new StringBuilder();
		for (ServerUrl url: urls) {
			str.append(url.toString()).append("\n");
		}
		return StringUtils.trimEnd(str.toString(), '\n');
	}
}
