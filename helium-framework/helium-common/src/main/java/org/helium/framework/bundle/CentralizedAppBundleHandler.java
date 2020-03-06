package org.helium.framework.bundle;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.BundleConfiguration;
import org.helium.framework.entitys.BundleNode;
import org.helium.framework.entitys.FactorGroupNode;
import org.helium.framework.route.center.CentralizedService;
import org.helium.framework.utils.StateController;
import org.helium.threading.Future;
import org.helium.threading.Timeout;
import org.helium.threading.TimeoutListener;
import org.helium.threading.UpcTimerTask;
import org.helium.util.CollectionUtils;
import org.helium.util.ErrorList;
import org.helium.util.TypeUtils;

import java.util.List;

/**
 * 支持中心化的BundleHandler
 * Created by Coral on 8/30/15.
 */
public class CentralizedAppBundleHandler implements AppBundleHandler {
	public static final int LAZY_STOP_WAIT_SECONDS = 30;

	private AppBundleHandler innerHandler;
	private CentralizedService center;
	private FactorGroupNode factorNode = null;

	public CentralizedAppBundleHandler(AppBundleHandler handler, CentralizedService center) {
		this.innerHandler = handler;
		this.center = center;

		//
		// 判断是否为灰度发布
		BundleNode bundleNode = innerHandler.getConfiguration().getParentNode();
		if (bundleNode != null) {
			this.factorNode = bundleNode.getGrayFactors();
		}
	}

	@Override
	public String getLocation() {
		return innerHandler.getLocation();
	}

	@Override
	public String getName() {
		return innerHandler.getName();
	}

	@Override
	public String getVersion() {
		return innerHandler.getVersion();
	}

	@Override
	public boolean isAppBundle() {
		return innerHandler.isAppBundle();
	}

	@Override
	public BundleConfiguration getConfiguration() {
		return innerHandler.getConfiguration();
	}

	@Override
	public BundleState getState() {
		return innerHandler.getState();
	}

	@Override
	public Throwable getLastError() {
		return innerHandler.getLastError();
	}

	@Override
	public boolean resolve() {
		return innerHandler.resolve();
	}

	@Override
	public boolean isExport() {
		return innerHandler.isExport();
	}

	@Override
	public StateController<BundleState> getStateController() {
		return innerHandler.getStateController();
	}

	@Override
	public List<BeanContext> getBeans() {
		return innerHandler.getBeans();
	}

	@Override
	public boolean start() {
		return getStateController().doAction(BundleAction.START, () -> {
			doStart();
			List<BeanConfiguration> beans = CollectionUtils.filter(getBeans(), bc -> {
				if (TypeUtils.isTrue(bc.getConfiguration().getExport())) {
					return filterBeanConfiguration(bc.getConfiguration());
				} else {
					return null;
				}
			});
			if (isExport()) {
				if (factorNode != null) {
					center.registerGrayBundle(getName(), getVersion(), factorNode, beans);
				} else {
					center.registerBundle(getName(), getVersion(), beans);
				}
			}
		});
	}


	@Override
	public boolean stop() {
		getStateController().doActionAsync(BundleAction.STOP, () -> {
			try {
				if (factorNode != null) {
					center.unregisterGrayBundle(getName(), getVersion());
				} else {
					center.unregisterBundle(getName(), getVersion());
				}
			} catch (Exception e) {
				return Future.createResult(e);
			}
			Future<Exception> future = new Future<Exception>();
			UpcTimerTask timerTask = new UpcTimerTask(LAZY_STOP_WAIT_SECONDS * 1000, new TimeoutListener() {
				@Override
				public void onTimeout(Timeout timeout) {
					try {
						doStop();
						future.complete(null, null);
					} catch (Exception ex) {
						future.complete(null, ex);
					}
				}
			});
			return future;
		});
		return true;
	}

	@Override
	public boolean uninstall() {
		return innerHandler.uninstall();
	}

	@Override
	public ErrorList getLastErrors() {
		return innerHandler.getLastErrors();
	}

	@Override
	public ErrorList doResolve() {
		return innerHandler.doResolve();
	}

	@Override
	public ErrorList doRegister(BeanContextService contextService) {
		return innerHandler.doRegister(contextService);
	}

	@Override
	public ErrorList doStart() {
		return innerHandler.doStart();
	}

	@Override
	public ErrorList doStop() {
		return innerHandler.doStop();
	}

	@Override
	public ErrorList doAssemble(BeanContextService contextService) {
		return innerHandler.doAssemble(contextService);
	}

	@Override
	public ErrorList doUpdate(BeanContextService contextService) {
		return innerHandler.doUpdate(contextService);
	}

	@Override
	public ErrorList doUninstall() {
		return innerHandler.doUninstall();
	}

	/**
	 * 移除BeanConfiguration中不必要的部分，仅保留需要
	 * @param configuration
	 * @return
	 */
	private BeanConfiguration filterBeanConfiguration(BeanConfiguration configuration) {
		return configuration;
	}
}
