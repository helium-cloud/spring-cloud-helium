package org.helium.framework.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Rpc匹配计算模块：支持灰度、复制、分发
 *
 */
public class ServiceMatchResults<T> {
	private List<ServiceMatchResult<T>> results;

	public ServiceMatchResults() {
	}

	public ServiceMatchResults(ServiceMatchResult result) {
		addResult(result);
	}


	/**
	 * 返回全量节点
	 *
	 * @return
	 */
	public List<T> getServices() {
		List<T> listProxy = new ArrayList<>();
		if (null == results || results.size() <= 0) {
			return listProxy;
		}
		for (ServiceMatchResult<T> result : results) {
			listProxy.add(result.getProxy());
		}
		return listProxy;

	}


	/**
	 * 返回非复制节点
	 *
	 * @return
	 */
	public T getService() {
		T service = null;
		if (null == results || results.size() <= 0) {
			return service;
		}
		for (ServiceMatchResult<T> result : results) {
			if (!result.isDuplicate()){
				service = result.getProxy();
			}
		}
		return service;

	}

	/**
	 * 返回抄送复制节点
	 *
	 * @return
	 */
	public List<T> getDuplicateService() {
		List<T> listProxy = new ArrayList<>();
		if (null == results || results.size() <= 0) {
			return listProxy;
		}
		for (ServiceMatchResult<T> result : results) {
			if (result.isDuplicate()){
				listProxy.add(result.getProxy());
			}
		}
		return listProxy;

	}

	/**
	 * 是否至少命中了一个结果
	 *
	 * @return
	 */
	public boolean hasResult() {
		return null != results;
	}

	/**
	 * 获取全部结果
	 *
	 * @return
	 */
	public List<ServiceMatchResult<T>> getResults() {
		return results;
	}

	/**
	 * 增加一个结果
	 *
	 * @param r
	 */
	public void addResult(ServiceMatchResult r) {
		if (null == results) {
			results = new ArrayList<>();
		}
		results.add(r);
	}

	public void addResults(ServiceMatchResults<T> lv) {
		if (null != lv.results) {
			lv.results.forEach(a -> addResult(a));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("ServiceMatchResults:\n[");
		if (results == null) {
			sb.append("]");
			return sb.toString();
		}
		for (ServiceMatchResult serviceMatchResult : results) {
			sb.append(serviceMatchResult.toString()).append("\n");
		}
		sb.append("]");
		return sb.toString();
	}
}
