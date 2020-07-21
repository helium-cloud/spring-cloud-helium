package org.helium.cloud.logger.config;


import org.helium.cloud.configcenter.autoconfig.ConfigCenterConfig;
import org.helium.cloud.logger.aop.LogAop;
import org.helium.cloud.logger.service.LogBridge;
import org.helium.cloud.logger.service.LogBridgeDefault;
import org.helium.cloud.logger.service.LogClient;
import org.helium.cloud.logger.service.LogClientDefault;
import org.helium.cloud.logger.writer.api.LogWriter;
import org.helium.cloud.logger.writer.impl.EntityLogWriter;
import org.helium.cloud.logger.writer.impl.KafkaLogWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@EnableConfigurationProperties(LoggerConfig.class)
public class LoggerAutoConfig {

	@Autowired
	private LoggerConfig loggerConfig;



	@Bean(name = "log-aop")
	public LogAop logAop(){
		return new LogAop();
	}

	@Bean(name = "LogWriter")
	public LogWriter EntityLogWriter(){
		if ("kafka".equalsIgnoreCase(loggerConfig.getWriter())){
			return new KafkaLogWriter();
		}
		return new EntityLogWriter();
	}


	@Bean(name = "logBridge")
	@ConditionalOnProperty(prefix = "helium.log", value = "consumer")
	public LogBridge logBridge(){
		return new LogBridgeDefault();
	}

	@Bean(name = "logClient")
	@ConditionalOnProperty(prefix = "helium.log", value = "producer")
	public LogClient LogClient(){
		return new LogClientDefault();
	}


}
