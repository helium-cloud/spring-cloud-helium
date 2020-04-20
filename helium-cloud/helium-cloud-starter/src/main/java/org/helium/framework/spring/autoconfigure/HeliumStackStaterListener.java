package org.helium.framework.spring.autoconfigure;

import org.helium.framework.route.ServerUrl;
import org.helium.framework.spi.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * 类描述：HeliumStackStaterListener
 *
 * @author zkailiang
 * @date 2020/4/17
 */
public class HeliumStackStaterListener implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumStackStaterListener.class);

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		Bootstrap bootstrap = Bootstrap.INSTANCE;

		if (bootstrap.isLoaded()) {
			LOGGER.info(">>> =================== Helium Stacks Start ======================= <<<");
			bootstrap.getBundleManager().startBundles();

			LOGGER.warn(">>> ================= BOOTSTRAP Start Finished ================= <<<");
			for (ServerUrl url : bootstrap.getStackManager().getServerUrls()) {
				LOGGER.warn(">>> listening: {}", url.getUrl());
			}
		}
	}
}
