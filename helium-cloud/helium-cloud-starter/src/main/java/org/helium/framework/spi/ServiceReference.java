package org.helium.framework.spi;

import org.helium.framework.BeanIdentity;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.route.ServerRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Coral on 8/22/15.
 */
public class ServiceReference extends BeanReference {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceReference.class);
	private boolean serviceUnknown = false;
	private String serviceName;
	private Object serviceProxy;
	private ServerRouter router;

	public ServiceReference(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
	}

	private Object createServiceProxy() {
//		Class<?> serviceInterface = getConfiguration().getInterfaceClazz();
//		if (serviceInterface == null) {
//			serviceInterface = ObjectCreator.loadClass(getConfiguration().getInterfaceType());
//		}
//		return RpcProxyFactory.getTransparentProxy(serviceName, serviceInterface, () -> {
//			if (router == null){
//				throw new RuntimeException("No Server for bean:" + getId());
//			}
//			BeanEndpoint ep = router.routeBean();
//			if (ep == null) {
//				throw new RuntimeException("No Server for bean:" + getId());
//			}
//			return RpcTcpEndpoint.parse(ep.getServerUrl().getUrl());
//		});
		return null;
	}

	@Override
	public Object getBean() {
		if (serviceProxy == null && !serviceUnknown) {
			synchronized (this) {
				if (serviceProxy == null) {
					try {
						serviceProxy = createServiceProxy();
					} catch (Exception ex) {
						LOGGER.error("unknown reference:" + serviceName, ex);
						serviceUnknown = true;
					}
				}
			}
		}
		return serviceProxy;
	}

	@Override
	protected void resolve() {
		serviceName = getRpcServiceName(getId());
	}

	public static String getRpcServiceName(BeanIdentity id) {
		return id.getGroup() + "." + id.getName();
	}

	public void addVersion(String version) {

	}

	public void removeVersion(String version) {

	}

	@Override
	public ServerRouter getRouter() {
		return router;
	}

	public void setRouter(ServerRouter router) {
		this.router = router;
	}

}
