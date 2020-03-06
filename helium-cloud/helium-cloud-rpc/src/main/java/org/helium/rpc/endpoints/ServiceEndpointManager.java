package org.helium.rpc.endpoints;

import org.helium.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class ServiceEndpointManager {
	public static final ServiceEndpointManager INSTANCE = new ServiceEndpointManager();

	private Map<String, ServiceEndpointFactory> epFactorys;

	private ServiceEndpointManager() {
		epFactorys = new HashMap<String, ServiceEndpointFactory>();
		//
		// Add Default Endpoints
		this.addEndpointFactory(InprocEndpoint.PROTOCOL, new InprocEndpointFactory());
		this.addEndpointFactory(TcpEndpoint.PROTOCOL, new TcpEndpointFactory());
		this.addEndpointFactory(TlsEndpoint.PROTOCOL, new TlsEndpointFactory());
		this.addEndpointFactory(UdpEndpoint.PROTOCOL, new UdpEndpointFactory());
		this.addEndpointFactory(HttpEndpoint.PROTOCOL, new HttpEndpointFactory());
		this.addEndpointFactory(HttpsEndpoint.PROTOCOL, new HttpsEndpointFactory());
	}

	public ServiceEndpoint parseEndpoint(String s) {
		if (StringUtils.isNullOrEmpty(s)) {
			throw new IllegalArgumentException("BadEndpointUri: null");
		}

		int begin = s.indexOf(ServiceEndpoint.BEGIN_DELIMETER);
		String protocol;
		String value;
		String parameters = null;

		if (begin < 0) {
			throw new IllegalArgumentException("BadEndpointUri:" + s);
		}
		protocol = s.substring(0, begin);
		int b2 = begin + ServiceEndpoint.BEGIN_DELIMETER.length();

		int end1 = s.indexOf(ServiceEndpoint.PARAM_DELIMETER);
		if (end1 > 0) {
			value = s.substring(b2, end1);
			parameters = s.substring(end1 + ServiceEndpoint.PARAM_DELIMETER.length());
		} else {
			value = s.substring(b2);
		}

		ServiceEndpointFactory factory = epFactorys.get(protocol);
		if (factory == null) {
			throw new IllegalArgumentException("BadEndpointUri unknown protocol:" + protocol);
		}

		ServiceEndpoint ep = factory.parseEndpoint(value);
		if (parameters != null) {
			ep.parseParameters(parameters);
		}
		return ep;
	}

	public void addEndpointFactory(String protocol, ServiceEndpointFactory factory) {
		epFactorys.put(protocol.toLowerCase(), factory);
	}

	public static void main(String[] args) {
		System.out.println(INSTANCE.parseEndpoint("tcp://192.168.1.1:5080"));
		System.out.println(INSTANCE.parseEndpoint("tcp://192.168.1.1:5080;"));
		System.out.println(INSTANCE.parseEndpoint("tcp://192.168.1.1:5080;nlb=true"));
		System.out.println(INSTANCE.parseEndpoint("udp://192.168.1.1:5080"));
		System.out.println(INSTANCE.parseEndpoint("udp://192.168.1.1:5080;nlb=true;tls=true"));
		System.out.println(INSTANCE.parseEndpoint("inproc://;"));
		System.out.println(INSTANCE.parseEndpoint("inproc://;transparent=true"));
	}
}
