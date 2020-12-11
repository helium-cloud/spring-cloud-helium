package org.helium.cloud.logger.service;

import org.helium.cloud.logger.service.monitor.ConsumerCounters;
import org.helium.framework.annotations.FieldSetter;
import org.helium.kafka.UkConsumer;
import org.helium.kafka.UkConsumerHandler;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public class LogBridgeDefault implements LogBridge {


	private static final Logger LOGGER = LoggerFactory.getLogger(LogBridgeDefault.class);

	@FieldSetter(value = "KF_LOG_CON")
	private UkConsumer ukConsumer;

	private ConsumerCounters consumeCounter;

	@Resource(name = "LogBridgeHandler")
	private UkConsumerHandler ukConsumerHandler;

	@PostConstruct
	public void init() {
		try {
			consumeCounter = PerformanceCounterFactory.getCounters(ConsumerCounters.class, "consumeCounter");
			//消费者任务
			LOGGER.info("ukConsumerHandler:{}", ukConsumerHandler);
			if (ukConsumerHandler != null) {
				ukConsumer.setAndRunHandler(ukConsumerHandler);
			} else {
				ukConsumer.setAndRunHandler(new ConsumerHandler());
			}

		} catch (Exception e) {
			LOGGER.error("init Exception", e);
		}
	}

	/**
	 * 执行kafaka消费
	 */
	class ConsumerHandler implements UkConsumerHandler {

		@Override
		public void consumer(byte[] content) {
			Stopwatch stopwatch = consumeCounter.getTx().begin();
			try {
				String contentStr = new String(content);
				LOGGER.info("ConsumerHandler consumer content:{}", contentStr);

				stopwatch.end();
			} catch (Exception e) {
				stopwatch.fail(e);
				LOGGER.error("Kafka Consumer exception:{}", e);
			}
		}
	}
}
