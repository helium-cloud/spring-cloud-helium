package org.helium.framework.spring.autoconfigure;


import org.helium.cloud.regsitercenter.configruation.NotifyBean;
import org.helium.framework.BeanContext;
import org.helium.framework.BeanIdentity;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.ServletMappingsNode;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.route.StaticServerRouter;
import org.helium.framework.servlet.ServletContext;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.spi.ServletReference;

import java.util.Collections;

/**
 * 类描述：BeanAsRouter
 *
 * @author zkailiang
 * @date 2020/4/1
 */
public class BeanAsRouter {

	public static ServletContext getImAsRouter(NotifyBean notifyBean) {
		ServletMappingsNode node = new ServletMappingsNode();
		node.setProtocol("sip");
		node.setInnerXml(notifyBean.getIfc());

		BeanConfiguration config = new BeanConfiguration();
		config.setId(notifyBean.getId());
		config.setType("SERVLET");
		config.setServletMappings(node);

		ServerUrl serverUrl = new ServerUrl(notifyBean.getUrl(), Collections.EMPTY_LIST);
		serverUrl.setProtocol("sip");

		StaticServerRouter2 router = new StaticServerRouter2();
		router.addServer(serverUrl);

		ServletReference servletContext = new ServletReference(config);
		router.setBeanContext(servletContext);

		servletContext.setRouter(router);
		servletContext.resolve();

		return servletContext;
	}

	static class StaticServerRouter2 extends StaticServerRouter {
		private BeanContext beanContext;

		public void setBeanContext(BeanContext beanContext) {
			this.beanContext = beanContext;
		}

		@Override
		public BeanContext getBeanContext() {
			return beanContext;
		}

	}

	public static void syncBean(NotifyBean notifyBean) {
		Bootstrap instance = Bootstrap.INSTANCE;
		switch (notifyBean.getStat()) {
			case CREATE:
				instance.addBean(getImAsRouter(notifyBean));
				break;
			case UPDATE:
				instance.putBean(getImAsRouter(notifyBean));
				break;
			case DELETE:
				instance.removeBean(BeanIdentity.parseFrom(notifyBean.getId()));
				break;
			default:
				break;
		}
	}
}
