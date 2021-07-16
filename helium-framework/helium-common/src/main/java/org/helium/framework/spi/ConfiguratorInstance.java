package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.Configurator;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.ConfiguratorConfiguration;

/**
 * Created by Coral on 7/31/15.
 */
public class ConfiguratorInstance extends BeanInstance {
	private Configurator instance;
	/**
	 * 构造函数
	 *
	 * @param configuration
	 * @param cp
	 */
	public ConfiguratorInstance(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
	}

	@Override
	protected void doResolve() {
		Configurator configurator = (Configurator)this.getBean();
		instance = configurator.getInstance();

		ConfigProvider configProvider = BeanContext.getContextService().getService(ConfigProvider.class);
		ConfiguratorConfiguration cc = configProvider.loadJson(getConfiguration().getPath(), ConfiguratorConfiguration.class);
		SetterInjector.injectSetters(instance, cc.getSetters(), false);
	}

	@Override
	protected void doStart() {
		instance.reloadValues();
	}

	@Override
	protected void doStop() {

	}
}
