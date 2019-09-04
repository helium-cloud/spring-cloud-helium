package org.helium.framework.spi.bundle;

import com.feinno.superpojo.util.StringUtils;
import org.helium.framework.BeanContext;
import org.helium.framework.BeanContextService;
import org.helium.framework.bundle.AppBundleHandler;
import org.helium.framework.bundle.BundleState;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.Environments;
import org.helium.framework.entitys.*;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.route.StaticServerRouter;
import org.helium.framework.route.center.ServiceReferenceCombo;
import org.helium.framework.spi.*;
import org.helium.framework.utils.StateController;
import org.helium.util.CollectionUtils;
import org.helium.util.ErrorList;
import org.helium.util.TypeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 引用AppBundle
 * Created by Coral on 10/7/15.
 */
public class ReferenceAppBundle implements AppBundleHandler {
	private String location;
	private BundleConfiguration configuration;
	private StateController<BundleState> stateController;
	private BeanContextProvider contextProvider;
	private List<BeanReference> references;
	private ErrorList lastErrors;

	public static ReferenceAppBundle createDefault(List<BeanReferenceNode> referenceNodes, ConfigProvider configProvider) {
		BundleConfiguration configuration = DefaultAppBundle.createDefaultBundleConfiguration("<references/>");
		configuration.setReferences(referenceNodes);
		return new ReferenceAppBundle(configuration, DefaultAppBundle.createContextProvider(configProvider));
	}

	public static ReferenceAppBundle createFromXml(BundleNode bundleNode, ConfigProvider configProvider) {
		BundleConfiguration configuration = configProvider.loadXml(bundleNode.getPath(), BundleConfiguration.class);
		bundleNode.setLocation("xml:" + bundleNode.getPath());
		configuration.setParentNode(bundleNode);

		for (BeanNode beanNode: configuration.getBeans()) {
			if (!TypeUtils.isTrue(beanNode.getExport())) {
				continue;
			}
			if (TypeUtils.isFalse(beanNode.getEnabled())) {
				continue;
			}

			BeanReferenceNode refNode = new BeanReferenceNode();
			refNode.setClazz(beanNode.getClazz());
			refNode.setPath(beanNode.getPath());
			configuration.getReferences().add(refNode);
		}
		return new ReferenceAppBundle(configuration, DefaultAppBundle.createContextProvider(configProvider));
	}

	private ReferenceAppBundle(BundleConfiguration configuration, BeanContextProvider contextProvider) {
		this.location = configuration.getParentNode().getLocation();
		this.configuration = configuration;
		this.stateController = new StateController<>(this.location, BundleState.INSTALLED);
		this.references = new ArrayList<>();
		this.contextProvider = contextProvider;
	}

	@Override
	public String getLocation() {
		return location;
	}

	@Override
	public String getName() {
		return configuration.getName();
	}

	@Override
	public String getVersion() {
		return Environments.RUNTIME_VERSION;
	}

	@Override
	public BundleConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public boolean isExport() {
		return false;
	}

	@Override
	public StateController<BundleState> getStateController() {
		return stateController;
	}

	@Override
	public List<BeanContext> getBeans() {
		return CollectionUtils.filter(references, b -> b);
	}

	@Override
	public ErrorList getLastErrors() {
		return lastErrors;
	}

	@Override
	public ErrorList doResolve() {
		ErrorList errors = new ErrorList();

		for (BeanReferenceNode refNode: configuration.getReferences()) {
			String label = "";
			BeanConfiguration config;
			try {
				if (!StringUtils.isNullOrEmpty(refNode.getInterfaceClazz())) {
					label = "interface:" + refNode.getInterfaceClazz();
					Class<?> clazz = contextProvider.loadClass(refNode.getInterfaceClazz());
					config = AnnotationResolver.resolveReference(clazz, false);

					if (!StringUtils.isNullOrEmpty(refNode.getId())) {
						config.setId(refNode.getId());
					}

					if (StringUtils.isNullOrEmpty(config.getId())) {
						throw new IllegalArgumentException("bad <reference/> node must have bean-id");
					}
				} else if (!StringUtils.isNullOrEmpty(refNode.getPath())) {
					label = "path:" + refNode.getPath();
					config = contextProvider.loadContentXml(refNode.getPath(), BeanConfiguration.class);
				} else {
					throw new IllegalArgumentException("bad <reference/> node:" + refNode.toString());
				}
				label = "id:" + config.getId();
				BeanReference reference = BeanContextFactory.createReference(config);

				//
				// 增加静态地址
				StaticServerRouter router = new StaticServerRouter();
				for (ServerUrl url : getReferenceServers(refNode)) {
					if (reference instanceof ServiceReference
							|| reference instanceof ServiceReferenceCombo
							|| reference instanceof TaskReference) {
						if (url.getProtocol().equals("rpc")) {
							router.addServer(url);
						}
					} else if (reference instanceof ServletReference) {
						String protocol = ((ServletReference) reference).getProtocol();
						if (protocol.equals(url.getProtocol())) {
							router.addServer(url);
						}
					}
				}
				if (reference instanceof ServiceReference) {
					((ServiceReference) reference).setRouter(router);
				} if (reference instanceof ServiceReferenceCombo) {
					((ServiceReferenceCombo) reference).setRouter(router);
				} else if (reference instanceof TaskReference) {
					((TaskReference) reference).setRouter(router);
				} else if (reference instanceof ServletReference) {
					((ServletReference) reference).setRouter(router);
				}

				reference.putAttachment(refNode);
				references.add(reference);
			} catch (Exception ex) {
				errors.addError(label, ex);
			}
		}
		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doRegister(BeanContextService contextService) {
		ErrorList errors = new ErrorList();
		for (BeanReference bean: references) {
			if (contextService.getBean(bean.getId()) != null) {
				continue;
			}
			if (!bean.register(contextService)) {
				errors.addError(bean.getId().toString(), bean.getLastError());
			}
		}
		return errors.hasError() ? errors : null;
	}

	@Override
	public ErrorList doAssemble(BeanContextService contextService) {
		return null;
	}

	@Override
	public ErrorList doUpdate(BeanContextService contextService) {
		return null;
	}

	@Override
	public ErrorList doStart() {
		return null;
	}

	@Override
	public ErrorList doStop() {
		return null;
	}

	@Override
	public ErrorList doUninstall() {
		return null;
	}

//	public ServerRouter getRouter(BeanContext bc) {
//		//
//		// 增加静态地址
//		for (ServerUrl url: getReferenceServers(refNode)) {
//			// TODO
////					if (reference instanceof ServiceReference || reference instanceof TaskReference) {
////						if (url.getProtocol().equals("rpc")) {
////							reference.getRouter().addServer(url);
////						}
////					} else if (reference instanceof ServletReference) {
////						String protocol = ((ServletReference)reference).getProtocol();
////						if (protocol.equals(url.getProtocol())) {
////							reference.getRouter().addServer(url);
////						}
////					}
//		};
//
//	}

	public static List<ServerUrl> getReferenceServers(BeanReferenceNode node) {
		List<ServerUrl> urls = new ArrayList<>();
		for (String ep : node.getEndpoints()) {
			ServerUrl url = ServerUrl.parse(ep);
			urls.add(url);
		}
		return urls;
	}
}
