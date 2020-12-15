package org.helium.framework.spring.assembly;

import org.helium.framework.BeanContext;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.spring.autoconfigure.HeliumBeanRegistrar;
import org.helium.framework.type.LoadState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 启动器
 * Created by Coral on 5/5/15.
 */
public class HeliumAssembly {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumAssembly.class);

	public static final HeliumAssembly INSTANCE = new HeliumAssembly();

	private LoadState loadState = LoadState.START;


	private HeliumAssembly() {
	}

	/**
	 * 增加配置路径
	 *
	 * @param path
	 */
	public void addPath(String path) {
		Bootstrap.INSTANCE.addPath(path);
	}


	public void run(HeliumConfig heliumConfig, boolean exitOnError) {
		try {
			loadState = LoadState.INIT;
			//配置类启动
			Bootstrap.INSTANCE.initialize(heliumConfig, exitOnError, false, false);

			loadState = LoadState.COMPLETE;

			HeliumBeanRegistrar.registerBean(Bootstrap.INSTANCE.getBeans());
		} catch (Exception e) {
			LOGGER.error("run exception:", e);
		}
	}

	public void run(String path, boolean exitOnError) {
		try {
			loadState = LoadState.INIT;
			//配置文件
			Bootstrap.INSTANCE.initialize(path, exitOnError, false);
			loadState = LoadState.COMPLETE;

			HeliumBeanRegistrar.registerBean(Bootstrap.INSTANCE.getBeans());
		} catch (Exception e) {
			LOGGER.error("run exception:", e);
		}
	}

	public BeanContext getBean(String id) {
		return Bootstrap.INSTANCE.getBean(id);
	}


	public List<BeanContext> getBeans() {
		return Bootstrap.INSTANCE.getBeans();
	}

	public boolean isStarted() {
		if (loadState == LoadState.START) {
			return false;
		}
		return true;
	}

	public boolean isComplete() {
		if (loadState == LoadState.COMPLETE) {
			return true;
		}
		return false;
	}
}
