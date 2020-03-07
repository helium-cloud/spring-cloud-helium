package org.helium.plugin.mybatis;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class SpringContextHelper implements BeanFactoryPostProcessor {

    private static ConfigurableListableBeanFactory factory;

    public static <T> T getBean(Class<T> c) {
        return factory.getBean(c);
    }

    public static <T> T getBean(Class<T> c, String beanName) {
        return c.cast(factory.getBean(beanName));
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        factory = beanFactory;
    }

    public static void putBean(String beanName, Object bean) {
        if (factory.containsBean(beanName)) {
            return;
        }
        factory.registerSingleton(beanName, bean);
    }
}
