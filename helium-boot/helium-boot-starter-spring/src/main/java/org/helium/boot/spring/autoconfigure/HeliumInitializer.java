package org.helium.boot.spring.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class HeliumInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{
    private static final Logger LOGGER = LoggerFactory.getLogger(HeliumInitializer.class);

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        LOGGER.info("HeliumInitializer.initialize{}", applicationContext.getApplicationName());
    }

}
