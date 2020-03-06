package org.helium.cloud.logger.config;


import org.helium.cloud.logger.aop.LogAop;
import org.helium.cloud.logger.writer.api.LogWriter;
import org.helium.cloud.logger.writer.impl.FileLogWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerAutoConfig {

    @Bean(name = "log-save-file")
    public LogWriter fileLogWriter(){
        return new FileLogWriter();
    }

//    @Bean(name = "log-save-kafka")
//    @ConditionalOnProperty(prefix = "spring", name = "common.log-save-to", havingValue = "kafka")
//    public LogWriter kafkaLogWriter(){
//        return new KafkaLogWriter();
//    }

	@Bean(name = "log-aop")
    public LogAop logAop(){
        return new LogAop();
    }

}
