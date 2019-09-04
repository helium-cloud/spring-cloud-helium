package org.helium.framework.spi;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanIdentity;
import org.helium.framework.BeanType;
import org.helium.framework.configuration.BeanContextProvider;
import org.helium.framework.entitys.BeanConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Coral on 7/25/15.
 */
public abstract class AbstractBeanContext implements BeanContext {
	private BeanType type;
	private BeanIdentity id;
	private BeanContextProvider contextProvider;
	private BeanConfiguration configuration;
	private Object attachmentLock = new Object();
	private Map<String, Object> attachments = new HashMap<>();

	public AbstractBeanContext(BeanConfiguration configuration, BeanContextProvider contextProvider) {
		this.configuration = configuration;
		this.contextProvider = contextProvider;
		this.id = BeanIdentity.parseFrom(configuration.getId());
		this.type = BeanType.fromText(configuration.getType());
	}

	@Override
	public BeanIdentity getId() {
		return id;
	}

	@Override
	public BeanType getType() {
		return type;
	}

	@Override
	public BeanConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public Object putAttachment(String key, Object value) {
		synchronized (attachmentLock) {
			return attachments.put(key, value);
		}
	}

	@Override
	public Object getAttachment(String key) {
		synchronized (attachmentLock) {
			return attachments.get(key);
		}
	}

	protected BeanContextProvider getContextProvider() {
		return contextProvider;
	}

	public void setContextProvider(BeanContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setConfiguration(BeanConfiguration configuration) {
		this.configuration = configuration;
	}
}
