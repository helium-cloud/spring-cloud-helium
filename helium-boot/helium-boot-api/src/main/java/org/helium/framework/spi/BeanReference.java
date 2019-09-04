package org.helium.framework.spi;

import org.helium.framework.*;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.route.BeanContextRemote;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.utils.StateController;

/**
 * BeanReference抽象类，
 * 子类：ServiceReference, ServletReference
 *
 * Created by Coral on 7/20/15.
 */
public abstract class BeanReference extends AbstractBeanContext implements BeanContextRemote {
	private StateController<BeanContextState> stateController;

	public BeanReference(BeanConfiguration configuration, BeanContextProvider cp) {
		super(configuration, cp);
		String marker = "BeanReference:" + configuration.getId();
		stateController = new StateController<>(marker, BeanContextState.INITIAL);

		stateController.doAction(BeanContextAction.RESOLVE, () -> {
			resolve();
		});
	}

	protected abstract void resolve();


	public boolean register(BeanContextService contextService) {
		return stateController.doAction(BeanContextAction.REGISTER, () -> {
			contextService.addBean(this);
		});
	}

	@Override
	public BeanContextState getState() {
		return stateController.getState();
	}

	@Override
	public Throwable getLastError() {
		return stateController.getLastError();
	}

}

