package org.helium.framework.spi;


import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;

/**
 * Created by Coral on 7/28/15.
 */
public class ServiceInstance extends BeanInstance {
	private String serviceName;
	private Class<?> serviceInterface;

	/**
	 * 构造函数
	 *
	 * @param configuration
	 * @param cp
	 */
	public ServiceInstance(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
		serviceName = ServiceReference.getRpcServiceName(getId());
		if (configuration.getInterfaceClazz() != null) {
			serviceInterface = configuration.getInterfaceClazz();
		} else {
			if (cp != null) {
				serviceInterface = cp.loadClass(configuration.getInterfaceType());
			} else {
				serviceInterface = ObjectCreator.loadClass(configuration.getInterfaceType());
			}
		}
	}

	public ServiceInstance(BeanConfiguration configuration, Object service, BeanContextProvider cp) {
		super(configuration, cp);
		serviceName = ServiceReference.getRpcServiceName(getId());
		if (configuration.getInterfaceClazz() != null) {
			serviceInterface = configuration.getInterfaceClazz();
		} else {
			if (cp != null) {
				serviceInterface = cp.loadClass(configuration.getInterfaceType());
			} else {
				serviceInterface = ObjectCreator.loadClass(configuration.getInterfaceType());
			}
		}
	}


	@Override
	protected void doResolve() {
		//Nothing to do
	}

	@Override
	protected void doStart() {
	}

	@Override
	protected void doStop() {

	}
}
