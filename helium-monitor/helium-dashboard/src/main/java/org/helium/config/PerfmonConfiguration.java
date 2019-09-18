package org.helium.config;


import org.helium.cloud.task.api.TaskProducerFactory;
import org.helium.cloud.task.autoconfigure.HeliumTaskConfig;
import org.helium.cloud.task.autoconfigure.TaskEventBeanPostProcessor;
import org.helium.cloud.task.autoconfigure.TaskImplementationAnnotationBeanPostProcessor;
import org.helium.cloud.task.store.TaskProducerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.PropertyResolver;

import java.util.Set;

import static java.util.Collections.emptySet;

@Configuration
@EnableConfigurationProperties(HeliumTaskConfig.class)
public class PerfmonConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(PerfmonConfiguration.class);


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
