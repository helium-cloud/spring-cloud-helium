package org.helium.rpc.endpoints;

import org.helium.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 服务接入点, 定位服务端口所在的位置
 * 在leaf系统中，ServiceEndpoint的定义形如
 * <p>
 * protocol://address;parameter=value;
 * <p>
 * parameter的扩展参数不会参与hashCode与equals计算
 * <p>
 * Created by Coral
 */
public abstract class ServiceEndpoint {
	public static final String BEGIN_DELIMETER = "://";
	public static final String END_DELIMETER = ";";
	public static final String PARAM_ASSIGNMENT = "=";
	public static final String PARAM_DELIMETER = ";";

	private Map<String, String> parameters;

	/**
	 * 获取协议
	 *
	 * @return
	 */
	public abstract String getProtocol();

	/**
	 * 获取扩展参数
	 *
	 * @param param
	 * @return
	 */
	public String getParameter(String param) {
		if (parameters == null) {
			return null;
		} else {
			return parameters.get(param);
		}
	}

	/**
	 * 增加一个扩展参数
	 *
	 * @param param
	 * @param value
	 */
	public void putParameter(String param, String value) {
		if (parameters == null) {
			parameters = new HashMap<String, String>();
		}
		parameters.put(param, value);
	}

	/**
	 * 移除一个扩展参数
	 *
	 * @param param
	 */
	public void removeParameter(String param) {
		if (parameters != null) {
			parameters.remove(param);
		}
	}

	/**
	 * 设置所有参数
	 *
	 * @param params
	 */
	public void setParameters(Map<String, String> params) {
		this.parameters = params;
	}

	/**
	 * 解析所有参数
	 *
	 * @param paramsStr
	 */
	public void parseParameters(String paramsStr) {
		parameters = new HashMap<String, String>();
		for (Entry<String, String> entry : StringUtils.splitValuePairs(paramsStr, PARAM_DELIMETER, PARAM_ASSIGNMENT).entrySet()) {
			if (StringUtils.isNullOrEmpty(entry.getKey())) {
				continue;
			}
			parameters.put(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 获取ServiceEndpoint的取值
	 *
	 * @return
	 */
	public abstract String getValue();

	/**
	 * 输出为protocol://address;p1=v1;p2=v2格式的字符串
	 */
	@Override
	public final String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.getProtocol());
		b.append(BEGIN_DELIMETER);
		b.append(getValue());
		if (parameters != null) {
			for (Entry<String, String> entry : parameters.entrySet()) {
				b.append(PARAM_DELIMETER);
				b.append(entry.getKey());
				b.append(PARAM_ASSIGNMENT);
				b.append(entry.getValue());
			}
		}
		return b.toString();
	}

	/**
	 * parameters不参与计算
	 */
	@Override
	public final int hashCode() {
		return this.getProtocol().hashCode() ^ getValue().hashCode();
	}

	/**
	 * parameters不参与计算
	 */
	@Override
	public final boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		ServiceEndpoint other = (ServiceEndpoint) obj;
		if (getValue() == null) {
			if (other.getValue() != null) {
				return false;
			}
		} else if (!getValue().equals(other.getValue())) {
			return false;
		}

		if (getProtocol() == null) {
			if (other.getProtocol() != null) {
				return false;
			}
		} else if (!getProtocol().equals(other.getProtocol())) {
			return false;
		}
		return true;
	}
}