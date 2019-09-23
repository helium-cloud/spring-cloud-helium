package org.helium.cloud.task.utils;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.TaskInstance;

public class TaskBeanUtils {
	public static String getBeanInstance(String name){
		return name;
	}

	public static String getBeanImpl(String name){
		return name + ":exc";
	}

	public static TaskInstance getTaskInstance(String beanName) {
		TaskInstance taskBeanInstance = SpringContextUtil.getBean(TaskBeanUtils.getBeanInstance(beanName), TaskInstance.class);
		taskBeanInstance.setBean(SpringContextUtil.getBean(TaskBeanUtils.getBeanImpl(beanName)));
		return taskBeanInstance;
	}

}
