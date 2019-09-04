package org.helium.cloud.common.config;//package com.coral.cin.cloud.config;
//
//import org.apache.dubbo.config.spring.beans.factory.annotation.ServiceAnnotationBeanPostProcessor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.PropertyResolver;
//
//import java.util.Set;
//
//import static java.util.Collections.emptySet;
//
//@Configuration
//public class CinCloudCommonConfiguration {
//
//    @Autowired
//    private CinCloudCommonProperties cinCloudCommonProperties;
//
//    /**
//     * Creates {@link ServiceAnnotationBeanPostProcessor} Bean
//     *
//     * @return {@link ServiceAnnotationBeanPostProcessor}
//     */
//    @Bean
//    public ServiceImplementationAnnotationBeanPostProcessor serviceImplementationAnnotationBeanPostProcessor(PropertyResolver propertyResolver) {
//        Set<String> packagesToScan = propertyResolver.getProperty(cinCloudCommonProperties.getScan(), Set.class, emptySet());
//        return new ServiceImplementationAnnotationBeanPostProcessor(packagesToScan);
//    }
//}