package org.helium.cloud.task.autoconfigure;



import org.helium.cloud.task.rpc.TaskInvokerFactory;
import org.helium.cloud.task.rpc.TaskInvokerFactoryCenter;
import org.helium.cloud.task.rpc.TaskInvokerFactoryEntity;
import org.helium.cloud.task.store.TaskProducerFactoryImpl;
import org.helium.framework.task.TaskProducerFactory;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;

import java.util.Set;

import static java.util.Collections.emptySet;

@Configuration
@EnableConfigurationProperties(HeliumTaskConfig.class)
public class HeliumTaskAutoConfiguration {
	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumTaskAutoConfiguration.class);


	/**
	 * task 生产者自动注入
	 * @return
	 */
	@Bean(name = HeliumTaskConfig.TASK_INVOKER_FACTORY)
	@ConditionalOnProperty(prefix = "helium.task", value = "package")
	@Order(99)
	public TaskInvokerFactory taskInvokerFactory(ConfigurableEnvironment environment) {
		String regUrl = environment.getProperty("dubbo.registry.address", "zookeeper://127.0.0.1:7998");
		if (StringUtils.isNullOrEmpty(regUrl)){
			return new TaskInvokerFactoryEntity();
		}
		//占位符处理
		regUrl = environment.resolvePlaceholders(regUrl);
		return new TaskInvokerFactoryCenter(regUrl);
	}

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
