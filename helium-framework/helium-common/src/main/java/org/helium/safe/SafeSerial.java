package org.helium.safe;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SafeSerial {

	/**
	 * 获取网卡信息
	 * @return
	 * @throws Exception
	 */
	public static String getMACAddressByLinux() {
		String result = "";
		try {
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
			result = str2.split("txqueuelen")[0].trim();
			br.close();
		} catch (Exception e){

		}


		return result;
	}

	/**
	 * 获取本机CPU信息
	 */
	public static String getCPUSerial() {
		String result = "";
		try {
			Process process = Runtime.getRuntime().exec("sudo dmidecode -s system-uuid");
			InputStream in;
			BufferedReader br;
			in = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(in));
			while (in.read() != -1) {
				result = br.readLine();
			}
			br.close();
			in.close();
			process.destroy();
		} catch (Throwable e) {
			result = "0000000-0000-0000-0000-000000000000";
		}
		return result;
	}
}
