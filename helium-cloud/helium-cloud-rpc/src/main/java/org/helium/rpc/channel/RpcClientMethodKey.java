/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 7, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;


/**
 * rpc客户端访问缓存key
 * <p>
 * Created by Coral
 */
class RpcClientMethodKey {
	private RpcEndpoint endpoint;
	private String service;
	private String method;

	public RpcClientMethodKey(RpcEndpoint ep, String service, String method) {
		this.endpoint = ep;
		this.service = service;
		this.method = method;
	}

	public RpcEndpoint getEndpoint() {
		return endpoint;
	}

	public String getService() {
		return service;
	}

	public String getMethod() {
		return method;
	}

	public String getServiceUrl() {
		return String.format("%s/%s.%s", endpoint, service, method);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RpcClientMethodKey other = (RpcClientMethodKey) obj;
		if (endpoint == null) {
			if (other.endpoint != null)
				return false;
		} else if (!endpoint.equals(other.endpoint))
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}
}
