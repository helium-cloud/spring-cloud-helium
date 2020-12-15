package org.helium.framework.route.center;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextModification;
import org.helium.framework.BeanContextService;
import org.helium.framework.BeanType;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.route.abtest.Factor;
import org.helium.framework.route.abtest.FactorFactory;
import org.helium.framework.route.center.entity.GrayBundleNode;
import org.helium.framework.route.center.entity.VersionedBundleNode;
import org.helium.framework.servlet.ServletDescriptor;
import org.helium.framework.servlet.StackManager;
import org.helium.framework.spi.ServiceReference;
import org.helium.framework.spi.bundle.DefaultAppBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 组合从Zk同步下来的结点，包含VersionedBundleNode, BundleEndpoint, 以及GrayBundle
 * 并完成到contextService的地址改写
 * Created by Coral on 8/6/15.
 */
public class BundleReference {
	private static final Logger LOGGER = LoggerFactory.getLogger(BundleReference.class);

	private String bundleName;
	private String bundleVersion;
	private String defaultVersion = "0";
	private BeanContextService contextService;
	private Map<String, ServletReferenceCombo> servlets;
	private Map<String, ServiceReferenceCombo> services;
	private BeanContextProvider contextProvider;

	public String getBundleName() {
		return bundleName;
	}

	public BundleReference(String name, BeanContextService contextService) {
		this.bundleName = name;
		this.servlets = new HashMap<>();
		this.services = new HashMap<>();
		this.contextService = contextService;
		ConfigProvider configProvider = contextService.getService(ConfigProvider.class);
		contextProvider = DefaultAppBundle.createContextProvider(configProvider);
	}

	/**
	 * version是个唯一的id
	 * @param bundle
	 */
	public void addVersion(VersionedBundleNode bundle) {
		LOGGER.info("addVersion: {}#{}", bundle.getBundleName(), bundle.getBundleVersion());
		bundleVersion = bundle.getBundleVersion();
		StackManager servletService  = BeanContext.getContextService().getService(StackManager.class);
		for (BeanConfiguration bc: bundle.getBeans()) {
			switch (BeanType.fromText(bc.getType())) {
				case SERVLET:
					ServletDescriptor sd = servletService.getDescriptor(bc.getServletMappings().getProtocol());
					if (sd == null) {
						LOGGER.debug("addVersion Not Require: {}", bc.getId());
						continue;
					}
					ServletReferenceCombo ref = servlets.get(bc.getId());
					if (ref == null) {
						ref = new ServletReferenceCombo(bc, contextProvider);
						servlets.put(bc.getId(), ref);
						putBeanContext(ref);
					}
					ref.addPrimary(bundleVersion, bc, bundleName);
					break;
				case SERVICE:
					ServiceReferenceCombo sr = services.get(bc.getId());
					if (sr == null) {
						sr = new ServiceReferenceCombo(bc, contextProvider);
						sr.setRouter(contextService.subscribeServerRouter(sr, bundleName, "rpc"));
						services.put(bc.getId(), sr);
						putBeanContext(sr);
					}
					sr.addVersion(bundleVersion);
					sr.addPrimary(bundleVersion, bc, bundleName, bundle);
					break;

			}
		}
	}


