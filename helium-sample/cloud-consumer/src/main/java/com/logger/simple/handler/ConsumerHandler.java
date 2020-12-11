package com.logger.simple.handler;

import org.helium.cloud.logger.service.LogBridgeDefault;
import org.helium.kafka.UkConsumerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("LogBridgeHandler")
public class ConsumerHandler implements UkConsumerHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogBridgeDefault.class);


	@Override
	public void consumer(byte[] content) {

		try {
			String contentStr = new String(content);
			LOGGER.info("ConsumerHandler consumer content:{}", contentStr);

		} catch (Exception e) {
			LOGGER.error("Kafka Consumer exception:{}", e);
		}
	}

}