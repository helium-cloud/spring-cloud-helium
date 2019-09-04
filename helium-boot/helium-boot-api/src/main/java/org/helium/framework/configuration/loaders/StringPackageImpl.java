package org.helium.framework.configuration.loaders;

import org.helium.util.CollectionUtils;
import org.helium.framework.BeanContext;
import org.helium.framework.configuration.ConfigProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Coral on 10/12/15.
 */
class StringPackageImpl implements StringPackage {
	private static final Logger LOGGER = LoggerFactory.getLogger(StringPackageImpl.class);
	private Map<String, String> map;

	StringPackageImpl(String file) {
		//TODO 支持spring扩展
		ConfigProvider provider = BeanContext.getContextService().getService(ConfigProvider.class);
		String s = provider.loadText(file);
		BufferedReader reader = new BufferedReader(new StringReader(s));
		map = new HashMap<>();
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if ("".equals(line)) {
					continue;
				}
				String old = map.put(line, line);
				if (old != null) {
					LOGGER.warn("duplicated String: {}" + line);
				}
				LOGGER.info("loadString: {}", line);
			}
		} catch (IOException e) {
			throw new RuntimeException("ReadStringPackage Failed:" + file);
		}
	}
	@Override
	public boolean hasEntry(String s) {
		return map.get(s) != null;
	}

	@Override
	public List<String> getStringList() {
		return CollectionUtils.cloneValues(map);
	}

	@Override
	public boolean isEmpty() {
		if (null == map || map.isEmpty()) {
			return true;
		}
		return false;
	}

	@Override
	public void refresh(String file) {
		if (null != map && map.size() > 0) {
			map.clear();
		}
		new StringPackageImpl(file);
	}
}
