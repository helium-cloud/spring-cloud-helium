package org.helium.superpojo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wuhao
 * @createTime 2021-07-15 17:34:00
 */
public class JsonUtils {
	protected static transient ObjectMapper mapper = new ObjectMapper();
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

	public static String toJson(Object o) {
		try {
			return mapper.writeValueAsString(o);
		} catch (Exception e) {
			LOGGER.error("JsonUtils toJson:{} ", o, e);
		}
		return null;
	}

	public static <T> T toObject(String json, Class<T> cj) {
		try {
			return mapper.readValue(json, cj);
		} catch (Exception e) {
			LOGGER.error("JsonUtils toObject:{} ", json, e);
		}
		return null;
	}
}