	/**
	 * add node in /ROOT/GrayBundles/
	 * @param bundle
	 */
	public void addGrayBundle(GrayBundleNode bundle) {
		String version = bundle.getBundleVersion();
		Factor factor = FactorFactory.createFrom(bundle.getGrayFactors());
		StackManager servletService  = BeanContext.getContextService().getService(StackManager.class);
		for (BeanConfiguration bc: bundle.getBeans()) {
			BeanType beanType = BeanType.fromText(bc.getType());
			if (beanType == BeanType.SERVLET) {
				ServletDescriptor sd = servletService.getDescriptor(bc.getServletMappings().getProtocol());
				if (sd == null) {
					LOGGER.debug("addVersion Not Require: {}", bc.getId());
					continue;
				}
				ServletReferenceCombo ref = servlets.get(bc.getId());
				if (ref == null) {
					ref = new ServletReferenceCombo(bc, contextProvider);
					servlets.put(bc.getId(), ref);
					putBeanContext(ref);
				}
				ref.addExperiment(version, bc, factor, bundle.getServerEndpoint());
				contextService.notify(ref, BeanContextModification.Action.UPDATE);
			} else if (beanType == BeanType.SERVICE){
				ServiceReferenceCombo sr = services.get(bc.getId());
				if (sr == null) {
					//灰度处理
					sr = new ServiceReferenceCombo(bc, contextProvider);
					sr.setRouter(contextService.subscribeServerRouter(sr, bundleName, "rpc"));
					services.put(bc.getId(), sr);
					putBeanContext(sr);
				}
				sr.addExperiment(version, bc, factor, bundle.getServerEndpoint());
				contextService.notify(sr, BeanContextModification.Action.UPDATE);
			}

		}
	}

	/**
	 * 移除一个版本
	 * @param bundle
	 */
	public void removeVersion(VersionedBundleNode bundle) {
		String version = bundle.getBundleVersion();
		for (BeanConfiguration bc: bundle.getBeans()) {
			switch (BeanType.fromText(bc.getType())) {
				case SERVLET:
					ServletReferenceCombo refServlet = servlets.get(bc.getId());
					if (refServlet != null) {
						refServlet.removePrimary(version);
						if (refServlet.isEmpty()) {
							servlets.remove(refServlet.getId());
							removeBeanContext(refServlet);
						}

					}
					break;
				case SERVICE:
					ServiceReferenceCombo refService = services.get(bc.getId());
					if (refService != null) {
						refService.removePrimary(version);
						if (refService.isEmpty()){
							services.remove(refService.getId());
						}

					}
					break;
			}

		}
	}


	public void removeGrayBundle(GrayBundleNode bundle) {
		String version = bundle.getBundleVersion();
		for (BeanConfiguration bc: bundle.getBeans()) {

			switch (BeanType.fromText(bc.getType())) {
				case SERVLET:
					//删除无节点容器
					ServletReferenceCombo refServlet = servlets.get(bc.getId());
					if (refServlet != null) {
						refServlet.removeExperiment(version, bundle.getServerEndpoint());
						if (refServlet.isEmpty()){
							servlets.remove(refServlet.getId());
							contextService.removeBean(refServlet.getId());
						}
						contextService.notify(refServlet, BeanContextModification.Action.UPDATE);
					}
					break;
				case SERVICE:
					ServiceReferenceCombo refService = services.get(bc.getId());
					if (refService != null) {
						refService.removeExperiment(version, bundle.getServerEndpoint());
						//删除无节点服务
						if (refService.isEmpty()){
							services.remove(refService.getId());
							contextService.removeBean(refService.getId());
						}
						contextService.notify(refService, BeanContextModification.Action.UPDATE);
					}
					break;
			}

		}
	}



	private void putBeanContext(BeanContext ref) {
		ref.putAttachment("bundle", bundleName + "#" + bundleVersion);

		BeanContext old = contextService.getBean(ref.getId());
		if (old == null) {
			contextService.putBean(ref);
		} else {
			if ((old instanceof ServiceReference)) {
				contextService.putBean(ref);
			} else if ((old instanceof ServiceReference)){
				contextService.putBean(ref);
			}
			if (old.getAttachment("__LOCKED") != null) {;
				contextService.putBean(ref);
			}
		}
	}

	private void removeBeanContext(BeanContext ref) {
		BeanContext old = contextService.getBean(ref.getId());
		if (old != null) {
			if (old.getAttachment("__LOCKED") != null) {;
				contextService.removeBean(ref.getId());
			}
		}
	}
}
