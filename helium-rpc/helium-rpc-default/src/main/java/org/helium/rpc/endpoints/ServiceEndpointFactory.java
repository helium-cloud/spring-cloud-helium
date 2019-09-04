package org.helium.rpc.endpoints;


/**
 * 用于创建ServiceEndpoint的工厂类
 * Created by Coral
 */
public interface ServiceEndpointFactory {
	/**
	 * 协议
	 *
	 * @return
	 */
	String getProtocol();

	/**
	 * 从文本中解析一个Endpoint
	 *
	 * @param str
	 * @return
	 */
	ServiceEndpoint parseEndpoint(String str);
}
