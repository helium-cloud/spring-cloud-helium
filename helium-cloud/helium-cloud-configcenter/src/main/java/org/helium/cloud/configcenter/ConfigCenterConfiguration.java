package org.helium.cloud.configcenter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigCenterConfiguration {


    @Bean
    public FieldSetterBeanPostProcessor cloudPropertySourceLocator(){
        return new FieldSetterBeanPostProcessor();
    }
}