/*
 * FAE, Feinno App Engine
 *
 * Create by gaolei 2011-1-18
 *
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.rpc.channel;

import org.helium.util.StringUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * Rpc地址, 处理Rpc客户端调用时的基础逻辑, 实现任何协议时应当继承此接口
 * <p>
 * RpcEndpoint地址的格式为: protocol://address;param1;param2=value2; 例如:<br>
 * tcp://192.168.1.100:7000;nlb;timeout=7000;<br>
 * uds:///var/uds_comm_file;<br>
 * http://192.168.1.100:8080/rpc_service<br>
 * inproc:///<br>
 * <br>
 * </p>
 * <p>
 * RpcEndpoint分为以下部分
 * <strong>protocol:</strong> 协议, 如tcp,uds,http, inproc<br>
 * <strong>address:</strong> 地址<br>
 * <strong>parameters:</strong> 参数, 由分号分隔开, 目前包含的参数有<br>
 * NLB: 网络负载均衡, 要求创建的代理类包含连接循环机制,
 * timeout: 超时, 可针对对段设定特定的超时
 * </p>
 * Created by Coral
 */
public abstract class RpcEndpoint {
	public static final String NLB_PARAM = "NLB";
	public static final String TIMEOUT_PARAM = "timeout";

	private Map<String, String> parameters;

	/**
	 * 解析除去xxx://部分的Uri
	 *
	 * @param address
	 */
	protected abstract void parseAddress(String address);

	/**
	 * 返回字符串格式的协议标识如: "tcp", "http", "inproc"等...
	 *
	 * @return
	 */
	public abstract String getProtocol();

	/**
	 * 获取一个客户端的Channel, 这个Channel在实现时可以放在private static字段上,
	 * 使用双检锁进行初始化
	 *
	 * @return
	 */
	public abstract RpcClientChannel getClientChannel();

	/**
	 * 获取Endpoint的附加参数
	 *
	 * @param key
	 * @return
	 */
	public final String getParameter(String key) {
		if (parameters == null) {
			return null;
		} else {
			return parameters.get(key);
		}
	}

	public final String formatParameters() {
		if (parameters == null) {
			return "";
		} else {
			StringBuilder s = new StringBuilder();
			s.append(";");
			for (String key : parameters.keySet()) {
				String value = parameters.get(key);
				if (StringUtils.isNullOrEmpty(value)) {
					s.append(key);
					s.append(";");
				} else {
					s.append(key);
					s.append("=");
					s.append(value);
				}
			}
			return s.toString();
		}
	}

	/**
	 * 设置Endpoint的附加参数, 非线程安全
	 *
	 * @param key
	 * @param value
	 */
	public final void putParameter(String key, String value) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		parameters.put(key, value);
	}

	/**
	 * 解析除去"protocol://"后的部分
	 *
	 * @param strWithoutProtocol
	 */
	public void parseWith(String strWithoutProtocol) {
		int end = strWithoutProtocol.indexOf(';');
		String address;
		if (end > 0) {
			String params = strWithoutProtocol.substring(end + 1);
			parameters = StringUtils.splitValuePairs(params, ";", "=");
		} else {
			end = strWithoutProtocol.length();
		}
		if (strWithoutProtocol.charAt(end - 1) == '/') {
			address = strWithoutProtocol.substring(0, end - 1);
		} else {
			address = strWithoutProtocol.substring(0, end);
		}
		parseAddress(address);
	}

	@Override
	public abstract String toString();

	@Override
	public abstract boolean equals(Object o);

	@Override
	public abstract int hashCode();
}
