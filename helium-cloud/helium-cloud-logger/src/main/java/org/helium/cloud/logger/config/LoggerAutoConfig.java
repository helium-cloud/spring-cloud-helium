package org.helium.cloud.logger.config;


import org.helium.cloud.logger.aop.LogAop;
import org.helium.cloud.logger.writer.api.LogWriter;
import org.helium.cloud.logger.writer.impl.FileLogWriter;
import org.helium.cloud.logger.writer.impl.KafkaLogWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerAutoConfig {

    @Bean(name = "file")
    @ConditionalOnProperty(prefix = "spring", name = "common.log-save-to", havingValue = "file")
    public LogWriter fileLogWriter(){
        return new FileLogWriter();
    }

    @Bean(name = "kafka")
    @ConditionalOnProperty(prefix = "spring", name = "common.log-save-to", havingValue = "kafka")
    public LogWriter kafkaLogWriter(){
        return new KafkaLogWriter();
    }

    @Bean()
    public LogAop logAop(){
        return new LogAop();
    }

}
