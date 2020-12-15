/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-10
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package com.feinno.superpojo.util;

import com.feinno.superpojo.generator.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

/**
 * 
 * <b>描述: </b>系统环境相关变量获取
 * <p>
 * <b>功能: </b>用于装载系统环境相关的变量
 * <p>
 * <b>用法: </b>正常方法调用
 * <p>
 * 
 * @author 高磊 gaolei@feinno.com
 * 
 */
public class ServiceEnvironment {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEnvironment.class);

	/** 进程PID */
	private static int pid;

	/** 服务名称 */
	private static String serviceName;

	/** 计算机名称 */
	private static String computerName;

	/** 服务器公网地址 */
	public static final String PUBLIC_IP = "PUBLIC_IP";

	/** 服务器私网地址 */
	public static final String PRIVATE_IP = "PRIVATE_IP";

	static {
		//
		serviceName = ManagementFactory.getRuntimeMXBean().getName();
		pid = Integer.parseInt(serviceName.substring(0, serviceName.indexOf('@')));
		// 获取计算机名
		try {
			InetAddress addr = InetAddress.getLocalHost();
			computerName = addr.getHostName();
		} catch (UnknownHostException e) {
			LOGGER.error(e.getMessage());
		}
	}

	public static String getServiceName() {
		return serviceName;
	}

	public static String getComputerName() {
		return computerName;
	}

	public static void setServiceName(String serviceName) {
		ServiceEnvironment.serviceName = serviceName;
	}

	public static void setComputerName(String computerName) {
		ServiceEnvironment.computerName = computerName;
	}

	public static Integer getPid() {
		return pid;
	}

	/**
	 * 获得服务器公网地址
	 * 
	 * @return
	 */
	public static final String getPublicIP() {
		return getEnv(PUBLIC_IP);
	}

	/**
	 * 获得服务器私网地址
	 * 
	 * @return
	 */
	public static final String getPrivateIP() {
		return getEnv(PRIVATE_IP);
	}

	/**
	 * 获得服务器的环境变量
	 * 
	 * @param name
	 *            环境变量名称
	 * @return 环境变量内容
	 */
	public static final String getEnv(String name) {
		return System.getenv(name);
	}

	/**
	 * 获得全量环境配置
	 * 
	 * @return
	 */
	public static final Map<String, String> getEnv() {
		return System.getenv();
	}

	/**
	 * 设置环境变量内容到文本中
	 * 
	 * @param text
	 * @return
	 */
	public static final String setEnvToText(String text) {
		try {
			Configuration cfg = new Configuration();
			cfg.setTemplateLoader(new StringTemplateLoader(text));
			cfg.setDefaultEncoding("UTF-8");
			Template template = cfg.getTemplate("");
			StringWriter writer = new StringWriter();
			template.process(getEnv(), writer);
			text = writer.toString();
			return text;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws Exception {
		RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
		// Object[] objs = new Object[0];
		// for (Method m: mx.getClass().getMethods()) {
		// try {
		// System.out.printf("System.out.println(\"%s\" + mx.%s());\n",
		// m.getName(), m.getName());
		// } catch (Exception ex) {
		// System.out.println(m.getName() + ex.getMessage());
		// }
		// }
		System.out.println("--------------------------------------");
		System.out.println("getName: " + mx.getName());
		System.out.println("getClassPath: " + mx.getClassPath());
		// System.out.println("getObjectName: " + mx.getObjectName());
		System.out.println("getBootClassPath: " + mx.getBootClassPath());
		System.out.println("getLibraryPath: " + mx.getLibraryPath());
		System.out.println("getVmName: " + mx.getVmName());
		System.out.println("getVmVendor: " + mx.getVmVendor());
		System.out.println("getVmVersion: " + mx.getVmVersion());
		System.out.println("isBootClassPathSupported: " + mx.isBootClassPathSupported());
		System.out.println("getInputArguments: " + mx.getInputArguments());
		System.out.println("getManagementSpecVersion: " + mx.getManagementSpecVersion());
		System.out.println("getSpecName: " + mx.getSpecName());
		System.out.println("getSpecVendor: " + mx.getSpecVendor());
		System.out.println("getSpecVersion: " + mx.getSpecVersion());
		System.out.println("getStartTime: " + mx.getStartTime());
		System.out.println("getSystemProperties: " + mx.getSystemProperties());
		System.out.println("getUptime: " + mx.getUptime());
		System.out.println("--------------------------------------");

		System.out.println("ServiceName:" + serviceName);
		System.out.println("Pid:" + serviceName);
		System.out.println("Computer:" + serviceName);
		System.out.println("Class:" + ServiceEnvironment.class.getCanonicalName());

		System.out.println("==============================");
		// List<com.sun.tools.attach.VirtualMachineDescriptor> vms =
		// com.sun.tools.attach.VirtualMachine.list();
		// for (com.sun.tools.attach.VirtualMachineDescriptor vm: vms) {
		// System.out.println("vm:" + vm.id());
		// }

		InetAddress addr = InetAddress.getLocalHost();
		System.out.println("Computer:" + serviceName);
		System.out.println("Address:" + addr);
		Thread.sleep(30 * 1000);
	}
}
