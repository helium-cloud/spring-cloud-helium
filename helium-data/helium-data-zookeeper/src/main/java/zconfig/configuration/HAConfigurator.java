package zconfig.configuration;

import org.helium.rpc.duplex.RpcDuplexClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zconfig.configuration.args.*;

import javax.naming.ConfigurationException;
import java.util.Date;

public class HAConfigurator {
	private static Logger LOGGER = LoggerFactory.getLogger(HAConfigurator.class);

	public static String CENTER_URL_KEY = "CENTER_URL";

	/** 与center交互的service */
	private HAWorkerAgentService service;

	/**
	 * 构造方法
	 *
	 * @param client
	 */
	public HAConfigurator(RpcDuplexClient client) {
		service = client.getService(HAWorkerAgentService.class);

		client.registerCallbackService(new HAWorkerAgentCallbackService() {
			@Override
			public void notifyConfigExpired(HAConfigArgs args) {
				// 通知某一条配置过期，需要去服务器取最新数据
				try {
					ConfigurationManager.getInstance().updateConfig(args.getType(), args.getPath(), args.getParams());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	public <K, V extends ConfigTableItem> ConfigTable<K, V> loadConfigTable(Class<K> keyType, Class<V> valueType,
																			String path) throws ConfigurationException {
		HAConfigArgs args = new HAConfigArgs();
		args.setType(ConfigType.TABLE);
		args.setPath(path);
		args.setParams(""); // ConfigTableSetter 不支持特例化

		HAConfigTableBuffer tableBuffer = service.loadConfigTable(args);
		ConfigTable<K, V> table = null;
		try {
			table = tableBuffer.toTable(keyType, valueType);
		} catch (ConfigurationNotFoundException e) {
			e.printStackTrace();
		} catch (ConfigurationFailedException e) {
			e.printStackTrace();
		}

		ConfigDataManager.putConfigTable(path, ConfigType.TABLE, table);

		try {
			// 如果更新成功，那么更新版本号
			Date version = table.getVersion();
			HAConfigArgs versionArgs = new HAConfigArgs();
			versionArgs.setType(ConfigType.TABLE);
			versionArgs.setPath(path);
			versionArgs.setParams("");
			versionArgs.setVersion(version);
			service.updateConfigVersion(versionArgs);
		} catch (Exception e) {
			LOGGER.error(String.format("HAConfigurator.loadConfigTable failed! tableName: %s, error: %s", path, e.getMessage()), e);

		}

		return table;
	}

	public String loadConfigText(String path, ConfigParams params)
			throws ConfigurationException {
		HAConfigArgs args = new HAConfigArgs();
		args.setType(ConfigType.TEXT);
		args.setPath(path);
		args.setParams("");

		/*ConfigParams p2 = new ConfigParams();
		//
		// 增加本机的特定环境信息
		p2.put("service", ServiceEnviornment.getServiceName());
		p2.put("computer", ServiceEnviornment.getComputerName());
		if (params != null) {
			p2 = p2.merge(params, true);
		}
		args.setParams(p2.toString());*/

		HAConfigTextBuffer configTextBuffer = service.loadConfigText(args);

		try {
			// 如果更新成功，那么更新版本号
			Date version = configTextBuffer.getVersion();
			HAConfigArgs versionArgs = new HAConfigArgs();
			versionArgs.setType(ConfigType.TEXT);
			versionArgs.setPath(path);
			versionArgs.setParams(params != null ? params.toString() : "");
			versionArgs.setVersion(version);
			service.updateConfigVersion(versionArgs);
		} catch (Exception e) {
			LOGGER.error(String.format("HAConfigurator.loadConfigText failed! path: %s, error: %s", path, e.getMessage()), e);

		}

		String strResult = configTextBuffer.getText();

		ConfigDataManager.putConfigText(path, ConfigType.TEXT, strResult);

		return strResult;
	}

	public void subscribeConfig(ConfigType type, String path, ConfigParams params) {
		/*if (type != URCSConfigType.TABLE) {
			if (params == null) {
				params = new ConfigParams();
			}
			params.put("service", ServiceEnviornment.getServiceName());
			params.put("computer", ServiceEnviornment.getComputerName());
		}*/

		HAConfigArgs args = new HAConfigArgs();
		args.setType(type);
		args.setPath(path);
		args.setParams(params != null ? params.toString() : "");
		service.subscribeConfig(args);
	}
}
