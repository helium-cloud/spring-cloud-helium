package org.helium.sample.boot.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DiskUtils {
	public static void main(String[] args) throws Exception {
		getMACAddressByLinux();
	}
	private static String getMACAddressByLinux() throws Exception {
		String[] cmd = {"ifconfig"};

		Process process = Runtime.getRuntime().exec(cmd);
		process.waitFor();

		BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		String str1 = sb.toString();
		String str2 = str1.split("ether")[1].trim();
		String result = str2.split("txqueuelen")[0].trim();
		System.out.println(result);
		br.close();

		return result;
	}

}
