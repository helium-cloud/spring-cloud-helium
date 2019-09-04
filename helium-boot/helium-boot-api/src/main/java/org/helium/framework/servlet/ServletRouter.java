package org.helium.framework.servlet;

import org.helium.framework.BeanContextService;
import org.helium.framework.module.ModuleContext;
import org.helium.util.CollectionUtils;
import org.helium.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Coral on 8/8/15.
 */
public class ServletRouter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServletRouter.class);
	private List<ServletContext> servlets = new ArrayList<>();
	private List<ServletContext> servletsForRead = new ArrayList<>();

	public ServletRouter(String protocol, BeanContextService contextService) {
		contextService.syncBeans(
				(sender, modification) -> {
					switch (modification.getAction()) {
						case INSERT:
							LOGGER.info("INSERT Servlet={}", modification.getBeanContext().getId());
							addServlet((ServletContext) modification.getBeanContext());
							break;
						case UPDATE:
							LOGGER.info("UPDATE Servlet={}", modification.getBeanContext().getId());
							updateServlet((ServletContext) modification.getBeanContext());
							break;
						case DELETE:
							LOGGER.info("REMOVE Servlet={}", modification.getBeanContext().getId());
							removeServlet((ServletContext) modification.getBeanContext());
							break;
					}
				},
				bc -> {
					if (!(bc instanceof ServletContext)) {
						return false;
					}
					ServletContext sc = (ServletContext)bc;
					if (!protocol.equals(sc.getProtocol())) {
						return false;
					}
					if (sc.isLocal()) {
						return TypeUtils.isTrue(sc.getConfiguration().getExport());
					} else {
						return true;
					}
				}
		);
	}

	/**
	 * 返回所有结果, 不进行Filter计算
	 * @param ctx
	 * @param args
	 * @return
	 */
	public ServletMatchResults matchAll(ModuleContext ctx, Object... args) {
		return match(ctx, ServletMatchResult.ALL_FILTER, args);
	}

	/**
	 * 返回结果, 使用传入的过滤器参与计算
	 * @param ctx
	 * @param filter
	 * @param args
	 * @return
	 */
	public ServletMatchResults match(ModuleContext ctx, ServletMatchResult.Filter filter, Object... args) {
		ServletMatchResults results = new ServletMatchResults();
		for (ServletContext servlet: servletsForRead) {
			ServletMatchResults r2 = servlet.matchRequest(ctx, filter, args);
			results.addResults(r2);
		}
		//
		// 过滤两遍,
		results.applyFilterLast(filter);
		//
		// 处理灰度
		results.applyExperimentFilter();
		return results;
	}

	private void addServlet(ServletContext servlet) {
		synchronized (this) {
			servlets.add(servlet);
			servletsForRead = CollectionUtils.cloneList(servlets);
		}
	}

	private void removeServlet(ServletContext servlet) {
		synchronized (this) {
			servlets.removeIf(a -> a.getId().equals(servlet.getId()));
			servletsForRead = CollectionUtils.cloneList(servlets);
		}
	}

	private void updateServlet(ServletContext servlet) {
		synchronized (this) {
			int k = -1;
			for (int i = 0; i < servlets.size(); i++) {
				ServletContext old = servlets.get(i);
				if (old.getId().equals(servlet.getId())) {
					k = i;
				}
			}
			if (k < 0) {
				LOGGER.error("ServletRouter.updateServlet failed: unknownServlet:{}", servlet);
				return;
			}
			servlets.set(k, servlet);
			servletsForRead = CollectionUtils.cloneList(servlets);
		}
	}
}