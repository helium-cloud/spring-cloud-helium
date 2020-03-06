package org.helium.framework.spi.bundle;

import com.feinno.superpojo.SuperPojo;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.Environments;
import org.helium.framework.entitys.BundleConfiguration;
import org.helium.framework.entitys.BundleNode;
import org.helium.framework.spi.ObjectCreator;
import org.helium.util.ErrorList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * Created by Coral on 10/5/15.
 */
public class XmlAppBundle extends AbstractAppBundle implements BeanContextProvider {
	private static final Logger LOGGER = LoggerFactory.getLogger(XmlAppBundle.class);
	private ConfigProvider configProvider;

	public static XmlAppBundle createAppBundle(BundleNode node, ConfigProvider configProvider) {
		BundleConfiguration config = configProvider.loadXml(node.getPath(), BundleConfiguration.class, true);
		config.setParentNode(node);
		String location = "xml:" + node.getPath();
		return new XmlAppBundle(location, config, configProvider);
	}

	private XmlAppBundle(String location, BundleConfiguration configuration, ConfigProvider configProvider) {
		super(location, configuration);
		this.configProvider = configProvider;

		importDefaultVars();
		BundleConfiguration c = this.loadXml(configuration.getParentNode().getPath(), BundleConfiguration.class);
		c.setParentNode(configuration.getParentNode());

		this.setConfiguration(c);
		this.setContextProvider(this);
	}

	@Override
	public ErrorList doUninstall() {
		return null;
	}

	@Override
	public Class<?> loadClass(String className) {
		return ObjectCreator.loadClass(className);
	}

	@Override
	public <E extends SuperPojo> E loadContentXml(String path, Class<E> clazz) {
		return configProvider.loadXml(path, clazz);
	}

	@Override
	public InputStream loadRaw(String file) {
		return configProvider.loadRaw(file);
	}

	@Override
	public boolean hasFile(String file) {
		return configProvider.hasFile(file);
	}

	@Override
	public String applyConfigText(String path, String text) {
		text = applyDefaultVars(text);
		return Environments.applyConfigText(path, text);
	}
}
