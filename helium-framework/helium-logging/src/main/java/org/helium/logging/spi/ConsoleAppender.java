package org.helium.logging.spi;

import org.helium.logging.LogAppender;

import java.io.PrintStream;

/**
 * 日志输出的控制台类,提供将日志写入控制台的方法
 *
 *
 * Created by Coral
 */
public class ConsoleAppender implements LogAppender {
	public static final String ANSI_RESET = "\u001B[0m";
	public static final String ANSI_BLACK = "\u001B[30m";
	public static final String ANSI_RED = "\u001B[31m";
	public static final String ANSI_GREEN = "\u001B[32m";
	public static final String ANSI_YELLOW = "\u001B[33m";
	public static final String ANSI_BLUE = "\u001B[34m";
	public static final String ANSI_PURPLE = "\u001B[35m";
	public static final String ANSI_CYAN = "\u001B[36m";
	public static final String ANSI_WHITE = "\u001B[37m";

	private boolean ansiColor = true;
	private PrintStream out;
	private PrintStream error;

	public ConsoleAppender() {
		out = System.out;
		error = System.err;
	}

	ConsoleAppender(boolean ansiColor) {
		this();
		this.ansiColor = ansiColor;
	}

	@Override
	public void open() {
	}

	@Override
	public void close() {
		out.flush();
	}

	@Override
	public boolean needQueue() {
		return false;
	}

	@Override
	public void writeLog(LogEvent event) {
		if (ansiColor) {
			if (event.marker != null) {
				switch (event.level) {
					case TRACE:
					case DEBUG:
					case INFO:
						out.print(ANSI_CYAN);
						break;
					case WARN:
						out.print(ANSI_YELLOW);
						break;
					case ERROR:
						out.print(ANSI_RED);
						break;
				}
			} else {
				switch (event.level) {
					case TRACE:
						break;
					case DEBUG:
						break;
					case INFO:
						// out.print(ANSI_EX1);
						break;
					case WARN:
						out.print(ANSI_PURPLE);
						break;
					case ERROR:
						out.print(ANSI_RED);
						break;
				}
			}
		}
		printContent(out, event);

		if (ansiColor) {
			out.print(ANSI_RESET);
		}
	}

	/**
	 *
	 * @param out
	 * @param event
	 */
	static void printContent(PrintStream out, LogEvent event) {
		out.print(event.level.getFormat());
		out.print(event.timeString);
		out.print(" ");

		if (event.marker != null) {
			out.print("<");
			out.print(event.marker.getName());
			out.print("> ");
		}
		out.print(event.loggerName);
		out.print("/(");
		out.print(event.threadName);
		out.print("-");
		out.print(event.threadId);
		out.print("): ");

		printMessage(out, event.message);
		if (event.error != null) {
			printError(out, event.error);
		}
		out.println();
	}
	/**
	 * 如果是单行的消息，打印在单行上并消除最后回车
	 * 如果是折行信息，先打印回车并在后面折行打印
	 * @param out
	 * @param message
	 */
	static void printMessage(PrintStream out, String message) {
		int line = 0;
		out.print(message);
	}

	static void printError(PrintStream out, Throwable error) {
		out.println();
		out.print("\t" + LogUtils.formatError(error));
	}
}
/*

日志的输出格式
[TRACE] 2015-09-12 21:00:01.222 {MARKER} org.helium.logger.Logger:

[DEBUG] 2015-09-12 21:00:01.222 org.helium.sip.SipServlet/thread-id-11801:

[INFO ] 2015-09-12 21:00:01.222 org.helium.logger

[WARN ] 2015-09-12 21:00:01.222 HELIUM>org.helium.logger

[ERROR]



 */