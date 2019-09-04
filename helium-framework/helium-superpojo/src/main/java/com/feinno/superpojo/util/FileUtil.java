package com.feinno.superpojo.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * <b>描述: </b>用于序列化时使用的辅助类,很简单的读取和写入文件的代码，序列化稳定运行后此类不应该再出现
 * <p>
 * <b>功能: </b>很简单的读取和写入文件的代码
 * <p>
 * <b>用法: </b>很简单的读取和写入文件的代码
 * <p>
 * 
 * @author Lv.Mingwei
 * 
 */
public class FileUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	public static String read(String path) {
		return read(new File(path));
	}

	public static String read(File file) {
		StringBuilder content = new StringBuilder();
		BufferedReader reader = null;
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(file);
			reader = new BufferedReader(fileReader);
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
		} catch (Exception e) {
			LOGGER.error("Read file error.", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					LOGGER.error("Close fileReader error", e);
				}

			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error("Close BufferedReader error", e);
				}
			}

		}
		return content.toString();
	}

	public static String readLine(String path, int lineNumber) {
		BufferedReader reader = null;
		FileReader fileReader = null;
		int count = 0;
		try {
			fileReader = new FileReader(path);
			reader = new BufferedReader(fileReader);
			String line;
			while ((line = reader.readLine()) != null) {
				if (++count == lineNumber) {
					return line;
				}
			}
		} catch (Exception e) {
			LOGGER.error("Read file error.", e);
			e.printStackTrace();
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					LOGGER.error("Close fileReader error", e);
				}

			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					LOGGER.error("Close BufferedReader error", e);
				}
			}

		}
		return "";
	}

	public static void write(String source, String path) {
		FileWriter fw = null;
		try {
			fw = new FileWriter(path);
			fw.write(source);
		} catch (Exception e) {
			LOGGER.error("Write file error", e);
		} finally {
			try {
				fw.flush();
				fw.close();
			} catch (IOException e) {
				LOGGER.error("Flush or Close FileWriter error", e);
			}
		}
	}

}
