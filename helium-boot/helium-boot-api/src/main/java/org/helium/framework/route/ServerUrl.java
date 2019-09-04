package org.helium.framework.route;

import org.helium.util.Outer;
import org.helium.util.StringUtils;
import org.helium.util.Tuple;
import org.helium.util.CollectionUtils;
import org.helium.util.TypeUtils;

import java.util.List;

/**
 * 在Helium路由体系中，描述一个server地址
 * tcp://10.10.208.126:5070;protocol=sip;tag=monitor;
 *
 * Created by Coral on 8/6/15.
 */
public class ServerUrl {
	public static final ServerUrl INPROC = ServerUrl.parse("inproc://local;protocol=*");

	private String url;
	private String protocol;
	private List<Tuple<String, String>> parameters;

	public ServerUrl(String url, List<Tuple<String, String>> parameters) {
		this.url = url;
		if (parameters == null) {
			throw new IllegalArgumentException("Unknown protocol for serverEndpoint:" + url);
		}
		this.parameters = CollectionUtils.filter(parameters, tuple -> {
			if ("protocol".equals(tuple.getV1())) {
				this.protocol = tuple.getV2();
				return null;
			} else {
				return tuple;
			}
		});
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public List<String> getParameters(String key) {
		return CollectionUtils.filter(parameters, v -> {
			if (key.equals(v.getV1())) {
				return v.getV2();
			} else {
				return null;
			}
		});
	}

	public void putParameters(String key, String value) {
		this.parameters.add(new Tuple<>(key, value));
	}

	public static ServerUrl parse(String s) {
		try {
			Outer<String> url = new Outer<>();
			Outer<String> remain = new Outer<>();
			if (!StringUtils.splitWithFirst(s, ";", url, remain)) {
				return new ServerUrl(url.value(), null);
			} else {
				return new ServerUrl(url.value(), TypeUtils.splitValuePairsList(remain.value(), ";", "="));
			}
		} catch (Exception ex) {
			throw new IllegalArgumentException("badFormat ServerUrl:" + s, ex);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ServerUrl serverUrl = (ServerUrl) o;

		if (url != null ? !url.equals(serverUrl.url) : serverUrl.url != null) return false;
		return !(protocol != null ? !protocol.equals(serverUrl.protocol) : serverUrl.protocol != null);

	}

	@Override
	public int hashCode() {
		int result = url != null ? url.hashCode() : 0;
		result = 31 * result + (protocol != null ? protocol.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder()
				.append(url)
				.append(";protocol=")
				.append(protocol);

		for (Tuple<String, String> p: parameters) {
			str.append(";").append(p.getV1()).append("=").append(p.getV2());
		}
		return str.toString();
	}

	public static void main(String[] args) {
		ServerUrl ep = ServerUrl.parse("tcp://10.10.208.126:5070;protocol=sip;tag=monitor;");
		System.out.println(ep);
		ep = ServerUrl.parse("http://www.baidu.com:7892/sssd;protocol=http");
		System.out.println(ep);
	}
}
