package org.helium.logging.spi;

import org.helium.logging.LogAppender;
import org.helium.logging.LogLevel;
import org.helium.logging.LoggingConfiguration;
import org.helium.logging.LoggingConfiguration.FilterNode;
import org.helium.logging.LoggingConfiguration.OutputNode;
import org.helium.logging.LoggingConfiguration.SubLevelNode;
import org.helium.logging.factory.LoggingConfigurationFactory;
import org.helium.logging.spi.LoggerFactoryImpl.SubLevel;
import org.helium.util.CollectionUtils;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 核心、唯一、管理Log的类
 * Created by Coral on 8/31/15.
 */
class LogContext {
	static final Logger LOGGER = LoggerFactory.getLogger(LogContext.class);
	static final LogContext INSTANCE = new LogContext();
	static final Marker MARKER = MarkerFactory.getMarker("LOGGER");

	//这个是外部扩展自定义使用，需要自己实现
	private static final String CONFIG_EXT = "org.helium.logging.factory.LoggingConfigurationFactoryExt";

	private LogOutput defaultOutput;
	private List<LogOutput> outputs;

	private LogContext() {
		//
		// output
		defaultOutput = new LogOutput(LogOutput.DEFAULT_NAME);
		List<LogAppender> appenders = new ArrayList<>();
		appenders.add(new ConsoleAppender());
		defaultOutput.initWithAppenders(appenders);
		SimpleMarkerFilter filter = new SimpleMarkerFilter("LOGGER");
		LoggerFactoryImpl.INSTANCE.putSubLevel("org.helium.logging", LogLevel.INFO, null, filter);

		Thread initThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LoggingConfiguration configuration = loadDefaultConfiguration();
					if (configuration != null) {
						applyConfiguration(configuration);
					}
				} catch (Exception ex) {
					LOGGER.error(MARKER, "applyConfiguration failed: {}", ex);
				}
			}
		});
		initThread.setName("logging-init");
		initThread.start();
	}

	public LoggingConfiguration loadDefaultConfiguration() {
		try {
			String path1 = System.getProperty("user.dir") + "/logging.xml";
			Path path = Paths.get(path1);
			LOGGER.info(MARKER, "try load logging.xml from:{} exists={}", path1, Files.exists(path));
			String xml;
			if (!Files.exists(path)) {
				InputStream is = LogContext.class.getClassLoader().getResourceAsStream("logging.xml");
				if (is == null) {
					LOGGER.info(MARKER, "not find logging.xml in resource path");
					try {
						Class logFactory = Class.forName(CONFIG_EXT);
						Object object = logFactory.newInstance();
						if (object instanceof LoggingConfigurationFactory){
							LoggingConfigurationFactory loggingConfigurationFactory = (LoggingConfigurationFactory) object;
							return loggingConfigurationFactory.getLoggingConfiguration();
						}
					} catch (Exception e){
						LOGGER.error(MARKER, "try load ext config not Exist:{}", e.getMessage());
					}
					return null;
				} else {
					LOGGER.info(MARKER, "loading logging.xml from resource path");
				}

				StringBuffer out = new StringBuffer();
				byte[] b = new byte[4096];
				for (int n; (n = is.read(b)) != -1;) {
					out.append(new String(b, 0, n));
				}
				xml = out.toString();
			} else {
				// System.out.println("Gotcha <logging.xml>: " + path.toFile().getName());
				xml = new String(Files.readAllBytes(path));
			}
			LOGGER.info(MARKER, "read logging.xml \r\n {}", xml);
			LoggingConfiguration configuration = new LoggingConfiguration();
			configuration.parseFromJson(xml);
			return configuration;
		} catch (Exception e) {
			LOGGER.error(MARKER, "loading logging.xml failed: \r\n {}", e);
			return null;
		}
	}

	public void applyConfiguration(LoggingConfiguration configuration) {
		LOGGER.info(MARKER, "applying loggerConfiguration rootLevel = {}", configuration.getLevel());
		LogLevel rootLevel = LogUtils.parseLogLevel(configuration.getLevel());

		//
		// 处理<outputs/>节点
		String outputString = null;
		LogOutput defaultOutput = null;
		Map<String, LogOutput> outputs = new HashMap<>();
		for (OutputNode node : configuration.getOutputs()) {
			try{
                LogOutput output = new LogOutput(node);
                outputs.put(output.getName(), output);
                output.start();
                if (outputString == null) {
                    outputString = "{" + output.getName();
                } else {
                    outputString = outputString + "," + output.getName();
                }
			}catch (Exception e){
				LOGGER.error("apply output appender error", e);
			}
		}
		outputString += "}";

		defaultOutput = outputs.get(LogOutput.DEFAULT_NAME);
		if (defaultOutput == null) {
			throw new IllegalArgumentException("<outputs/> must has a default output");
		}

		//
		// 处理<subLevels/>节点
		Map<String, SubLevel> subLevels = new HashMap<>();
		for (SubLevelNode node : configuration.getSubLevels()) {
			if (StringUtils.isNullOrEmpty(node.getName())) {
				throw new IllegalArgumentException("<subLevel> node name can't be null");
			}
			SubLevel subLevel = new SubLevel();
			subLevel.name = StringUtils.trimEnd(node.getName(), '*');
			subLevel.level = LogUtils.parseLogLevel(node.getLevel());
			if (!StringUtils.isNullOrEmpty(node.getOutput())) {
				subLevel.output = outputs.get(node.getOutput());
				if (subLevel.output == null) {
					String msg = String.format("<subLevel name=\"%s\"> output not found:" + node.getOutput());
					throw new IllegalArgumentException(msg);
				}
			}
			subLevels.put(subLevel.name, subLevel);
		}

		//
		// 处理<filters/>节点
		int filterCount = 0;
		List<LogFilter> rootFilters = new ArrayList<>();
		for (FilterNode node: configuration.getFilters()){
			filterCount++;
			LogFilter filter = new LogFilter(node);
			if (!StringUtils.isNullOrEmpty(node.getOutput())) {
				filter.output = outputs.get(node.getOutput());
				if (filter.output == null) {
					String msg = String.format("<filter> output not found:" + node.getOutput());
					throw new IllegalArgumentException(msg);
				}
			}
			List<LogFilter> filters;
			if (!StringUtils.isNullOrEmpty(node.getLoggerName())) {
				SubLevel subLevel = subLevels.get(node.getLoggerName());
				if (subLevel == null) {
					subLevel = new SubLevel();
					subLevel.name = StringUtils.trimEnd(node.getLoggerName(), '*');
					subLevel.level = rootLevel;
					subLevels.put(subLevel.name, subLevel);
				}
				filters = subLevel.filters;
			} else {
				filters = rootFilters;
			}
			filters.add(filter);
		}

		LOGGER.info(MARKER, "applyLoggingConfiguration DONE!!! level={}|{}, outputs={}, filters={}",
				rootLevel, subLevels.size(), outputString, filterCount);

		//
		// 如果到这里那么应该配置没问题了，一次性设置好吧
		LoggerFactoryImpl.INSTANCE.setLevels(rootLevel, rootFilters, subLevels);

		LogContext.INSTANCE.setOutputs(defaultOutput, outputs.values());

		SimpleMarkerFilter filter = new SimpleMarkerFilter("LOGGER");
		LoggerFactoryImpl.INSTANCE.putSubLevel("org.helium.logging", LogLevel.INFO, null, filter);
	}

	public void setOutputs(LogOutput defaultOutput, Iterable<LogOutput> outputs) {
		this.defaultOutput = defaultOutput;
		this.outputs = CollectionUtils.cloneList(outputs);
	}

	void doLog(LogEvent event) {
		if (event.output != null) {
			event.output.doLog(event);
		} else {
			defaultOutput.doLog(event);
		}
	}
}
