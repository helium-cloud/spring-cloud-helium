package org.helium.logging.spi;

import org.helium.logging.LogAppender;
import org.helium.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Coral on 8/31/15.
 */
public class TextAppender implements LogAppender {
	private String path;
	private String dateFormat = "yyyyMMdd_HH";
	private String fileFormat = "LOG_${DATE}.log";
	private String encoding = "UTF-8";

	public TextAppender() {
		dateFormatObject = new SimpleDateFormat(dateFormat);
	}

	@Override
	public boolean needQueue() {
		return true;
	}

	@Override
	public void open() {
		dateFormatObject = new SimpleDateFormat(dateFormat);
		if (StringUtils.isNullOrEmpty(path)) {
			path = System.getProperty("user.dir");
		} else {
			path = StringUtils.trimEnd(path, File.separatorChar);
		}
		File folder = new File(path);
		if (!folder.exists()) {
			folder.mkdirs();
		}
	}

	@Override
	public void close() {
	}

	@Override
	public void writeLog(LogEvent event) throws IOException {
		PrintStream out = getOutputStream();
		ConsoleAppender.printContent(out, event);
	}

	private DateFormat dateFormatObject;
	private PrintStream outStream;
	private String dateString;

	public PrintStream getOutputStream() throws IOException {
		Date now = new Date();
		String s = dateFormatObject.format(now);
		if (s.equals(dateString)) {
			return outStream;
		} else {
			if (outStream != null) {
				outStream.close();
			}
			dateString = s;
			String filename = path + "/" + fileFormat.replace("${DATE}", dateString);
			FileOutputStream fs = new FileOutputStream(filename, true);
			outStream = new PrintStream(fs, true, encoding);
			return outStream;
		}
	}

	public static void main(String[] args) {
		DateFormat format = new SimpleDateFormat("yyyyMMdd_HH");
		System.out.println(format.format(new Date()));
	}
}
