package org.helium.logging.spi;


import org.helium.logging.FilterResult;
import org.helium.logging.LogLevel;
import org.helium.util.IntegerReference;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

/**
 * 日志记录实现类
 *
 * @author Lv.Mingwei
 * @author coral
 * @author Gao Lei
 */
public class LoggerImpl implements org.slf4j.Logger {
	private String name;
	private LogLevel level;
	private LogOutput output;
	private LinkChain<LogFilter> filterChain;
	private MarkerTag defaultTag;

	public LoggerImpl(String name, LogLevel level) {
		this.name = name;
		this.level = level;
		this.defaultTag = new MarkerTag();
	}

	public LogLevel getLevel() {
		return level;
	}

	public void setLevel(LogLevel level) {
		this.level = level;
	}

	public void setOutput(LogOutput output) {
		this.output = output;
		this.defaultTag.output = output;
	}

	public void setFilterChain(LinkChain<LogFilter> chain) {
		this.filterChain = chain;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isTraceEnabled() {
		return LogLevel.TRACE.canLog(level);
	}

	@Override
	public boolean isDebugEnabled() {
		return LogLevel.DEBUG.canLog(level);
	}

	@Override
	public boolean isInfoEnabled() {
		return LogLevel.INFO.canLog(level);
	}

	@Override
	public boolean isWarnEnabled() {
		return LogLevel.WARN.canLog(level);
	}

	@Override
	public boolean isErrorEnabled() {
		return LogLevel.ERROR.canLog(level);
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return isLogEnabled2(LogLevel.TRACE, marker);
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return isLogEnabled2(LogLevel.DEBUG, marker);
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return isLogEnabled2(LogLevel.INFO, marker);
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return isLogEnabled2(LogLevel.WARN, marker);
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return isLogEnabled2(LogLevel.ERROR, marker);
	}

	@Override
	public void trace(String message) {
		if (isTraceEnabled()) {
			innerLog(LogLevel.TRACE, message, null);
		}
	}

	@Override
	public void trace(String format, Object arg1) {
		if (isTraceEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog(LogLevel.TRACE, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		if (isTraceEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog(LogLevel.TRACE, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void trace(String format, Object[] arguments) {
		if (isTraceEnabled()) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog(LogLevel.TRACE, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void trace(String message, Throwable t) {
		if (isTraceEnabled()) {
			innerLog(LogLevel.TRACE, message, t);
		}
	}

	@Override
	public void trace(Marker marker, String message) {
		MarkerTag m = filterMarker(LogLevel.TRACE, marker);
		if (m != null) {
			innerLog2(m, LogLevel.TRACE, message, null);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg1) {
		MarkerTag m = filterMarker(LogLevel.TRACE, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog2(m, LogLevel.TRACE, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void trace(Marker marker, String format, Object[] arguments) {
		MarkerTag m = filterMarker(LogLevel.TRACE, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog2(m, LogLevel.TRACE, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void trace(Marker marker, String message, Throwable t) {
		MarkerTag m = filterMarker(LogLevel.TRACE, marker);
		if (m != null) {
			innerLog2(m, LogLevel.TRACE, message, t);
		}
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		MarkerTag m = filterMarker(LogLevel.TRACE, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog2(m, LogLevel.TRACE, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void debug(String message) {
		if (isDebugEnabled()) {
			innerLog(LogLevel.DEBUG, message, null);
		}
	}

	@Override
	public void debug(String format, Object arg1) {
		if (isDebugEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog(LogLevel.DEBUG, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		if (isDebugEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog(LogLevel.DEBUG, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void debug(String format, Object[] arguments) {
		if (isDebugEnabled()) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog(LogLevel.DEBUG, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void debug(String message, Throwable t) {
		if (isDebugEnabled()) {
			innerLog(LogLevel.DEBUG, message, t);
		}
	}

	@Override
	public void debug(Marker marker, String message) {
		MarkerTag m = filterMarker(LogLevel.DEBUG, marker);
		if (m != null) {
			innerLog2(m, LogLevel.DEBUG, message, null);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg1) {
		MarkerTag m = filterMarker(LogLevel.DEBUG, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog2(m, LogLevel.DEBUG, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void debug(Marker marker, String format, Object[] arguments) {
		MarkerTag m = filterMarker(LogLevel.DEBUG, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog2(m, LogLevel.DEBUG, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void debug(Marker marker, String message, Throwable t) {
		MarkerTag m = filterMarker(LogLevel.DEBUG, marker);
		if (m != null) {
			innerLog2(m, LogLevel.DEBUG, message, t);
		}
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		MarkerTag m = filterMarker(LogLevel.DEBUG, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog2(m, LogLevel.DEBUG, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void info(String message) {
		if (isInfoEnabled()) {
			innerLog(LogLevel.INFO, message, null);
		}
	}

	@Override
	public void info(String format, Object arg1) {
		if (isInfoEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog(LogLevel.INFO, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		if (isInfoEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog(LogLevel.INFO, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void info(String format, Object[] arguments) {
		if (isInfoEnabled()) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog(LogLevel.INFO, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void info(String message, Throwable t) {
		if (isInfoEnabled()) {
			innerLog(LogLevel.INFO, message, t);
		}
	}


	@Override
	public void info(Marker marker, String message) {
		MarkerTag m = filterMarker(LogLevel.INFO, marker);
		if (m != null) {
			innerLog2(m, LogLevel.INFO, message, null);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg1) {
		MarkerTag m = filterMarker(LogLevel.INFO, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog2(m, LogLevel.INFO, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void info(Marker marker, String format, Object[] arguments) {
		MarkerTag m = filterMarker(LogLevel.INFO, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog2(m, LogLevel.INFO, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void info(Marker marker, String message, Throwable t) {
		MarkerTag m = filterMarker(LogLevel.INFO, marker);
		if (m != null) {
			innerLog2(m, LogLevel.INFO, message, t);
		}
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		MarkerTag m = filterMarker(LogLevel.INFO, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog2(m, LogLevel.INFO, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(String message) {
		if (isWarnEnabled()) {
			innerLog(LogLevel.WARN, message, null);
		}
	}

	@Override
	public void warn(String format, Object arg1) {
		if (isWarnEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog(LogLevel.WARN, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		if (isWarnEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog(LogLevel.WARN, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(String format, Object[] arguments) {
		if (isWarnEnabled()) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog(LogLevel.WARN, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(String message, Throwable t) {
		if (isWarnEnabled()) {
			innerLog(LogLevel.WARN, message, t);
		}
	}

	@Override
	public void warn(Marker marker, String message) {
		MarkerTag m = filterMarker(LogLevel.WARN, marker);
		if (m != null) {
			innerLog2(m, LogLevel.WARN, message, null);
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg1) {
		MarkerTag m = filterMarker(LogLevel.WARN, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog2(m, LogLevel.WARN, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		MarkerTag m = filterMarker(LogLevel.WARN, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog2(m, LogLevel.WARN, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(Marker marker, String format, Object[] arguments) {
		MarkerTag m = filterMarker(LogLevel.WARN, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog2(m, LogLevel.WARN, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void warn(Marker marker, String message, Throwable t) {
		MarkerTag m = filterMarker(LogLevel.WARN, marker);
		if (m != null) {
			innerLog2(m, LogLevel.WARN, message, t);
		}
	}

	@Override
	public void error(String message) {
		if (isErrorEnabled()) {
			innerLog(LogLevel.ERROR, message, null);
		}
	}

	@Override
	public void error(String format, Object arg1) {
		if (isErrorEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog(LogLevel.ERROR, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		if (isErrorEnabled()) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog(LogLevel.ERROR, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void error(String format, Object[] arguments) {
		if (isErrorEnabled()) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog(LogLevel.ERROR, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void error(String message, Throwable t) {
		if (isErrorEnabled()) {
			innerLog(LogLevel.ERROR, message, t);
		}
	}

	@Override
	public void error(Marker marker, String message) {
		MarkerTag m = filterMarker(LogLevel.ERROR, marker);
		if (m != null) {
			innerLog2(m, LogLevel.ERROR, message, null);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg1) {
		MarkerTag m = filterMarker(LogLevel.ERROR, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1);
			innerLog2(m, LogLevel.ERROR, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void error(Marker marker, String format, Object[] arguments) {
		MarkerTag m = filterMarker(LogLevel.ERROR, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.arrayFormat(format, arguments);
			innerLog2(m, LogLevel.ERROR, tp.getMessage(), tp.getThrowable());
		}
	}

	@Override
	public void error(Marker marker, String message, Throwable t) {
		MarkerTag m = filterMarker(LogLevel.ERROR, marker);
		if (m != null) {
			innerLog2(m, LogLevel.ERROR, message, t);
		}
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		MarkerTag m = filterMarker(LogLevel.ERROR, marker);
		if (m != null) {
			FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
			innerLog2(m, LogLevel.ERROR, tp.getMessage(), tp.getThrowable());
		}
	}

	private boolean isLogEnabled2(LogLevel level, Marker marker) {
		if (filterChain != null && marker != null) {
			IntegerReference ir = new IntegerReference(0);
			filterChain.foreach(f -> {
				FilterResult fr = f.filter.filter(marker);
				switch (fr) {
					case ACCEPT:
						ir.value = 1;
						return false;
					case NEUTRAL:
						ir.value = level.canLog(this.level) ? 1 : 0;
						return ir.value == 0; // 如果存在多个Filter，只要有一个filter允许记录日志，就认为允许记录日志
					case DENY:
						ir.value = 0;
						return false;
				}
				return true;
			});
			return ir.value == 1;
		} else {
			return level.canLog(this.level);
		}
	}

	private MarkerTag filterMarker(LogLevel level, Marker marker) {
		if (filterChain != null && marker != null) {
			IntegerReference ir = new IntegerReference(0);
			Wrapper<MarkerTag> tag = new Wrapper<>();
			filterChain.foreach(f -> {
				tag.value = f.apply(marker);
				switch (tag.value.result) {
					case ACCEPT:
						return false;
					case NEUTRAL:
						// 如果存在多个Filter，只要有一个filter允许记录日志，就认为允许记录日志
						if (!level.canLog(this.level)) {
							tag.value = null;
							return true;
						} else {
							return false;
						}
					case DENY:
						tag.value = null;
						return false;
				}
				return true;
			});
			return tag.value;
		} else {
			return level.canLog(this.level) ? defaultTag : null;
		}
	}

	//
	// 直接记录日志
	private final void innerLog(LogLevel level, String message, Throwable error) {
//		LoggerCountListener.doCounter(counter, level, t);
		try {
			LogEvent event = new LogEvent(name, level, message, error, null, output);
			LogContext.INSTANCE.doLog(event);
		} catch (Exception ex) {
			System.err.print("innerLog failed:");
			ex.printStackTrace();
		}
	}

	//
	// 记录带Marker的地址
	private final void innerLog2(MarkerTag m, LogLevel level, String message, Throwable error) {
//		LoggerCountListener.doCounter(counter, level, t);
		try {
			LogEvent event = new LogEvent(name, level, message, error, m.marker, m.output);
			LogContext.INSTANCE.doLog(event);
		} catch (Exception ex) {
			System.err.print("innerLog2 failed:");
			ex.printStackTrace();
		}
	}
}


//	/**
//	 * 根据目录结构中的"."符号，找到对应的位置，如果点符号找不到，则使�?$'符号找位�?因为可能为内部类�?
//	 * 此方法主要是为了提供日志的树形结构方法做支持
//	 *
//	 * @param name
//	 * @param fromIndex
//	 * @return
//	 */
//	static int getSeparatorIndexOf(String name, int fromIndex) {
//		int i = name.indexOf(CoreConstants.DOT, fromIndex);
//		if (i != -1) {
//			return i;
//		} else {
//			return name.indexOf(CoreConstants.DOLLAR, fromIndex);
//		}
//	}
//	/**
//	 * 在当前节点下，查找是否有符合指定名称的子节点<br>
//	 * <code>logback</code>使用的是树形结构来存储日志对象，那么我们也采用了此结构来存储日志对象�?
//	 * 用于日后当有热更新配置功能时能够通过树形结构找到相应的节点及子节点进行更�?
//	 *
//	 * @param childName
//	 * @return
//	 */
//	LoggerImpl getChildByName(final String childName) {
//		if (children == null) {
//			return null;
//		} else {
//			int len = this.children.size();
//			for (int i = 0; i < len; i++) {
//				final LoggerImpl childLogger_i = (LoggerImpl) children.get(i);
//				final String childName_i = childLogger_i.getName();
//
//				if (childName.equals(childName_i)) {
//					return childLogger_i;
//				}
//			}
//			// no child found
//			return null;
//		}
//	}
//
//	/**
//	 * 创在当前节点下创建一个子节点 采用<code>logback</code>的思路采用了树形结�?
//	 *
//	 * @param childName
//	 * @return
//	 */
//	LoggerImpl createChildByName(final String childName) {
//		int i_index = getSeparatorIndexOf(childName, this.name.length() + 1);
//		if (i_index != -1) {
//			throw new IllegalArgumentException("For logger [" + this.name + "] child name [" + childName
//					+ " passed as parameter, may not include '.' after index" + (this.name.length() + 1));
//		}
//
//		if (children == null) {
//			children = new ArrayList<LoggerImpl>(DEFAULT_CHILD_ARRAY_SIZE);
//		}
//		LoggerImpl childLogger;
//		childLogger = new LoggerImpl(childName, this, this.loggerFactory);
//		children.add(childLogger);
//		childLogger.effectiveLevel = this.effectiveLevel;
//		childLogger.effectiveFilter = this.effectiveFilter;
//		return childLogger;
//	}
//
//	/**
//	 * 采用<code>logback</code>
//	 * 的思路采用了树形结构，因此在设置一个节点的等级后，会遍历它的子节点，将子节点的生效等级也相应的修改为此等级，当发现子节点有自己的配置等级时�?
//	 * 才停�?
//	 *
//	 * @param newLevel
//	 */
//	public synchronized void setLevel(LogLevel newLevel) {
//		if (level == newLevel) {
//			// nothing to do;
//			return;
//		}
//		if (newLevel == null && isRootLogger()) {
//			throw new IllegalArgumentException("The level of the root logger cannot be set to null");
//		}
//
//		level = newLevel;
//		if (newLevel == null) {
//			effectiveLevel = parent.effectiveLevel;
//		} else {
//			effectiveLevel = newLevel;
//		}
//
//		if (children != null) {
//			int len = children.size();
//			for (int i = 0; i < len; i++) {
//				LoggerImpl child = (LoggerImpl) children.get(i);
//				// tell child to handle parent levelInt change
//				child.handleParentLevelChange(effectiveLevel);
//			}
//		}
//	}
//
//	/**
//	 * 如果父节点的日志等级有变更，会递归的变更其子节点的日志等级，直到找到子节点的真实等级不为空的情�?br>
//	 * 此方法与setLevel方法相对应，是由setLevel方法调入进来
//	 *
//	 * @param newParentLevelInt
//	 */
//	private synchronized void handleParentLevelChange(LogLevel newParentLevelInt) {
//		// changes in the parent levelInt affect children only if their levelInt
//		// is
//		// null
//		if (level == null) {
//			effectiveLevel = newParentLevelInt;
//
//			// propagate the parent levelInt change to this logger's children
//			if (children != null) {
//				int len = children.size();
//				for (int i = 0; i < len; i++) {
//					LoggerImpl child = (LoggerImpl) children.get(i);
//					child.handleParentLevelChange(newParentLevelInt);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 与等级的设置很类似，此处是设置一个Filter
//	 *
//	 * @param newFilter
//	 */
//	public synchronized void setFilter(Filter newFilter) {
//		if (filter == newFilter) {
//			// nothing to do;
//			return;
//		}
//
//		filter = newFilter;
//		if (newFilter == null) {
//			effectiveFilter = parent.effectiveFilter;
//		} else {
//			effectiveFilter = newFilter;
//		}
//
//		if (children != null) {
//			int len = children.size();
//			for (int i = 0; i < len; i++) {
//				LoggerImpl child = (LoggerImpl) children.get(i);
//				// tell child to handle parent filter change
//				child.handleParentFilterChange(effectiveFilter);
//			}
//		}
//	}
//
//	/**
//	 * 如果父节点的日志Filter有变更，会递归的变更其子节点的Filter，直到找到子节点的真实Filter不为空的情况<br>
//	 * 此方法与setFilter方法相对应，是由setFilter方法调入进来
//	 *
//	 * @param newParentFilterInt
//	 */
//	private synchronized void handleParentFilterChange(Filter newParentFilterInt) {
//		// changes in the parent filter affect children only if their levelInt
//		// is
//		// null
//		if (level == null) {
//			effectiveFilter = newParentFilterInt;
//
//			// propagate the parent filter change to this logger's children
//			if (children != null) {
//				int len = children.size();
//				for (int i = 0; i < len; i++) {
//					LoggerImpl child = (LoggerImpl) children.get(i);
//					child.handleParentFilterChange(newParentFilterInt);
//				}
//			}
//		}
//	}
//
//	/**
//	 * 判断是否是根节点
//	 *
//	 * @return
//	 */
//	private final boolean isRootLogger() {
//		return parent == null;
//	}
//
//	/**
//	 * 通知appender可以开始工作了
//	 *
//	 * @param event
//	 *            The event to log
//	 */
//	public void callAppenders(LogEvent event) {
//		// 不同于logback一样遍历所有节点的appender，我们只需要调用根节点的一个既�?
//		loggerFactory.rootLogger.appendLoopOnAppenders(event);
//	}
//
//	/**
//	 * 发消息给appender，写日志
//	 *
//	 * @param event
//	 * @return
//	 */
//	private void appendLoopOnAppenders(LogEvent event) {
//		if (loggerFactory.getAppenderAttachable() != null) {
//			loggerFactory.getAppenderAttachable().appendLoopOnAppenders(event);
//		}
//	}
//
//	public final Filter getFilter() {
//		return effectiveFilter;
//	}
// 当前节点下的子节点
//	private List<LoggerImpl> children;
//
//	// 当前日志节点的父节点
//	private LoggerImpl parent;
//
//	// 当前日志名称
//	private String name;
//
//	// 日志的建造工工厂类，保存它的引用的目的是它会拥有比较丰富的资源
//	private LoggerFactoryImpl loggerFactory;
//
//	// 日志等级，可为空null
//	private LogLevel level;
//
//	// 日志的生效等级
//	public LogLevel effectiveLevel;
//
//	// 日志Filter，可为空null
//	private Filter filter;
//
//	// 日志的生效等级
//	public Filter effectiveFilter;
//
//	private static final int DEFAULT_CHILD_ARRAY_SIZE = 5;
//
//	public LoggerImpl(String name, LoggerImpl parent, LoggerFactoryImpl loggerFactory) {
//		this.name = name;
//		this.parent = parent;
//		this.loggerFactory = loggerFactory;
//	}