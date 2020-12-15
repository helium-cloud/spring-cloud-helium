package org.helium.framework.spi;

import org.helium.framework.BeanType;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;

/**
 * Created by Coral on 7/28/15.
 */
public class BeanContextFactory {

	public static BeanInstance createInstance(Class<?> beanType, BeanContextProvider cp) {
		BeanConfiguration conf = AnnotationResolver.resolveInstance(beanType, cp);
		return createInstance(conf, cp);
	}
//
//	public static BeanInstance createServiceInstance(Object service, BeanContextProvider cp) {
//		BeanConfiguration conf = AnnotationResolver.resolveInstance(service.getClass(), cp);
//		return new ServiceInstance(conf, service);
//	}

	public static BeanInstance createInstance(BeanConfiguration conf, BeanContextProvider cp) {
		switch (BeanType.fromText(conf.getType())) {
			case SERVICE:
				return new ServiceInstance(conf, cp);
			case MODULE:
				return new ModuleInstance(conf, cp);
			case SERVLET:
				return new ServletInstance(conf, cp);
			case CONFIGURATOR:
				return new ConfiguratorInstance(conf, cp);
			default:
				throw new IllegalArgumentException("Unknown beanType:" + conf.getType());
		}
	}

	public static BeanReference createReference(BeanConfiguration conf) {
		return createReference(conf, null);
	}

	public static BeanReference createReference(BeanConfiguration conf, BeanContextProvider cp) {
		switch (BeanType.fromText(conf.getType())) {
			case SERVLET:
				return new ServletReference(conf, cp);
			case SERVICE:
				return new ServiceReference(conf, cp);
			default:
				throw new IllegalArgumentException("Unsupported ServletType:" + conf.getType());
		}
	}
}
