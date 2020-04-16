package org.helium.framework.spring.autoconfigure;

import org.helium.framework.BeanContext;
import org.helium.framework.spring.assembly.HeliumAssembly;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
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

	private static BeanDefinitionRegistry registry;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		HeliumBeanRegistrar.registry = registry;
	}

	/**
	 * 将helium bean加载再到spring 容器中
	 * @param beans
	 */
	public static void registerBean(List<BeanContext> beans) {
		if (beans != null && beans.size() > 0) {
			beans.forEach(bean -> registerBean(bean));
		}
	}

	public static void registerBean(BeanContext bean) {
		Object obj = bean.getBean();
		Class<?> objClass = obj.getClass();
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(objClass);
		AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();

		registry.registerBeanDefinition(objClass.getName(), beanDefinition);
	}

}
