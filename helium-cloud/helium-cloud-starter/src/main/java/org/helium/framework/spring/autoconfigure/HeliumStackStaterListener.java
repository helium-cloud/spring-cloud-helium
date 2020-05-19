package org.helium.framework.spring.autoconfigure;

import org.helium.cloud.regsitercenter.configruation.HeliumRegister;
import org.helium.framework.BeanContext;
import org.helium.framework.entitys.BeanConfiguration;
import org.helium.framework.entitys.ServletMappingsNode;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.spi.Bootstrap;
import org.helium.framework.spi.ServletInstance;
import org.helium.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 类描述：HeliumStackStaterListener
 *
 * @author zkailiang
 * @date 2020/4/17
 */
public class HeliumStackStaterListener implements ApplicationListener<ApplicationStartedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumStackStaterListener.class);

	@Autowired
	private HeliumRegister heliumRegister;

	@Value("${PRIVATE_ID:0:0:0:0}")
	private String host;

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		Bootstrap bootstrap = Bootstrap.INSTANCE;

		if (bootstrap.isLoaded()) {
			List<ServerUrl> serverUrls = heliumBeanStater(bootstrap);
			if (serverUrls == null || serverUrls.size() == 0) return;

			register(bootstrap, serverUrls);

			subscribe();
		}
	}

	private List<ServerUrl> heliumBeanStater(Bootstrap bootstrap) {
		LOGGER.warn(">>> =================== Helium Stacks Start ======================= <<<");
		bootstrap.getBundleManager().startBundles();

		List<ServerUrl> serverUrls = bootstrap.getStackManager().getServerUrls();
		LOGGER.warn(">>> ================= BOOTSTRAP Start Finished ================= <<<");
		if (serverUrls == null || serverUrls.size() == 0) {
			return null;
		}
		for (ServerUrl url : serverUrls) {
			LOGGER.warn(">>> listening: {}", url.getUrl());
		}
		return serverUrls;
	}

	private void subscribe() {
		LOGGER.warn(">>> ================= BOOTSTRAP Subscribe ================= <<<");
		heliumRegister.subscribe("tcp://0.0.0.0", BeanAsRouter::syncBean);
	}

	private boolean register(Bootstrap bootstrap, List<ServerUrl> serverUrls) {
		LOGGER.warn(">>> ================= BOOTSTRAP Register ================= <<<");
		List<BeanConfiguration> configurations = bootstrap.getBeans()
				.stream()
				.filter(bean -> bean instanceof ServletInstance)
				.map(BeanContext::getConfiguration)
				.filter(config -> TypeUtils.isTrue(config.getExport()))
				.collect(Collectors.toList());

		List<ServerUrl> urls = serverUrls.stream().filter(url -> "sip".equalsIgnoreCase(url.getProtocol())).collect(Collectors.toList());
		if (urls.size() == 0) {
			return false;
		}
		String path = urls.get(0).getUrl();

		configurations.forEach(cfg -> {
			ServletMappingsNode node = cfg.getServletMappings();
			heliumRegister.registry(path, node.getInnerXml(), cfg.getId());
		});
		return true;
	}
}
