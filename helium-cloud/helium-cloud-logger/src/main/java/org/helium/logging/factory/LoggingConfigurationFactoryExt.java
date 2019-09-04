package org.helium.logging.factory;

import org.helium.logging.LoggingConfiguration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;


public class LoggingConfigurationFactoryExt implements LoggingConfigurationFactory {

    @Override
    public LoggingConfiguration getLoggingConfiguration() {
        ClassPathResource resource = new ClassPathResource("logging.xml");
        if (resource.exists()){
            try {
                InputStream inputStream = resource.getInputStream();
                LoggingConfiguration configuration = new LoggingConfiguration();
                configuration.parseXmlFrom(inputStream);
                return configuration;
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
