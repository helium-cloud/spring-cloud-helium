package org.helium.framework.configuration;

import com.alibaba.fastjson.JSONObject;
import com.feinno.superpojo.SuperPojo;
import org.helium.framework.annotations.ServiceInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Properties;

/**
 * Created by Coral on 5/6/15.
 */
@ServiceInterface(id = ConfigProvider.BEAN_ID)
public interface ConfigProvider {
	String BEAN_ID = "helium:ConfigProvider";

    /**
     * 读取原始数据流
     * @param file
     * @return
     */
    InputStream loadRaw(String file);

	/**
	 * 读取原始文件
	 * @param file
	 * @return
	 */
	default String loadRawText(String file) {
        InputStream stream = loadRaw(file);
        if (stream == null) {
            return null;
        }
        String text;
        try {
			// available有待優化
            byte[] buffer = new byte[stream.available()];
			stream.read(buffer);
            text = new String(buffer);
        } catch (IOException e) {
            throw new IllegalArgumentException("load file failed:" + file, e);
        }
        return text;
    }

	/**
	 * 读取一个文本文件
	 * @param file
	 * @return
	 */
	default String loadText(String file) {
		String txt = loadRawText(file);
		return applyConfigText(file, txt);
	}

	/**
	 * 是否存在文件
	 * @param file
	 * @return
	 */
	boolean hasFile(String file);


	/**
	 * 读取一个Xml文件
	 * @param file
	 * @param clazz
	 * @param <E>
	 * @return
	 */
	default <E extends SuperPojo> E loadXml(String file, Class<E> clazz) {
		return loadXml(file, clazz, false);
	}

	/**
	 * 读取一个Xml文件
	 * @param file
	 * @param clazz
	 * @param <E>
	 * @return
	 */
	default <E extends SuperPojo> E loadJson(String file, Class<E> clazz) {

		return loadJson(file, clazz, false);
	}
	/**
	 * 读取一个Xml文件
	 * @param file
	 * @param clazz
	 * @param isRaw 是否不做修改
	 * @param <E>
	 * @return
	 */
	default <E extends SuperPojo> E loadJson(String file, Class<E> clazz, boolean isRaw) {
		try {
			String json = isRaw ? loadRawText(file) : loadText(file);
			E e = JSONObject.parseObject(json, clazz);
			return e;
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new IllegalArgumentException("load json failed! :" + file, ex);
		}
	}
	/**
	 * 读取一个Xml文件
	 * @param file
	 * @param clazz
	 * @param isRaw 是否不做修改
	 * @param <E>
	 * @return
	 */
	default <E extends SuperPojo> E loadXml(String file, Class<E> clazz, boolean isRaw) {
		try {
			String xml = isRaw ? loadRawText(file) : loadText(file);
			E e = clazz.newInstance();
			e.parseXmlFrom(xml);
			return e;
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new IllegalArgumentException("load xml failed! :" + file, ex);
		}
	}

	/**
	 * 读取一个符合properties规范的文件
	 * @param file
	 * @see java.util.Properties
	 * @return
	 */
	default Properties loadProperties(String file) {
		try {
			String txt = loadText(file);
			Properties result = new Properties();
			result.load(new StringReader(txt));
			return result;
		} catch (IllegalArgumentException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new IllegalArgumentException("load properties failed! :" + file, ex);
		}
	}

	default String applyConfigText(String path, String text) {
		return Environments.applyConfigText(path, text);
	}


	default String applyConfigVar(String text) {
		return Environments.applyConfigVariable(text);
	}
	default String getAbsolutePath(String file) {
		return null;
	}
	default String findJarFile(String pattern) {return null;}
}
