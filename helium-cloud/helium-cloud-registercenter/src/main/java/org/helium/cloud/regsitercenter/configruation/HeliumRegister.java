package org.helium.cloud.regsitercenter.configruation;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.registry.NotifyListener;
import org.apache.dubbo.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.dubbo.common.constants.CommonConstants.*;
import static org.apache.dubbo.common.constants.RegistryConstants.DYNAMIC_KEY;
import static org.apache.dubbo.common.constants.RegistryConstants.EMPTY_PROTOCOL;

/**
 * 类描述：HeliumRegister
 *
 * @author zkailiang
 * @date 2020/4/22
 */
@Component
public class HeliumRegister {

	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumRegister.class);
	private static final String IFC = "ifc";
	private RegisterAppConfig registerAppConfig;
	private static Map<String, URL> registryUrls = new ConcurrentHashMap<>();
	private static Map<String, URL> subscribeUrls = new ConcurrentHashMap<>();

	@Resource(name = "heliumRegistry")
	public Registry registry;

	public HeliumRegister(RegisterAppConfig registerAppConfig) {
		this.registerAppConfig = registerAppConfig;
	}


	public void registry(String path, String ifc, String servletId) {
		if (!registerAppConfig.isNeedRegistry()) {
			return;
		}
		//anyhost=true&application=dubbo-registry-zookeeper-provider-sample&deprecated=false&dubbo=2.0.2&dynamic=true&generic=false&interface=org.apache.dubbo.spring.boot.demo.consumer.DemoService&methods=sayHello&pid=13260&release=2.7.6&revision=1.0.0&side=provider&timestamp=1587543554430&version=1.0.
		URL url = URL.valueOf(path)
				.addParameter(SIDE_KEY, PROVIDER_SIDE)
				.addParameter(DYNAMIC_KEY, true)
				.addParameter(GROUP_KEY, "helium")
				.addParameter(IFC, ifc)
				.addParameter("interface", servletId);

		registry.register(url);
		registryUrls.put(key(url), url);
		LOGGER.info("注册方法：key:{},url:{}", key(url), url.toString());
	}

	public void subscribe(String path, HeliumNotifyListener listener) {
		if (!registerAppConfig.isNeedSubscribe()) {
			return;
		}

		URL url = URL.valueOf(path)
				.addParameter(SIDE_KEY, PROVIDER_SIDE)
				.addParameter(DYNAMIC_KEY, true)
				.addParameter(GROUP_KEY, "helium")
				.addParameter("interface", "*");
		registry.subscribe(url, new InnerNotifyListener(listener));
	}

	private static class InnerNotifyListener implements NotifyListener {
		private HeliumNotifyListener listener;


		public InnerNotifyListener(HeliumNotifyListener listener) {
			this.listener = listener;
		}

		@Override
		public void notify(List<URL> urls) {
			urls.forEach(url -> {
				String key = key(url);
				if (registryUrls.containsKey(key)) {
					return;
				}
				LOGGER.info("订阅方法：key:{},url:{}", key, url.toString());
				if (EMPTY_PROTOCOL.equalsIgnoreCase(url.getProtocol())) {
					subscribeUrls.remove(key);
					listener.notify(new NotifyBean().setId(url.getServiceInterface()).setStat(NotifyStat.DELETE));
				} else {
					NotifyStat stat;
					URL have = subscribeUrls.get(key);
					if (have == null) {
						stat = NotifyStat.CREATE;
					} else {
						stat = NotifyStat.UPDATE;
					}
					subscribeUrls.put(key, url);
					listener.notify(new NotifyBean().setId(url.getServiceInterface()).setUrl(url.getProtocol() + PROTOCOL_SEPARATOR + url.getAddress()).setIfc(url.getParameter(IFC)).setStat(stat));
				}
			});
		}

	}

	private static String key(URL url) {
		return url.getParameter(GROUP_KEY) + ":" + url.getServiceInterface();
	}
}
