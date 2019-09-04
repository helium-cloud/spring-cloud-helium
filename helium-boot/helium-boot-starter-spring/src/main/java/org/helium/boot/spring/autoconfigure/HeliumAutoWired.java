package org.helium.boot.spring.autoconfigure;

import org.helium.boot.spring.assembly.HeliumAssembly;
import org.helium.framework.BeanContext;
import org.helium.framework.spi.ServiceInstance;
import org.helium.framework.spi.ServletInstance;
import org.helium.framework.spi.TaskInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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

	private static final Logger LOGGER = LoggerFactory.getLogger(HeliumAutoWired.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        HeliumAutoWired.applicationContext = applicationContext;
		resolveAutoWired();
    }

 
    private static void resolveAutoWired() {
        if (HeliumAutoWired.applicationContext == null) {
            throw new RuntimeException("resolveAutoWired And applicationContext Is Null");
        }
        //获取helium所有bean组件
		List<BeanContext> beanContexts = HeliumAssembly.INSTANCE.getBeans();
		for (BeanContext beanContext : beanContexts) {
			boolean resolve = beanContext instanceof ServiceInstance ||
					beanContext instanceof TaskInstance ||
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

