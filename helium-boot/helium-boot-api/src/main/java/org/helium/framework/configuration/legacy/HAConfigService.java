package org.helium.framework.configuration.legacy;

import org.helium.framework.annotations.ServiceInterface;

/**
 * Created by Leon on 8/3/16.
 */
@ServiceInterface(id = "helium:HAConfigService")
public interface HAConfigService {
	/**
	 * 读取一个ConfigText
	 * @param path
	 * @param params
	 * @return
	 * @throws Exception
	 */
	String loadConfigText(String path, ConfigParams params) throws Exception;

	/**
	 * 读取一个ConfigTable
	 * @param keyType
	 * @param valueType
	 * @param path
	 * @param <K>
	 * @param <V>
	 * @return
	 * @throws Exception
	 */
	<K, V extends ConfigTableItem> ConfigTable<K, V> loadConfigTable(Class<K> keyType, Class<V> valueType, String path) throws Exception;
}
