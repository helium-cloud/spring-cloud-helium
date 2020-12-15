package org.helium.framework.service;

import org.helium.framework.BeanIdentity;
import org.helium.framework.route.ServerUrl;

/**
 *
 * bean mapping过滤
 */
public class ServiceMatchResult<T>{

	public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;

	/**
	 * 匹配到优先级
	 */
	private int priority;

	/**
	 *
	 * 服务代理
	 */
	private T proxy;

	/**
	 * 是否是全量灰度
	 */
	private boolean isAll;

	/**
	 * 远程service地址
	 */
	private ServerUrl serverUrl;

	/**
	 * 接口名称
	 */
	private String serviceName;

	/**
	 * 接口类
	 */
	private Class interfaceClass;

	/**
	 * 是否复制
	 *
	 */
	private boolean duplicate;


	private BeanIdentity beanIdentity;


	public ServiceMatchResult() {
	}


	/**
	 * 值越低，优先级越高
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public T getProxy() {
		return proxy;
	}

	public void setProxy(T proxy) {
		this.proxy = proxy;
	}



	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public Class getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public boolean isDuplicate() {
		return duplicate;
	}

	public void setDuplicate(boolean duplicate) {
		this.duplicate = duplicate;
	}

	public boolean isAll() {
		return isAll;
	}

	public void setAll(boolean all) {
		isAll = all;
	}

	public ServerUrl getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(ServerUrl serverUrl) {
		this.serverUrl = serverUrl;
	}

	public BeanIdentity getBeanIdentity() {
		return beanIdentity;
	}

	public void setBeanIdentity(BeanIdentity beanIdentity) {
		this.beanIdentity = beanIdentity;
	}

	@Override
	public String toString() {
		return "{" +
				"priority=" + priority +
				", proxy=" + proxy +
				", isAll=" + isAll +
				", serverUrl=" + serverUrl +
				", serviceName='" + serviceName + '\'' +
				", interfaceClass=" + interfaceClass +
				", duplicate=" + duplicate +
				'}';
	}

}