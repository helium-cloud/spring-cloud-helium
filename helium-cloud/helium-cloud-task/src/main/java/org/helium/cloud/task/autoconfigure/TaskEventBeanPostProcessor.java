package org.helium.cloud.task.autoconfigure;


import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.task.TaskProducer;
import org.helium.framework.task.TaskProducerFactory;
import org.helium.logging.spi.SetterInjector;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.annotation.Resource;
import java.lang.reflect.Field;


public class TaskEventBeanPostProcessor implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskEventBeanPostProcessor.class);


    @Resource(name = HeliumTaskConfig.TASK_PRODUCER_FACTORY)
	private TaskProducerFactory taskProducerFactory;
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
        processFieldSetter(bean, beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        return bean;
    }


    public void processFieldSetter(Object bean, String beanName) {
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
                    String key = taskEvent.value();
                    if (StringUtils.isNullOrEmpty(key)) {
                        LOGGER.error("beanName process Error:{} and TaskProducer.value not be null", field);
                        continue;
                    }
					try {
						TaskProducer taskProducer = taskProducerFactory.getProducer(key);
						SetterInjector.setField(bean, field, taskProducer);
						LOGGER.warn("TaskProducer set:{}-{}.", field, key);
					} catch (Exception e) {
						LOGGER.error("TaskProducer set Error:{} ", field, e);
					}
                }
            }
        } catch (Exception e) {
            throw new BeanCreationException(beanName, e);
        }
    }



}
