package org.helium.cloud.logger.config;


import org.helium.cloud.logger.aop.LogAop;
import org.helium.cloud.logger.service.LogBridge;
import org.helium.cloud.logger.service.LogBridgeDefault;
import org.helium.cloud.logger.service.LogClient;
import org.helium.cloud.logger.service.LogClientDefault;
import org.helium.cloud.logger.writer.api.LogWriter;
import org.helium.cloud.logger.writer.impl.KafkaLogWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerAutoConfig {

	@Bean(name = "log-aop")
	public LogAop logAop(){
		return new LogAop();
	}

//    @Bean(name = "log-save-file")
//    public LogWriter fileLogWriter(){
//        return new FileLogWriter();
//    }

    @Bean(name = "logClientAno")
    @ConditionalOnProperty(prefix = "helium.service.log", value = "annotation")
    public LogWriter kafkaLogWriter(){
        return new KafkaLogWriter();
    }


	@Bean(name = "logBridge")
	@ConditionalOnProperty(prefix = "helium.service.log", value = "consumer")
	public LogBridge logBridge(){
		return new LogBridgeDefault();
	}

	@Bean(name = "logClient")
	@ConditionalOnProperty(prefix = "helium.service.log", value = "producer")
	public LogClient LogClient(){
		return new LogClientDefault();
	}



}
