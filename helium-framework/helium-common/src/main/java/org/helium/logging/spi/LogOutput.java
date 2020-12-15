package org.helium.logging.spi;

import org.helium.logging.LogAppender;
import org.helium.logging.LogLevel;
import org.helium.logging.LoggingConfiguration;
import org.helium.util.CollectionUtils;
import org.helium.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 集成
 * Created by Coral on 8/31/15.
 */
class LogOutput {
	public static final String DEFAULT_NAME = "DEFAULT";
	public static final int MAX_QUEUE_SIZE = 8192;
	public static final int SLEEP_INTERVAL = 10;
	public static final int DISCARD_WARNING_CYCLE = 10000;

	private String name;
	private int discardCount = 0;
	private LogAppender[] directAppenders = new LogAppender[0];
	private LogAppender[] queuedAppenders = new LogAppender[0];
	private ConcurrentLinkedDeque<LogEvent> queue = null;
	private Thread thread = null;
	private boolean running;

	public String getName() {
		return name;
	}

	public LogOutput(LoggingConfiguration.OutputNode node) {
		if (!StringUtils.isNullOrEmpty(node.getName())) {
			this.name = node.getName();
		} else {
			this.name = DEFAULT_NAME;
		}

		List<LogAppender> appenders = new ArrayList<>();
		for (LoggingConfiguration.AppenderNode appenderNode: node.getAppenders()) {
			LogAppender appender = (LogAppender) ObjectCreator.createObject(appenderNode.getClazz(), null, appenderNode.getSetters());
			appenders.add(appender);
		}
		initWithAppenders(appenders);
	}

	LogOutput(String name) {
		this.name = name;
	}

	void initWithAppenders(Iterable<LogAppender> appenders) {
		for (LogAppender appender : appenders) {
			try {
				appender.open();
				if (appender.needQueue()) {
					queuedAppenders = CollectionUtils.appendArray(queuedAppenders, appender);
				} else {
					directAppenders = CollectionUtils.appendArray(directAppenders, appender);
				}
			}catch (Exception ex){
				System.out.println("LogOutput-initWithAppenders:" + name + " process error");
			}
		}
	}

	public void start() {
		if (queuedAppenders.length > 0) {
			running = true;
			queue = new ConcurrentLinkedDeque<>();
			thread = new Thread(new Runnable() {
				@Override
				public void run() {
					dequeueThread();
				}
			});
			thread.setName("LogOutput-" + name);
			thread.setDaemon(true);
			thread.start();
		}
	}

	public void doLog(LogEvent event) {
		for (LogAppender appender: directAppenders) {
			writeLog(appender, event);
		}
		if (queue != null) {
			if (queue.size() < MAX_QUEUE_SIZE) {
				queue.add(event);
			} else {
				discardCount++;
				if (discardCount % DISCARD_WARNING_CYCLE == 0) {
					writeDiscardWarning();
				}
			}
		}
	}

	public void stop() {
		if (thread != null) {
			running = false;
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void dequeueThread() {
		while (running) {
			try {
				if (queue.isEmpty()) {
					Thread.sleep(SLEEP_INTERVAL);
				} else {
					LogEvent e = queue.poll();
					for (LogAppender appender: queuedAppenders) {
						writeLog(appender, e);
					}
				}
			} catch (InterruptedException e) {
			} catch (Exception ex) {
				System.out.println("LogOutput:" + name + " process error");
				ex.printStackTrace();
			}
		}
		flush();
	}

	private void flush() {
		while (queue.isEmpty()) {
			LogEvent e = queue.poll();
			for (LogAppender appender: queuedAppenders) {
				writeLog(appender, e);
			}
		}
	}

	private void writeLog(LogAppender appender, LogEvent e) {
		try {
			appender.writeLog(e);
		} catch (Exception ex) {
			System.err.println("LogOutput:" + name + " Failed:");
			ex.printStackTrace();
		}
	}

	private void writeDiscardWarning() {
		String msg = String.format("LogOutput<%s> discard:%d queue:%d", name, discardCount, queue.size());
		LogEvent event = new LogEvent(LOGGER_NAME, LogLevel.ERROR, msg, null, null, null);
		for (LogAppender appender: directAppenders) {
			writeLog(appender, event);
		}
		if (queue != null) {
			queue.addFirst(event); // 加到队列顶端
		}
	}

	private static final String LOGGER_NAME = LogOutput.class.getName();
}
