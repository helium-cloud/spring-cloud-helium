package org.helium.boot.spring.annotation.processor;

import org.helium.boot.spring.assembly.HeliumAssembly;
import org.helium.util.StringUtils;
import org.helium.framework.BeanContext;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.annotations.TaskImplementation;
//import org.helium.framework.spi.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class TaskEventBeanPostProcessor implements BeanPostProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(TaskEventBeanPostProcessor.class);

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
		if (!HeliumAssembly.INSTANCE.isComplete()){
			return bean;
		}
		Class<?> objClz;
		if (AopUtils.isAopProxy(bean)) {
			objClz = AopUtils.getTargetClass(bean);
		} else {
			objClz = bean.getClass();
		}

		try {
			for (Field field : objClz.getDeclaredFields()) {
				TaskEvent taskEvent = field.getAnnotation(TaskEvent.class);
				if (taskEvent != null) {
					String taskId = taskEvent.value();
					if (StringUtils.isNullOrEmpty(taskId)){
						TaskImplementation taskImplementation = field.getType().getAnnotation(TaskImplementation.class);
						if (taskImplementation != null){
							taskId = taskImplementation.id();
						}
					}
					if (StringUtils.isNullOrEmpty(taskId)){
						LOGGER.error("process Error:{} and taskId not be null", field);
						continue;
					}
					BeanContext beanContext = HeliumAssembly.INSTANCE.getBean(taskId);
					if (beanContext == null){
						LOGGER.error("process Error:{}-{} and beanContext is null", field, taskId);
						continue;
					}
					field.setAccessible(true);
					field.set(bean, beanContext.getBean());
				}
			}
		} catch (Exception e) {
			throw new BeanCreationException(beanName, e);
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}
}
