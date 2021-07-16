package org.helium.framework.route;

import org.helium.superpojo.SuperPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * 标记Server地址
 * <serverEndpoint id="TEST-SERVER-10">
 * <urls>
 * <url>http://10.10.61.73:8080;protocol=http</url>
 * </urls>
 * </serverUrls>
 * Created by Coral on 8/16/15.
 */

public class ServerEndpoint extends SuperPojo {

	private String id;

	private List<String> urlList = new ArrayList<>();

	/**
	 * 获得符合某协议的Server地址
	 *
	 * @param protocol
	 * @return
	 */
	public List<ServerUrl> getServerUrls(String protocol) {
		List<ServerUrl> list = new ArrayList<>();
		for (String s : urlList) {
			ServerUrl url = ServerUrl.parse(s);
			if (protocol.equals(url)) {
				list.add(url);
			}
		}
		return list;
	}

	/**
	 * 获得第一个符合协议的Server地址
	 *
	 * @param protocol
	 * @return
	 */
	public ServerUrl getServerUrl(String protocol) {
		return getServerUrl(protocol, a -> true);
	}

	/**
	 * 获得第一个符合的Server地址
	 *
	 * @param protocol
	 * @return
	 */
	public ServerUrl getServerUrl(String protocol, Predicate<ServerUrl> func) {
		for (String s : urlList) {
			ServerUrl url = ServerUrl.parse(s);
			if (protocol.equals(url.getProtocol())) {
				if (func.test(url)) {
					return url;
				}
			}
		}
		return null;
	}

	public List<String> getUrlList() {
		return urlList;
	}

	public ServerEndpoint setUrlList(List<String> urlList) {
		this.urlList = urlList;
		return this;
	}

	public String getId() {
		return id;
	}

	public ServerEndpoint setId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		ServerEndpoint that = (ServerEndpoint) o;

		if (id != null ? !id.equals(that.id) : that.id != null) {
			return false;
		}
		return !(urlList != null ? !urlList.equals(that.urlList) : that.urlList != null);

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (urlList != null ? urlList.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder()
				.append("Server<")
				.append(id)
				.append("> urls=");
		for (String url : urlList) {
			s.append(url);
			s.append(";");
		}
		return s.toString();
	}
}
