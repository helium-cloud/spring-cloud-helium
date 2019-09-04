package org.helium.logging.spi;


import org.helium.logging.LogLevel;
import org.helium.logging.MarkerFilter;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author coral
 * @auther Gao Lei
 * TODO: 设置级别的代码可能需要优化，也可能完全不需要
 * @version 创建时间：2014年9月17日 类说明
 */
public class LoggerFactoryImpl implements ILoggerFactory {
	public static final LoggerFactoryImpl INSTANCE = new LoggerFactoryImpl();
	public static final int MAX_LOGGERS = 20480;
	public static final String ELSE_LOGGER_NAME = "else.Logger";

	private LogLevel rootLevel = LogLevel.INFO;
	private LinkChain<LogFilter> rootFilter;
	private List<SubLevel> subLevels = new ArrayList<>();
	private Map<String, LoggerImpl> loggers = new ConcurrentHashMap<>();
	private LoggerImpl elseLogger;

	private LoggerFactoryImpl() {
		elseLogger = new LoggerImpl(ELSE_LOGGER_NAME, rootLevel);
		loggers.put(ELSE_LOGGER_NAME, elseLogger);
	}

	public LogLevel getRootLevel() {
		return rootLevel;
	}

	@Override
	public synchronized Logger getLogger(String name) {
		LoggerImpl logger = loggers.get(name);
		if (logger == null) {
			if (loggers.size() >= MAX_LOGGERS) {
				logger = elseLogger;
			} else {
				SubLevel n = getLevel(name);
				if (n == null) {
					logger = new LoggerImpl(name, rootLevel);
				} else {
					logger = new LoggerImpl(name, n.level);
					logger.setOutput(n.output);
				}
				loggers.put(name, logger);
			}
		}
		return logger;
	}

	/**
	 * 设置日志级别
	 * @param level
	 */
	public synchronized void setRootLevel(LogLevel level) {
		this.rootLevel = level;
		refreshLoggersLevel(null);
	}

	public synchronized void putSubLevel(String name, LogLevel level, LogOutput output, MarkerFilter... filters) {
		List<LogFilter> list = new ArrayList<>();
		for (MarkerFilter filter: filters) {
			LogFilter a = new LogFilter(filter);
			a.output = output;
			list.add(a);
		}
		LinkChain<LogFilter> c = LinkChain.fromList(list);

		boolean added = false;
		for (SubLevel t: subLevels) {
			if (t.name.equals(name)) {
				t.level = level;
				t.output = output;
				t.filterChain = c;
				added = true;
			}
		}
		if (!added) {
			SubLevel s = new SubLevel();
			s.name = name;
			s.level = level;
			s.output = output;
			s.filterChain = c;

			List<SubLevel> newLevels = cloneSubLevels();
			newLevels.add(s);
			this.subLevels = newLevels;
		}
		refreshLoggersLevel(name);
	}

	public void setLevels(LogLevel rootLevel, List<LogFilter> rootFilters, Map<String, SubLevel> levels) {
		this.rootLevel = rootLevel;
		this.rootFilter = LinkChain.fromList(rootFilters);
		List<SubLevel> newLevels = new ArrayList<>();
		levels.forEach((k, v) -> {
			v.filterChain = LinkChain.fromList(v.filters);
			newLevels.add(v);
		});
		this.subLevels = newLevels;
		refreshLoggersLevel(null);
	}

	public synchronized void removeSubLevel(String name) {
		List<SubLevel> newLevels = cloneSubLevels();
		newLevels.removeIf(t -> t.name.equals(name));
		this.subLevels = newLevels;
		refreshLoggersLevel(name);
	}

	private List<SubLevel> cloneSubLevels() {
		List<SubLevel> levels = new ArrayList<>();
		for (SubLevel subLevel: subLevels) {
			levels.add(subLevel);
		}
		return levels;
	}

	private SubLevel getLevel(String name) {
		SubLevel hit = null;
		for (SubLevel t: subLevels) {
			if (name.startsWith(t.name)) {
				if (hit == null || hit.name.length() < t.name.length()) {
					hit = t;    // hig longest match
				}
			}
		}
		return hit;
	}

