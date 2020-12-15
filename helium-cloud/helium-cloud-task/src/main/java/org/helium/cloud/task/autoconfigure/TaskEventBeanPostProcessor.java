package org.helium.cloud.task.autoconfigure;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * 处理TaskEvent注解
 */
public class TaskEventBeanPostProcessor implements BeanPostProcessor {

	@Autowired
	private TaskEventBeanHandler taskEventBeanHandler;

	/**
	 * for springboot  support helium
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		taskEventBeanHandler.processTaskEvent(bean, beanName);
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

}
