package org.helium.boot.spring.annotation.processor;

import org.helium.boot.spring.assembly.HeliumAssembly;
import org.helium.util.StringUtils;
import org.helium.framework.BeanContext;
import org.helium.framework.annotations.ServiceInterface;
import org.helium.framework.annotations.ServiceSetter;
//import org.helium.framework.spi.Bootstrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;

public class ServiceSetterBeanPostProcessor implements BeanPostProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceSetterBeanPostProcessor.class);

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
				ServiceSetter serviceSetter = field.getAnnotation(ServiceSetter.class);
				if (serviceSetter != null) {
					String sid = serviceSetter.id();
					if (StringUtils.isNullOrEmpty(sid)){
						ServiceInterface sif = field.getType().getAnnotation(ServiceInterface.class);
						if (sif != null){
							sid = sif.id();
						}
					}
					if (StringUtils.isNullOrEmpty(sid)){
						LOGGER.error("beanName process Error:{} and sid not be null", field);
						continue;
					}
					BeanContext beanContext = HeliumAssembly.INSTANCE.getBean(sid);
					if (beanContext == null){
						LOGGER.error("beanName process Error:{}-{} and beanContext is null", field, sid);
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
