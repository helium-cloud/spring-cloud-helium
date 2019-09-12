package org.helium.cloud.task.autoconfigure;


import org.helium.cloud.common.utils.SpringContextUtil;
import org.helium.cloud.task.api.TaskProducerFactory;
import org.helium.cloud.task.manager.TaskConsumerManager;
import org.helium.cloud.task.manager.TaskConsumerManagerImpl;
import org.helium.cloud.task.store.TaskProducerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;

import java.util.Set;

import static java.util.Collections.emptySet;

@Configuration
@EnableConfigurationProperties(HeliumTaskConfig.class)
public class HeliumTaskConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumTaskConfiguration.class);

	/**
	 * task 生产者自动注入
	 *
	 * @return
	 */
	@Bean
	public TaskProducerFactory taskProducerFactoryPostProcessor() {
		return new TaskProducerFactoryImpl();
	}

	/**
	 * task 生产者自动注入
	 *
	 * @return
	 */
	@Bean
	public TaskEventBeanPostProcessor taskEventBeanPostProcessor() {
		return new TaskEventBeanPostProcessor();
	}

	/**
	 * task 生产消费管理
	 *
	 * @return
	 */
	@Bean
	public TaskConsumerManager taskConsumerManagerBeanPostProcessor() {
		return new TaskConsumerManagerImpl();
	}

	@Bean
	public SpringContextUtil getSpringContextUtil(){
		return new SpringContextUtil();
	}
	/**
	 *
	 * @param propertyResolver
	 * @return
	 */
	@Bean
	public TaskImplementationAnnotationBeanPostProcessor taskImplementationAnnotationBeanPostProcessor( PropertyResolver propertyResolver) {
		Set<String> packagesToScan = propertyResolver.getProperty(HeliumTaskConfig.PREFIX + ".bean", Set.class, emptySet());
		return new TaskImplementationAnnotationBeanPostProcessor(packagesToScan);
	}


}
