package org.helium.cloud.task.autoconfigure;


import org.helium.cloud.task.api.TaskProducerFactory;

import org.helium.cloud.task.store.TaskProducerFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.*;

import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;

@Configuration
@EnableConfigurationProperties(HeliumTaskConfig.class)
public class HeliumTaskConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumTaskConfiguration.class);

//	@Bean(name = HeliumTaskConfig.TASK_SCAN_PACKAGES_PROPERTY_RESOLVER)
//	public PropertyResolver taskScanBasePackagesPropertyResolver(ConfigurableEnvironment environment) {
//		ConfigurableEnvironment propertyResolver = new AbstractEnvironment() {
//			protected void customizePropertySources(MutablePropertySources propertySources) {
//				Map<String, Object> taskScanProperties = TaskSourcesUtils.(environment, HeliumTaskConfig.PREFIX);
//				propertySources.addLast(new MapPropertySource("taskScanProperties", taskScanProperties));
//			}
//		};
//		ConfigurationPropertySources.attach(propertyResolver);
//		return new TaskDelegatingPropertyResolver(propertyResolver);
//	}
	/**
	 * task 生产者自动注入
	 *
	 * @return
	 */
	@Bean(name = HeliumTaskConfig.TASK_PRODUCER_FACTORY)
	public TaskProducerFactory taskProducerFactoryPostProcessor() {
		return new TaskProducerFactoryImpl();
	}

	/**
	 * task 生产者自动注入
	 *
	 * @return
	 */
	@Bean(name = HeliumTaskConfig.TASK_EVENT_BEAN_POSTPROCESSOR)
	public TaskEventBeanPostProcessor taskEventBeanPostProcessor() {
		return new TaskEventBeanPostProcessor();
	}


	/**
	 *
	 * @param environment
	 * @return
	 */
	@Bean(name = HeliumTaskConfig.TASK_IMPLEMENTATION_ANNOTATION_BEAN_POSTPROCESSOR)
	public TaskImplementationAnnotationBeanPostProcessor taskImplementationAnnotationBeanPostProcessor(
			ConfigurableEnvironment environment) {
		Set<String> packagesToScan = environment.getProperty(HeliumTaskConfig.PREFIX_PACKAGE, Set.class, emptySet());
		return new TaskImplementationAnnotationBeanPostProcessor(packagesToScan);
	}


}
