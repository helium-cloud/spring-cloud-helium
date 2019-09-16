package org.helium.cloud.common.autoconfig;

import org.helium.cloud.common.utils.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration

public class CommonAutoConfiguration {

    @Bean
    public SpringContextUtil getSpringContextUtil(){
        return new SpringContextUtil();
    }

}
