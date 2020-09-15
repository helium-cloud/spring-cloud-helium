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

public class LogBridgeEmpty implements LogBridge {


	private static final Logger LOGGER = LoggerFactory.getLogger(LogBridgeEmpty.class);


}
