package org.helium.kafka.spi.producer;


import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.helium.kafka.UkProducer;
import org.helium.kafka.spi.KafkaCounters;
import org.helium.perfmon.PerformanceCounterFactory;
import org.helium.perfmon.Stopwatch;
import org.helium.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * kafka生产者
 */
public class UkProducerImpl implements UkProducer {
    private static final Logger logger = LoggerFactory.getLogger(UkProducerImpl.class);
    private Producer producer;
    private String kafkaConf;
    private Properties properties;
	private KafkaCounters counters;

    public UkProducerImpl(Producer producer, String kafkaConf, Properties properties) {
        this.producer = producer;
        this.kafkaConf = kafkaConf;
        this.properties = properties;
		this.counters = PerformanceCounterFactory.getCounters(KafkaCounters.class, kafkaConf);
    }


	@Override
	public void produce(byte [] content) {
		String topic = properties.getProperty("topic");
		counters.getQps().increase();
		Stopwatch watch = counters.getTx().begin();
		if (StringUtils.isNullOrEmpty(topic)) {
            logger.error("kafka config properties not find topic, please config it, file name :{}", kafkaConf + ".properties");
            throw new IllegalArgumentException("kafka properties not find topic, please config it, file name" + kafkaConf);
		}
		ProducerRecord producerRecord = new ProducerRecord(topic, content);
		producer.send(producerRecord, (metadata, exception) -> {
			if (metadata != null) {
                logger.debug("produce msg successful, msg's metadata :{}", metadata.toString());
                watch.end();
			}
			if (exception != null) {
                logger.warn("produce msg error, exception :", exception);
                watch.fail(exception.getMessage());
			}

		});
	}

}
