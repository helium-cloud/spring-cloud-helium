package org.helium.data.utils;

import com.feinno.superpojo.util.StringUtils;

import java.util.Properties;

/**
 * Created by Coral on 7/29/16.
 */
public class PropertiesLoader {
	private Properties props;
	public PropertiesLoader(Properties props) {
		this.props = props;
	}

	public int getInt(String key, int def) {
		String v = props.getProperty(key);
		if (StringUtils.isNullOrEmpty(v)) {
			return def;
		} else {
			return Integer.parseInt(v);
		}
	}

	public boolean getBoolean(String key, boolean def) {
		String v = props.getProperty(key);
		if (StringUtils.isNullOrEmpty(v)) {
			return def;
		} else {
			return Boolean.parseBoolean(v);
		}
	}

	public String getString(String key, String def) {
		String v = props.getProperty(key);
		if (StringUtils.isNullOrEmpty(v)) {
			return def;
		} else {
			return v;
		}
	}

	public String getString(String key) {
		String v = props.getProperty(key);
		if (StringUtils.isNullOrEmpty(v)) {
			throw new IllegalArgumentException("missing " + key);
		} else {
			return v;
		}
	}

	public int getInt(String key) {
		String v = props.getProperty(key);
		if (StringUtils.isNullOrEmpty(v)) {
			throw new IllegalArgumentException("missing " + key);
		} else {
			return Integer.parseInt(v);
		}
	}
}
