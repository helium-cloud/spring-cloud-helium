package org.helium.cloud.task.utils;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.TaskBeanInstance;

public class TaskBeanUtils {
	public static String getBeanInstance(String name){
		return name;
	}

	public static String getBeanImpl(String name){
		return name + ":exc";
	}

	public static TaskBeanInstance getTaskInstance(String beanName) {
		TaskBeanInstance taskBeanInstance = SpringContextUtil.getBean(TaskBeanUtils.getBeanInstance(beanName), TaskBeanInstance.class);
		taskBeanInstance.setBean(SpringContextUtil.getBean(TaskBeanUtils.getBeanImpl(beanName)));
		return taskBeanInstance;
	}

}
