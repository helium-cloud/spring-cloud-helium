package org.helium.logging;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * Created by chenxuwu on 2017/8/31.
 */
public class EnvUtil {

	private final static String SERVICE_NAME = "SERVICENAME";
	private static String serviceName;
	private static String computerName;
	private static Integer pid;

	static {
		String serviceId = ManagementFactory.getRuntimeMXBean().getName();
		pid = Integer.parseInt(serviceId.substring(0, serviceId.indexOf('@')));

		//set ServiceName
		Map<String, String> envs = System.getenv();
		if(envs.containsKey(SERVICE_NAME)){
			serviceName = envs.get(SERVICE_NAME);
		}else{
			String currentWorkDirPath = System.getProperty("user.dir");

			String OS = System.getProperty("os.name").toLowerCase();
			String[] split = null;
			if (OS.indexOf("win") >= 0) {
				split = currentWorkDirPath.split("\\\\");
			}else{
				split = currentWorkDirPath.split("/");
			}
			serviceName = split[split.length-1];
		}

		//set ComputerName，存在dns丢失.so情况和dns没有配置情况
		try {
			computerName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
		}

		if(computerName == null){
			computerName = getSystemComputerName();
		}

		//如果通过读取/etc/host文件，或者/proc/sys/kernel/hostname 还有权限问题，操蛋
		//TODO 时间仓促，就设置一个localhost，这里存在陷阱
		if(computerName == null){
			computerName = "localhost";
		}
	}

	/**
	 * https://stackoverflow.com/questions/7348711/recommended-way-to-get-hostname-in-java
	 * @return
	 */
	private static String getSystemComputerName() {
		String computerName = null;
		String OS = System.getProperty("os.name").toLowerCase();
		if (OS.indexOf("win") >= 0) {
			computerName = System.getenv("COMPUTERNAME");
		}else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0) {
			computerName = System.getenv("HOSTNAME");
		}

		return computerName;
	}

	public static String getServiceName(){
		return serviceName;
	}

	public static String getComputerName(){
		return computerName;
	}

	public static Integer getPid(){ return pid; }
}
