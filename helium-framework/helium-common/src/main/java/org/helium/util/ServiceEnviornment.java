/*
 * FAE, Feinno App Engine
 *  
 * Create by gaolei 2011-1-10
 * 
 * Copyright (c) 2011 北京新媒传信科技有限公司
 */
package org.helium.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * <b>描述: </b>系统环境相关变量获取
 * <p/>
 * <b>功能: </b>用于装载系统环境相关的变量
 * <p/>
 * <b>用法: </b>正常方法调用
 * <p/>
 *
 * Created by Coral
 */
public class ServiceEnviornment {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceEnviornment.class);

    private static int pid;
    private static String serviceName;
    private static String computerName;

    public static String getServiceName() {
        return serviceName;
    }

    public static String getComputerName() {
        return computerName;
    }

    public static void setServiceName(String serviceName) {
        ServiceEnviornment.serviceName = serviceName;
    }

    public static void setComputerName(String computerName) {
        ServiceEnviornment.computerName = computerName;
    }

    static {
        // 这里获取到的结果实际上是：processId@computerName
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

    public static Integer getPid() {
        return pid;
    }

//	public static void main(String[] args) throws Exception
//	{
//		RuntimeMXBean mx = ManagementFactory.getRuntimeMXBean();
//		System.out.println("--------------------------------------");
//		System.out.println("getName: " + mx.getName());
//		System.out.println("getClassPath: " + mx.getClassPath());
//		//System.out.println("getObjectName: " + mx.getObjectName());
//		System.out.println("getBootClassPath: " + mx.getBootClassPath());
//		System.out.println("getLibraryPath: " + mx.getLibraryPath());
//		System.out.println("getVmName: " + mx.getVmName());
//		System.out.println("getVmVendor: " + mx.getVmVendor());
//		System.out.println("getVmVersion: " + mx.getVmVersion());
//		System.out.println("isBootClassPathSupported: " + mx.isBootClassPathSupported());
//		System.out.println("getInputArguments: " + mx.getInputArguments());
//		System.out.println("getManagementSpecVersion: " + mx.getManagementSpecVersion());
//		System.out.println("getSpecName: " + mx.getSpecName());
//		System.out.println("getSpecVendor: " + mx.getSpecVendor());
//		System.out.println("getSpecVersion: " + mx.getSpecVersion());
//		System.out.println("getStartTime: " + mx.getStartTime());
//		System.out.println("getSystemProperties: " + mx.getSystemProperties());
//		System.out.println("getUptime: " + mx.getUptime());
//		System.out.println("--------------------------------------");
//
//		System.out.println("jmx:" + ObjectHelper.dumpObject(ManagementFactory.getRuntimeMXBean()));
//		System.out.println("ServiceName:" + serviceName);
//		System.out.println("Pid:" + serviceName);
//		System.out.println("Computer:" + serviceName);
//		System.out.println("Class:" + ServiceEnviornment.class.getCanonicalName());
//
//		System.out.println("==============================");
//		List<VirtualMachineDescriptor> vms = VirtualMachine.list();
//		for (VirtualMachineDescriptor vm: vms) {
//			System.out.println("vm:" + vm.id());
//		}
//
//		InetAddress addr = InetAddress.getLocalHost();
//		System.out.println("Computer:" + serviceName);System.out.println("Address:" + addr);
//		Thread.sleep(30 * 1000);
//	}
}
