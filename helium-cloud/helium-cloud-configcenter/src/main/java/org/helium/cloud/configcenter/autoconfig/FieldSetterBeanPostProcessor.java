package org.helium.cloud.configcenter.autoconfig;

import org.apache.dubbo.configcenter.ConfigChangeEvent;
import org.apache.dubbo.configcenter.ConfigurationListener;
import org.helium.cloud.configcenter.ConfigCenterClient;
import org.helium.cloud.configcenter.utils.LoaderUtils;
import org.helium.framework.annotations.FieldSetter;
import org.helium.framework.entitys.SetterNode;
import org.helium.framework.spi.SetterInjector;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Field;


public class FieldSetterBeanPostProcessor implements BeanPostProcessor {
	private static final Logger LOGGER = LoggerFactory.getLogger(FieldSetterBeanPostProcessor.class);


	@Autowired
	private ConfigCenterClient configCenterClient;

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
			setFieldClass(bean, objClz);
		} catch (Exception e) {
			throw new BeanCreationException(beanName, e);
		}
	}

	private void setFieldClass(Object bean, Class<?> objClz) {
		setField(bean, objClz);

		Class<?> superclass = objClz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			setFieldClass(bean, superclass);
		}
	}

	private void setField(Object bean, Class<?> objClz) {
		for (Field field : objClz.getDeclaredFields()) {
			FieldSetter fieldSetter = field.getAnnotation(FieldSetter.class);
			if (fieldSetter != null) {
				String key = fieldSetter.value();
				if (StringUtils.isNullOrEmpty(key)) {
					LOGGER.error("beanName process Error:{} and fieldSetter.value not be null", field);
					continue;
				}
				String value = configCenterClient.get(key, fieldSetter.group());
				if (!StringUtils.isNullOrEmpty(value)) {
					SetterNode setterNode = LoaderUtils.toSetNode(fieldSetter, field, value);
					SetterInjector.injectFieldSetter(bean, setterNode);
				}

				configCenterClient.addListener(key, fieldSetter.group(), new ConfigurationListener() {
					@Override
					public void process(ConfigChangeEvent event) {

						try {
							String newValue = configCenterClient.get(key, fieldSetter.group());
							SetterNode setterNode = LoaderUtils.toSetNode(fieldSetter, field, newValue);
							SetterInjector.injectFieldSetter(bean, setterNode);

							LOGGER.warn("process modify:{}-{}.", field, newValue);
						} catch (Exception e) {
							LOGGER.error("process modify Error:{} ", field, e);
						}
					}
				});
			}
		}
	}


}
