package org.helium.framework.spring.utils;

import com.feinno.superpojo.util.FileUtil;

import java.io.File;
import java.io.IOException;

public class LicFile {
	public static final String LICENSE = ".LicIms";

	public static void write(String content) {
		File file = new File(LICENSE);
		if (file.exists()) {
			file.delete();

		}
		try {
			file.createNewFile();
		} catch (IOException e) {
			System.out.println("Create File Error:" + LICENSE);
		}
		FileUtil.write(content, LICENSE);

	}

	public static String read() {
		File file = new File(LICENSE);
		if (file.exists()) {
			return FileUtil.read(LICENSE);
		}
		return "license not install";

	}

	public static void delete() {
		File file = new File(LICENSE);
		if (file.exists()) {
			file.delete();

		}
	}

	public static void main(String[] args) {
		LicFile.write("111");
		System.out.println(LicFile.read());
	}
}
