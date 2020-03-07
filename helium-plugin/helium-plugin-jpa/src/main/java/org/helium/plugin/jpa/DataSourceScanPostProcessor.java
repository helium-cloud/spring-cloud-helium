//package org.helium.plugin.jpa;
//
//import com.allstar.cintracer.CinTracer;
//import org.helium.plugin.jpa.AnnotationDefinitonScanner;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.BeanClassLoaderAware;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.context.EnvironmentAware;
//import org.springframework.context.ResourceLoaderAware;
//import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
//import org.springframework.core.env.Environment;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.core.type.filter.AnnotationTypeFilter;
//import org.springframework.stereotype.Component;
//
//import javax.persistence.Entity;
//import java.util.HashSet;
//import java.util.Set;
//
//@Component
//public class DataSourceScanPostProcessor implements BeanDefinitionRegistryPostProcessor{
//    private final static LO LOGGER = CinTracer.getInstance(DataSourceScanPostProcessor.class);
//
//
//
//    public static Set<BeanDefinition> classHashSet = null;
//
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
//
//    }
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
//        if (classHashSet != null){
//            return;
//        }
//        synchronized (DataSourceScanPostProcessor.class){
//            if (classHashSet == null){
//                classHashSet = new HashSet<>();
//                AnnotationDefinitonScanner scanner =
//                        new AnnotationDefinitonScanner(beanDefinitionRegistry, Entity.class);
//                scanner.registerTypeFilter();
//                classHashSet = scanner.findCandidateComponents("com.allstar");
//            }
//        }
//
//
//
//    }
//}