	private void refreshLoggersLevel(String root) {
		loggers.forEach((k, v) -> {
			if (root == null || k.startsWith(root)) {
				SubLevel n = getLevel(k);
				if (n == null) {
					v.setLevel(rootLevel);
					v.setFilterChain(rootFilter);
					v.setOutput(null);
				} else {
					v.setLevel(n.level);
					v.setOutput(n.output);
					v.setFilterChain(n.filterChain);
				}
			}
		});
	}

	public static class SubLevel {
		String name;
		LogLevel level;
		LogOutput output;
		LinkChain<LogFilter> filterChain;
		List<LogFilter> filters = new ArrayList<>();
	}
}

//
//
//
//
//
//
//	/**
//	 * 日志的配置信息
//	 */
//	private static LoggingConfig config = null;
//
//	/**
//	 * 日志数量计数器
//	 */
//	private int size = 0;
//
//	/**
//	 * 根日志节点
//	 */
//	LoggerImpl rootLogger;
//
//	/**
//	 * 是否启用缓存，默认不启用
//	 */
//	private boolean isEnableCache = false;
//
//	/**
//	 * 日志的appender集合
//	 */
//	private AppenderAttachable appenderAttachable;
//
//	/**
//	 * 这是一个单例类
//	 */
//	public static final LoggerFactoryImpl INSTANCE = new LoggerFactoryImpl();
//
//	/**
//	 * 私有的构造器，是此类为单例模式
//	 */
//	private LoggerFactoryImpl() {
//	}
//
//	static {
//		INSTANCE.initialize();
//	}
//
//	/**
//	 * 为首次使用做初始化
//	 */
//	private void initialize() {
//		//
//		// 初次使用LoggerContext，首先进行初始化
//		this.loggerCache = new HashMap<String, LoggerImpl>();
//		this.rootLogger = new LoggerImpl(LoggerImpl.ROOT_LOGGER_NAME, null, this);
//		this.rootLogger.setLevel(LogLevel.INFO);
//		loggerCache.put(LoggerImpl.ROOT_LOGGER_NAME, rootLogger);
//		size = 1;
//
//		try {
//			loadSettings();
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException("Load logging.xml failed.", e);
//		}
//	}
//
//	public LoggerImpl getLogger(final Class<?> clazz) {
//		return getLogger(clazz.getName());
//	}
//
//	/**
//	 * 根据日志名称获取日志对象
//	 *
//	 * @param name
//	 *            日志名称
//	 */
//	public LoggerImpl getLogger(final String name) {
//		if (StringUtils.isNullOrEmpty(name)) {
//			throw new IllegalArgumentException("name argument cannot be null");
//		}
//
//		if (LoggerImpl.ROOT_LOGGER_NAME.equalsIgnoreCase(name)) {
//			return rootLogger;
//		}
//
//		int i = 0;
//		LoggerImpl logger = rootLogger;
//
//		// check if the desired logger exists, if it does, return it
//		// without further ado.
//		LoggerImpl childLogger = (LoggerImpl) loggerCache.get(name);
//		// if we have the child, then let us return it without wasting time
//		if (childLogger != null) {
//			return childLogger;
//		}
//
//		// if the desired logger does not exist, them create all the loggers
//		// in between as well (if they don't already exist)
//		String childName;
//		while (true) {
//			int h = LoggerImpl.getSeparatorIndexOf(name, i);
//			if (h == -1) {
//				childName = name;
//			} else {
//				childName = name.substring(0, h);
//			}
//			// move i left of the last point
//			i = h + 1;
//			synchronized (logger) {
//				childLogger = logger.getChildByName(childName);
//				if (childLogger == null) {
//					childLogger = logger.createChildByName(childName);
//					loggerCache.put(childName, childLogger);
//					incSize();
//				}
//			}
//			logger = childLogger;
//			if (h == -1) {
//				return childLogger;
//			}
//		}
//	}
//
//	/**
//	 * 为了给所创建的日志计数，不过暂时没有用，呵呵
//	 */
//	private void incSize() {
//		size++;
//	}
//
//	int size() {
//		return size;
//	}
//
//	/**
//	 * 加载日志配置信息
//	 *
//	 * @param props
//	 *            Properties类型文件
//	 * @throws FileNotFoundException
//	 */
//	public synchronized void loadSettings() throws FileNotFoundException {
//		// config = new LoggingConfig(ConfigUtils.getConfig("logging.xml"));
//		config = new LoggingConfig(ConfigUtils.getConfigAsStream("logging.xml"));
//
//		// 配置信息的日志级别不为空时,设置rootLevel级别,否则是默认级别 INFO
//		if (config.getLevel() != null && !"".equals(config.getLevel())) {
//			this.rootLogger.setLevel(LogLevel.valueOf(config.getLevel().toUpperCase()));
//		}
//		setFilter(this.rootLogger, config.getFilterList());
//
//		//
//		// 使用XML中存储的子日志等级进行设置
//		if (config.getLoggerList() != null) {
//			List<LoggerNode> loggerList = config.getLoggerList();
//			for (LoggerNode loggerNode : loggerList) {
//				Boolean isMultiple = loggerNode.getIsMultiple();
//				if (!isMultiple) {
//					continue;
//				}
//				String keyString = loggerNode.getKey().replaceAll("/", ".");
//				String level = loggerNode.getLevel();
//				List<FilterNode> filterList = loggerNode.getFilterList();
//
//				LoggerImpl logger = loggerCache.get(keyString);
//				if (logger == null) {
//					logger = this.getLogger(keyString);
//					loggerCache.put(keyString, logger);
//				}
//				// 设置子日志等级
//				logger.setLevel(LogLevel.valueOf(level.toUpperCase()));
//				// 设置子日志Filter
//				setFilter(logger, filterList);
//			}
//		}
//
//		// 管理appender
//		if (appenderAttachable == null) {
//			appenderAttachable = new AppenderAttachable();
//		} else {
//			appenderAttachable.removeAllAppender();
//			appenderAttachable.closeQueue();
//		}
//
//		// 设置日志缓存
//		setEnableCache(config.getCache());
//
//		List<AppenderNode> appenders = config.getAppenderList();
//
//		for (AppenderNode appender : appenders) {
//			try {
//				if (appender.getEnabled()) {
//					LogAppender app = appender.get();
//					app.setConfig(appender.getValues());
//					appenderAttachable.addAppender(app);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//
//	}
//
//	/**
//	 * 设置缓存的启用与否
//	 *
//	 * @param cache
//	 */
//	public void setEnableCache(CacheNode cache) {
//		this.isEnableCache = cache.getEnabled();
//		if (isEnableCache) {
//			// 如果设置缓存了，就初始化一个有缓存特性的队列
//			appenderAttachable.initCacheQueue(cache.getBatchCount(), cache.getLazyMs());
//		} else {
//			// 如果没有启用缓存，那么初始化一个可以及时响应时间的队列，但是此队列也要有一定的长度限制，否则无限的膨胀会造成内存溢出
//			appenderAttachable.initSyncQueue();
//		}
//	}
//
//	public void setFilter(LoggerImpl logger, List<FilterNode> filters) {
//		if (filters != null && filters.size() > 0) {
//			Filter filter = null;
//			for (FilterNode filterNode : filters) {
//				Boolean isMultiple = filterNode.getIsMultiple();
//				if (!isMultiple) {
//					continue;
//				}
//				String filterClass = filterNode.getClassName().replaceAll("/", ".");
//				String level = filterNode.getLevel();
//				if (filter == null) {
//					filter = ClassUtils.newClassInstance(Filter.class, filterClass);
//					if (level != null && level.length() > 0) {
//						filter.setLevel(LogLevel.valueOf(level.toUpperCase()));
//					}
//				} else {
//					Filter filterTemp = ClassUtils.newClassInstance(Filter.class, filterClass);
//					if (level != null && level.length() > 0) {
//						filterTemp.setLevel(LogLevel.valueOf(level.toUpperCase()));
//					}
//					filter.setNextFilter(filterTemp);
//				}
//			}
//			logger.setFilter(filter);
//		}
//	}
//
//	/**
//	 * 得到管理Appender的对象
//	 *
//	 * @return
//	 */
//	public AppenderAttachable getAppenderAttachable() {
//		return appenderAttachable;
//	}
// }
