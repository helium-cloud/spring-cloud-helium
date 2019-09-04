package org.helium.framework.spi.bundle;

import com.feinno.superpojo.SuperPojo;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.configuration.ConfigProvider;
import org.helium.framework.configuration.Environments;
import org.helium.framework.entitys.BundleConfiguration;
import org.helium.framework.entitys.BundleNode;
import org.helium.framework.spi.ObjectCreator;
import org.helium.framework.utils.ClassPathHack;
import org.helium.util.ErrorList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by Coral on 10/5/15.
 */
public class JarAppBundle extends AbstractAppBundle implements BeanContextProvider {
	public static final String CONFIG_PATH = "META-INF/";
	public static final String CONFIG_FILE_BUNDLE_XML =  "bundle.xml";

	private JarFile jar;
	private ConfigProvider configProvider;

	public static JarAppBundle createAppBundle(BundleNode node, ConfigProvider configProvider) {
		String path = node.getPath(); // applyJarPattern(node.getPath());
		try {
			String location = "jar:" + path;
			JarFile jar = new JarFile(path);
			ClassPathHack.addFile(path);

			BundleConfiguration config = loadXmlFromJar(jar, CONFIG_FILE_BUNDLE_XML, BundleConfiguration.class, null);
			config.setParentNode(node);
			JarAppBundle bundle = new JarAppBundle(location, jar, config, configProvider);
			return bundle;
		} catch (IOException ex) {
			throw new IllegalArgumentException("can't load jar:" + path, ex);
		}
	}

	public JarAppBundle(String location, JarFile jar, BundleConfiguration configuration, ConfigProvider configProvider) {
		super(location, configuration);

		this.jar = jar;
		this.configProvider = configProvider;

		//
		// 第一遍LoadBundleConfiguration不处理ConfigImports值，这里需要加载<configImports>节点后，重新load一遍xml
		// TODO: 这块的处理非常不舒服，在下个版本需要需要重构一下
		importDefaultVars();
		BundleConfiguration c = loadXmlFromJar(jar, CONFIG_FILE_BUNDLE_XML, BundleConfiguration.class, this);
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
		return loadXmlFromJar(jar, path, clazz, this);
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

	private static <E extends SuperPojo> E loadXmlFromJar(JarFile jar, String path, Class<E> clazz, ConfigProvider configProvider) {
		try {
			JarEntry e = jar.getJarEntry(CONFIG_PATH + path);
			if (e == null) {
				throw new IllegalArgumentException("file not found from jar: " + path);
			}
			InputStream in = jar.getInputStream(e);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line;
			StringBuilder str = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				str.append(line);
				str.append("\r\n");
			}
			E result = clazz.newInstance();
			//
			// String xml = Environments.applyConfigText(path, str.toString());
			String xml = str.toString();
			if (configProvider != null) {
				xml = configProvider.applyConfigText(path, xml);
			}
			result.parseXmlFrom(xml);
			return result;
		} catch (Exception ex) {
			throw new IllegalArgumentException("load bundle.xml from jar failed: path=" + path, ex);
		}
	}


	public static void main(String[] args) {
		String classPath = System.getProperty("java.class.path");
		HashSet<String> paths = new HashSet<>();
		for (String path: classPath.split(":")) {
			paths.add(path);
			System.out.println(":" + path);
		}
		for (String path : paths) {
			System.out.println(">" + path);
		}

		// System.out.println(applyJarPattern("helium-dashboard/build/libs/helium-dashboard~.jar"));
	}
}
