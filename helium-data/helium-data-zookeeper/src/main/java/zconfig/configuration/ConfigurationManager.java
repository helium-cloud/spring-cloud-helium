package zconfig.configuration;

import org.helium.util.ServiceEnviornment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.configuration.args.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ConfigurationManager {
	private static Logger LOGGER = LoggerFactory.getLogger(ConfigurationManager.class);

	private static HAConfigurator configurator;
	private static Object syncRoot = new Object();
	private static List<ConfigUpdater<?>> updaters;
	private static SearchIndex<ConfigUpdater<?>> updaterIndex;

	private static ConfigurationManager instance;
	private static Object syncObject = new Object();

	public ConfigurationManager() {
		updaters = new ArrayList<ConfigUpdater<?>>();
		String[] params = new String[] { "path", "type", "params" };
		try {
			updaterIndex = new SearchIndex(ConfigUpdater.class, updaters, params);
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
	}

	private static void initialize(){
		if (instance == null) {
			synchronized (syncObject) {
				if (instance == null) {
					instance = new ConfigurationManager();
				}
			}
		}
	}

	public static ConfigurationManager getInstance() {
		if (instance == null) {
			initialize();
		}

		return instance;
	}

	/**
	 * 
	 * 设置配置加载器, 可能存在本地配置加载或全局配置加载两种情况
	 * 
	 * @param
	 */
	public void setConfigurator(HAConfigurator configurator) {
		this.configurator = configurator;
	}

	public <K, V extends ConfigTableItem> ConfigTable<K, V> loadTable(final Class<K> keyType,
																	  final Class<V> valueType,
																	  final String path) throws ConfigurationException {
		try {
			ConfigTable<K, V> table = configurator.loadConfigTable(keyType, valueType, path);

			if (!SubscribeConfigManager.isSubscribeConfig(path, ConfigType.TABLE))
			{
				configurator.subscribeConfig(ConfigType.TABLE, path, null);

				SubscribeConfigManager.putSubscribeConfig(path, ConfigType.TABLE);
			}

			return  table;
		} catch (Exception e) {
			LOGGER.error(String.format("ConfigurationManager.loadTable failed! tableName: %s, error: %s", path, e.getMessage()), e);

			throw new ConfigurationException("loadTable failed: " + path, e);
		}
	}

	public <K, V extends ConfigTableItem> void subscribe(final Class<K> keyType,
                                                         final Class<V> valueType,
                                                         final String path) throws ConfigurationException {
		try {
			configurator.subscribeConfig(ConfigType.TABLE, path, null);
		}
		catch (Exception ex)
		{
			throw new ConfigurationException(ex.getMessage());
		}
	}

	public <K, V extends ConfigTableItem> void callBackConfigData(final Class<K> keyType,
                                                                  final Class<V> valueType,
                                                                  final String path,
                                                                  final ConfigUpdateAction<ConfigTable<K, V>> updateCallback) throws ConfigurationException {
		try {
			ConfigUpdater<ConfigTable<K, V>> updater = new ConfigUpdater<ConfigTable<K, V>>(path, ConfigType.TABLE, "") {
				@Override
				public void update() throws Exception {
					ConfigTable<K, V> table = configurator.loadConfigTable(keyType, valueType, path);

					if (updateCallback != null) {
						updateCallback.run(table);
					}
				}
			};
			synchronized (syncRoot) {
				updaters.add(updater);
				updaterIndex.build(updaters);
			}
		}
		catch (Exception ex)
		{
			throw new ConfigurationException(ex.getMessage());
		}
	}

	public String loadText(final String path) throws ConfigurationException {
		try {
			String strResult = configurator.loadConfigText(path, null);

			if (!SubscribeConfigManager.isSubscribeConfig(path, ConfigType.TEXT))
			{
				configurator.subscribeConfig(ConfigType.TEXT, path, null);

				SubscribeConfigManager.putSubscribeConfig(path, ConfigType.TEXT);
			}

			return strResult;
		} catch (Exception e) {
			LOGGER.error(String.format("ConfigurationManager.loadText failed! path: %s, error: %s", path, e.getMessage()), e);

			throw new ConfigurationException("loadText error", e);
		}
	}

	public void subscribe(final String path) throws ConfigurationException {
		try {
			configurator.subscribeConfig(ConfigType.TEXT, path, null);
		}
		catch (Exception ex)
		{
			throw new ConfigurationException(ex.getMessage());
		}
	}

	public void callBackConfigData(final String path, final ConfigUpdateAction<String> updateCallback) throws ConfigurationException {
		try {
			// 订阅用于配置变动时的更新
			ConfigUpdater<String> updater = new ConfigUpdater<String>(path, ConfigType.TEXT, "") {
				@Override
				public void update() throws Exception {
					String text = configurator.loadConfigText(path, null);

					if (updateCallback != null) {
						updateCallback.run(text);
					}
				}
			};
			synchronized (syncRoot) {
				updaters.add(updater);
				updaterIndex.build(updaters);
			}
		}
		catch (Exception ex)
		{
			throw new ConfigurationException(ex.getMessage());
		}
	}

	/**
	 * 
	 * Push方式更新配置
	 * 
	 * @param path
	 * @param type
	 */
	public void updateConfig(ConfigType type, String path, String params) throws ConfigurationException {
		try {
			List<ConfigUpdater<?>> list = updaterIndex.find(path, type, params);
			if (list != null && list.size() > 0)
				for (ConfigUpdater updater : list)
					updater.update();
		} catch (Exception e) {
			String msg = String.format("updateConfig(%s, %s) FAILED!", path, type.toString());
			LOGGER.error(msg, e);
			throw new ConfigurationException(msg, e);
		}
	}

	/**
	 * 将一个UTF-8的字符串转换为一个InputBuffer缓冲区
	 * 
	 * @param text
	 * @return
	 */
	public InputStream convertToStream(String text) {
		if (text == null)
			return null;
		InputStream stream = new ByteArrayInputStream(text.getBytes());
		return stream;
	}

	/**
	 * 将一个UTF-8的字符串转换为一个Properties对象
	 * 
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public Properties convertToProperies(String text) throws IOException {
		if (text == null)
			return null;
		Properties prop = new Properties();
		StringReader reader = new StringReader(text);
		prop.load(reader);
		return prop;
	}

	private ConfigParams configParamDecoration(ConfigParams params) {
		if (params == null) {
			params = new ConfigParams();
		}
		//params.put("service", ServiceSettings.INSTANCE.getServiceName());
		params.put("service", ServiceEnviornment.getServiceName());
		params.put("computer", ServiceEnviornment.getComputerName());
		return params;
	}
}
