package org.helium.http.servlet.spi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lei Gao on 7/23/15.
 */
public class StaticResourceLoader {
	private static final Logger LOGGER = LoggerFactory.getLogger(StaticResourceLoader.class);
	private Class<?> rootClazz;
	private String resourceRoot;
	private Map<String, byte[]> entrys;

	public StaticResourceLoader(String resourceRoot, Class<?> clazz) {
		if (resourceRoot.endsWith("/")) {
			resourceRoot = resourceRoot.substring(0, resourceRoot.length() - 1);
		}
		this.resourceRoot = resourceRoot;
		this.rootClazz = clazz;
		this.entrys = new HashMap<>();
	}

	public byte[] loadResource(String key) {
		String path	= resourceRoot + key;
		URL url = rootClazz.getResource(path);

		LOGGER.info("loading path={}", path);
		if (url == null) {
			return null;
		}
		byte[] data = null;
		synchronized (this) {
			data = entrys.get(key);
		}
		if (data != null) {
			return data;
		}
		try {
			InputStream in = rootClazz.getResourceAsStream(path);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[4096];
			while (true) {
				int len = in.read(buffer, 0, buffer.length);
				if (len > 0) {
					out.write(buffer, 0, len);
				} else {
					break;
				}
			}
			data = out.toByteArray();
			LOGGER.info("load {} into cache", path);
			synchronized (this) {
				entrys.put(key, data);
			}
			return data;
		} catch (IOException ex) {
			LOGGER.error("load stream failed {}:" + path, ex);
			throw new RuntimeException("loadResource Failed:", ex);
		}
	}
}
