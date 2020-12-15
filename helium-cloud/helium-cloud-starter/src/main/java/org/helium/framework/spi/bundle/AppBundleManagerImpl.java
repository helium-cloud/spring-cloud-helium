package org.helium.framework.spi.bundle;

import org.helium.framework.BeanContextService;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.bundle.AppBundleHandler;
import org.helium.framework.bundle.BundleHandler;
import org.helium.framework.bundle.BundleManager;
import org.helium.util.CollectionUtils;
import org.helium.util.ErrorList;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Coral on 10/7/15.
 */
@ServiceImplementation
public class AppBundleManagerImpl implements BundleManager {
	private List<AppBundleHandler> bundles = new ArrayList<>();

	@Override
	public List<BundleHandler> getBundles() {
		return CollectionUtils.filter(bundles, b -> b);
	}

	@Override
	public BundleHandler getBundle(String location) {
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public BundleHandler installBundle(String location) throws Exception {
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public void addBundle(BundleHandler bundle) {
		bundles.add((AppBundleHandler)bundle);
	}

	@Override
	public void uninstallBundle(String location) throws Exception {
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public Class<?> findClass(String className, boolean allowAmbiguous) {
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public List<Class<?>> findAllClass(String className) {
		throw new UnsupportedOperationException("NotImplemented");
	}

	@Override
	public ErrorList startBundles() {
		return forEach(bundle -> bundle.start());
	}

	@Override
	public ErrorList assembleBundles(BeanContextService contextService) {
		return forEach(bundle -> bundle.assemble(contextService));
	}

	@Override
	public ErrorList updateBundles(BeanContextService contextService) {
		return forEach(bundle -> bundle.update(contextService));
	}

	@Override
	public ErrorList registerBundles(BeanContextService contextService) {
		return forEach(bundle -> bundle.register(contextService));
	}

	@Override
	public ErrorList stopBundles() {
		return forEach(bundle -> bundle.stop());
	}

	@Override
	public ErrorList resolveBundles() {
		return forEach(bundle -> bundle.resolve());
	}

	public ErrorList forEach(Function<AppBundleHandler, Boolean> func) {
		ErrorList errors = new ErrorList();
		for (AppBundleHandler bundle: bundles) {
			if (!func.apply(bundle)) {
				errors.addError(bundle.getLocation(), bundle.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}
}
