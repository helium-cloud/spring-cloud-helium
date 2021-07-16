package org.helium.superpojo;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * @author wuhao
 * @createTime 2021-07-15 17:55:00
 */
public class SuperPojoManager {
	public static String toJsonString(Object o) throws JsonProcessingException {
		return JsonUtils.toJson(o);
	}

	public static <T> T jsonToObject(String json, Class<T> cj) throws JsonProcessingException {
		return JsonUtils.toObject(json, cj);
	}

	public static byte[] toPbByteArray(Object object) {
		return ProtostuffUtils.toBytes(object);
	}

	public static <T> T parsePbFrom(byte[] bytes, Class<T> tClass) {
		return ProtostuffUtils.toObject(bytes, tClass);
	}
}
