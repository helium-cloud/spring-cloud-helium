package org.helium.framework.spi.bundle;

import com.feinno.superpojo.SuperPojo;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.entitys.BeanNode;
import org.helium.framework.entitys.BundleConfiguration;
import org.helium.framework.entitys.BundleNode;
import org.helium.framework.spi.ObjectCreator;
import org.helium.util.ErrorList;
import org.helium.util.TypeUtils;

import java.io.InputStream;
import java.util.List;

/**
 * Created by Coral on 10/7/15.
 */

public class DefaultAppBundle extends AbstractAppBundle {
	//TODO DefaultAppBundle getName
	@Override
	public String getName() {
		return "name";
	}

	@Override
	public boolean isExport() {
		boolean isExport = false;
		for (BeanContext beanContext : this.getBeans()) {
			if (TypeUtils.isTrue(beanContext.getConfiguration().getParentNode().getExport())) {
				isExport = true;
			}
		}
		return isExport;
	}

	public static BundleConfiguration createDefaultBundleConfiguration(String name) {
		BundleConfiguration configuration = new BundleConfiguration();
		configuration.setName(name);
		BundleNode parentNode = new BundleNode();
		parentNode.setLocation("bootstrap:" + name);
		configuration.setParentNode(parentNode);
		return configuration;
	}

	public static DefaultAppBundle createAppBundle(List<BeanNode> beans, ConfigProvider configProvider) {
		BundleConfiguration configuration = createDefaultBundleConfiguration("<beans/>");
		configuration.setBeans(beans);
		return new DefaultAppBundle(configuration, createContextProvider(configProvider));
	}

	public DefaultAppBundle(BundleConfiguration configuration, BeanContextProvider contextProvider) {
		super(configuration.getParentNode().getLocation(), configuration);
		setContextProvider(contextProvider);
	}

	@Override
	public ErrorList doUninstall() {
		return null;
	}

	public static BeanContextProvider createContextProvider(ConfigProvider configProvider) {
		//
		// - 创建默认的ContextProvider
		return new BeanContextProvider() {
			@Override
			public InputStream loadRaw(String file) { return configProvider.loadRaw(file); }

			@Override
			public boolean hasFile(String file) {
				return configProvider.hasFile(file);
			}

			@Override
			public Class<?> loadClass(String className) {
				return ObjectCreator.loadClass(className);
			}

			@Override
			public <E extends SuperPojo> E loadContentXml(String path, Class<E> clazz) {
				return configProvider.loadXml(path, clazz);
			}
		};
	}
}
