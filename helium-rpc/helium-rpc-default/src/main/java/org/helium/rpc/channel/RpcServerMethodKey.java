/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei Apr 7, 2012
 *
 * Copyright (c) 2012 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;


/**
 * 缓存所有的客户端访问信息,
 * <p>
 * 考虑LRU，考虑duplex情况下的perfmon
 * <p>
 * 分开保存
 * 1. fromId -> fromServer|fromComputer
 * 2. 在duplex情况只缓存
 * <p>
 * Created by Coral
 */
public class RpcServerMethodKey {
	private String fromComputer;
	private String fromService;
	private String service;
	private String method;

	public RpcServerMethodKey(String fromComputer, String fromService, String service, String method) {
		this.fromComputer = fromComputer;
		this.fromService = fromService;
		this.service = service;
		this.method = method;
	}

	public String getFromComputer() {
		return fromComputer;
	}

	public String getFromService() {
		return fromService;
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
		result = prime * result + ((fromComputer == null) ? 0 : fromComputer.hashCode());
		result = prime * result + ((fromService == null) ? 0 : fromService.hashCode());
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
		RpcServerMethodKey other = (RpcServerMethodKey) obj;
		if (fromComputer == null) {
			if (other.fromComputer != null)
				return false;
		} else if (!fromComputer.equals(other.fromComputer))
			return false;
		if (fromService == null) {
			if (other.fromService != null)
				return false;
		} else if (!fromService.equals(other.fromService))
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(fromService);
		sb.append('@').append(fromComputer).append(" >> ");
		sb.append(service).append('#').append(method);
		return sb.toString();
	}
}
