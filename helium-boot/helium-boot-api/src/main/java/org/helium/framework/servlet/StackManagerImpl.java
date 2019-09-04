package org.helium.framework.servlet;

import org.helium.util.StringUtils;
import org.helium.framework.annotations.ServiceImplementation;
import org.helium.framework.entitys.BootstrapConfiguration;
import org.helium.framework.entitys.ObjectWithSettersNode;
import org.helium.framework.route.ServerUrl;
import org.helium.framework.spi.ObjectCreator;
import org.helium.util.CollectionUtils;
import org.helium.framework.utils.EnvUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Coral on 7/25/15.
 */
@ServiceImplementation
public class StackManagerImpl implements StackManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(StackManagerImpl.class);
	private Map<String, ServletStack> stacks;
	private Map<String, ServletDescriptor> descriptors;

	public StackManagerImpl() {
		stacks = new HashMap<>();
		descriptors = new HashMap<>();
	}

	public ServletStack loadStack(ObjectWithSettersNode node, Function<String, Class> classLoader) {
		LOGGER.info("loadStack:{}", node.getId());
		ServletStack stack = (ServletStack) ObjectCreator.createObject(node);
		stack.setId(node.getId());
		synchronized (this) {
			if (stacks.get(node.getId()) != null) {
				throw new IllegalArgumentException("Duplicated StackId:" + node.getId());
			}
			stacks.put(node.getId(), stack);
			LOGGER.info("registerStack id={} class={}", node.getId(), stack.getClass().getName());
		}
		try {
			stack.start();
		} catch (Exception ex) {
			throw new RuntimeException("Stack failed:" + node.getId(), ex);
		}
		putDescriptor(stack.getServletDescriptor());
		return stack;
	}

	public synchronized void putDescriptor(ServletDescriptor descriptor) {
		if (descriptor != null) {
			descriptors.put(descriptor.getProtocol(), descriptor);
		}
	}

	@Override
	public synchronized ServletStack getStack(String id) {
		return stacks.get(id);
	}

	@Override
	public synchronized List<ServletStack> getStacks() {
		return CollectionUtils.cloneValues(stacks);
	}

	@Override
	public synchronized ServletDescriptor getServletDescriptor(Object servlet) {
		for (ServletStack stack: stacks.values()) {
			if (stack.isSupportServlet(servlet)) {
				return stack.getServletDescriptor();
			}
		}
		return null;
	}

	@Override
	public ServletDescriptor getModuleDescriptor(Object module) {
		for (ServletStack stack: stacks.values()) {
			if (stack.isSupportModule(module)) {
				return stack.getServletDescriptor();
			}
		}
		return null;
	}

	@Override
	public synchronized ServletDescriptor getDescriptor(String protocol) {
		return descriptors.get(protocol);
	}

	@Override
	public List<ServerUrl> getServerUrls() {
		List<ServerUrl> serverUrls = new ArrayList<>();
		for (ServletStack stack : stacks.values()) {
			for (ServerUrl url : stack.getServerUrls()) {
				serverUrls.add(url);
			}
		}
		return serverUrls;
	}

	@Override
	public List<ServerUrl> getCenterServerUrls(BootstrapConfiguration configuration) {
		List<ServerUrl> serverUrls = new ArrayList<>();
		for (ServletStack stack : stacks.values()) {

			for (ServerUrl url : stack.getServerUrls()) {
				String regIp = EnvUtils.getEnv(configuration, EnvUtils.REG_IP);
				LOGGER.info("ServerUrl.getUrl():{}-regIp {}",url.getUrl(), regIp );
				if (!StringUtils.isNullOrEmpty(regIp)){
					url.setUrl(url.getUrl().replace(stack.getHost(), regIp));
				}
				serverUrls.add(url);
			}
		}
		return serverUrls;
	}
}
