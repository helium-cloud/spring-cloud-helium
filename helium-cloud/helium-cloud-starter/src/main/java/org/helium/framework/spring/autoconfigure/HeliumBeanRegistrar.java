package org.helium.framework.spring.autoconfigure;

import org.helium.framework.BeanContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.List;


/**
 * 类描述：TODO
 *
 * @author zkailiang
 * @date 2020/4/15
 */
public class HeliumBeanRegistrar implements ImportBeanDefinitionRegistrar {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumBeanRegistrar.class);

	private static BeanDefinitionRegistry registry;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		HeliumBeanRegistrar.registry = registry;
	}

	/**
	 * 将helium bean加载再到spring 容器中
	 *
	 * @param beans
	 */
	public static void registerBean(List<BeanContext> beans) {
		if (beans != null && beans.size() > 0) {
			beans.forEach(bean -> registerBean(bean));

		}
	}

	public static void registerBean(BeanContext beanContext) {
		Object bean = beanContext.getBean();
		if (bean == null) {
			return;
		}

		String beanName = beanContext.getId().toString();
		((DefaultListableBeanFactory) registry).registerSingleton(beanName, bean);
		LOGGER.debug("helium bean register spring container,beanNme:{},Class:{}", beanName, bean.getClass().getName());
	}

}
