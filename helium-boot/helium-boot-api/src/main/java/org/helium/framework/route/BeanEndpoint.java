package org.helium.framework.route;

import org.helium.framework.BeanContext;
import org.helium.framework.BeanIdentity;
import org.helium.framework.servlet.ServletMatchResult;

/**
 * 标记一个Bean在集群中的地址
 * Created by Coral on 8/10/15.
 */
public class BeanEndpoint {
	private int priority = ServletMatchResult.DEFAULT_PRIORITY;
	private BeanContext beanContext;
	private ServerUrl serverUrl;

	public BeanIdentity getBeanId() {
		return beanContext.getId();
	}

	public ServerUrl getServerUrl() {
		return serverUrl;
	}

	public int getPriority() {
		return priority;
	}

	public BeanContext getBeanContext() {
		return beanContext;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public BeanEndpoint(BeanContext bc, ServerUrl serverUrl) {
		this.beanContext = bc;
		this.serverUrl = serverUrl;
	}

	@Override
	public String toString() {
		return serverUrl + "/" + beanContext.getId();
	}

}
