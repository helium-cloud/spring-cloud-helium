/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 2, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.client;

import org.helium.rpc.channel.RpcEndpoint;

/**
 * 用于缓存RpcMethodStub的Key
 * <p>
 * Created by Coral
 */
public class RpcMethodStubKey {
	private RpcEndpoint ep;
	private String service;
	private String method;

	public RpcMethodStubKey(RpcEndpoint ep, String service, String method) {
		this.ep = ep;
		this.service = service;
		this.method = method;
	}

	public RpcEndpoint getEndpoint() {
		return ep;
	}

	public String getService() {
		return service;
	}

	public String getMethod() {
		return method;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ep == null) ? 0 : ep.hashCode());
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
		RpcMethodStubKey other = (RpcMethodStubKey) obj;
		if (ep == null) {
			if (other.ep != null)
				return false;
		} else if (!ep.equals(other.ep))
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