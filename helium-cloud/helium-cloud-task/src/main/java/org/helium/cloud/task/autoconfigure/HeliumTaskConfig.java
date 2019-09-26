package org.helium.cloud.task.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = HeliumTaskConfig.PREFIX)
public class HeliumTaskConfig {
    public static final String PREFIX = "helium.task";
	public static final String PREFIX_PACKAGE = "helium.task.package";
	public static final String TASK_PRODUCER_FACTORY = "helium:TaskProducerFactory";
	public static final String TASK_EVENT_BEAN_POSTPROCESSOR = "helium:TaskEventBeanPostProcessor";
	public static final String TASK_IMPLEMENTATION_ANNOTATION_BEAN_POSTPROCESSOR = "helium:taskImplementationAnnotationBeanPostProcessor";
	public static final String TASK_INVOKER_FACTORY = "helium:TaskInvokerFactory";

	public static String getPREFIX() {
		return PREFIX;
	}


}
