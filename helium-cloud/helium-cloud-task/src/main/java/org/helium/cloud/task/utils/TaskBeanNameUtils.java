package org.helium.cloud.task.utils;

public class TaskBeanNameUtils {
	public static String getBeanInstance(String name){
		return name;
	}

	public static String getBeanImpl(String name){
		return name + ":exc";
	}

}
