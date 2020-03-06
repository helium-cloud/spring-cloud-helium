package org.helium.framework.spring.autoconfigure;

import org.helium.cloud.task.autoconfigure.HeliumTaskConfig;
import org.helium.framework.BeanContext;
import org.helium.framework.annotations.TaskEvent;
import org.helium.framework.spi.ServiceInstance;
import org.helium.framework.spi.ServletInstance;

import org.helium.framework.spring.assembly.HeliumAssembly;
import org.helium.framework.task.TaskProducerFactory;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.List;

//import org.helium.framework.spi.Bootstrap;

/**
 * helium支持spring autowire
 */
public class HeliumAutoWired implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

	@Resource(name = HeliumTaskConfig.TASK_PRODUCER_FACTORY)
	private TaskProducerFactory taskProducerFactory;

	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumAutoWired.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        HeliumAutoWired.applicationContext = applicationContext;
		resolveAutoWired();
		resolveTaskEvent();
    }



	private static void resolveTaskEvent() {
		if (HeliumAutoWired.applicationContext == null) {
			throw new RuntimeException("resolveTaskEvent And applicationContext Is Null");
		}
		//获取helium所有bean组件
		List<BeanContext> beanContexts = HeliumAssembly.INSTANCE.getBeans();
		for (BeanContext beanContext : beanContexts) {
			boolean resolve = beanContext instanceof ServiceInstance ||
					beanContext instanceof ServletInstance;
			if (!resolve){
				continue;
			}
			Class<?> objClz = beanContext.getBean().getClass();

			for (Field field : objClz.getDeclaredFields()) {
				TaskEvent taskEvent = field.getAnnotation(TaskEvent.class);
				if (taskEvent != null) {
					try {
						String key = taskEvent.value();
						if (StringUtils.isNullOrEmpty(key)) {
							LOGGER.error("beanName process Error:{} and TaskProducer.value not be null", field);
							continue;
						}
						field.setAccessible(true);
						field.set(beanContext.getBean(),
								applicationContext.getBean(TaskProducerFactory.class).getProducer(key));
					} catch (Exception e) {
						LOGGER.error("resolveAutoWired Error continue:{}", field, e);
					}

				}
			}

		}
	}
 
    private static void resolveAutoWired() {
        if (HeliumAutoWired.applicationContext == null) {
            throw new RuntimeException("resolveAutoWired And applicationContext Is Null");
        }
        //获取helium所有bean组件
		List<BeanContext> beanContexts = HeliumAssembly.INSTANCE.getBeans();
		for (BeanContext beanContext : beanContexts) {
			boolean resolve = beanContext instanceof ServiceInstance ||
					beanContext instanceof ServletInstance;
			if (!resolve){
				continue;
			}
			Class<?> objClz = beanContext.getBean().getClass();

			for (Field field : objClz.getDeclaredFields()) {
				Autowired autowired = field.getAnnotation(Autowired.class);
				if (autowired != null) {
					try {
						field.setAccessible(true);
						field.set(beanContext.getBean(), applicationContext.getBean(field.getType()));
					} catch (Exception e) {
						LOGGER.error("resolveAutoWired Error continue:{}", field, e);
					}

				}
			}

		}
    }
 
}

