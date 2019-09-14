package org.helium.kafka.logging;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.helium.kafka.entity.SysLogEvent;

import org.helium.logging.spi.LogEvent;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenxuwu on 2017/8/31.
 */
public class KafkaLogManager {

	private static Map<String, Producer> producerMap = new ConcurrentHashMap<>();
	private Properties properties;
	private Producer producer;

	/**
	 * 初始化对象
	 */
	private KafkaLogManager() {
	}

	public KafkaLogManager(Properties properties, String key) {
		this.properties = properties;
		initKafkaProducer(key);
	}

	private synchronized Producer createProducer(Properties prop) {
		return new KafkaProducer(prop);
	}

	private void initKafkaProducer(String key){
		producer = producerMap.get(key);
		if(producer == null){
			producer = createProducer(properties);
		}
	}

	public void send(String topicName, LogEvent event){
		String message = unwrapperEvent(event);
		ProducerRecord producerRecord = new ProducerRecord(topicName, message);
		producer.send(producerRecord, (metadata, exception) -> {
			if (metadata != null) {
//				LOGGER.info("produce msg successful, msg's metadata :{}", metadata.toString());
			}
			if (exception != null) {
//				LOGGER.warn("produce msg error, exception :{}", exception);
			}
		});
	}


	private String unwrapperEvent(LogEvent logEvent) {
		SysLogEvent sysLogEvent = new SysLogEvent();

		sysLogEvent.setTime(logEvent.getTime());
		sysLogEvent.setLoggerName(logEvent.getLoggerName());
		sysLogEvent.setLevel(logEvent.getLevel().intValue());
		sysLogEvent.setMessage(logEvent.getMessage());

		sysLogEvent.setError(logEvent.getError() == null ? "" : logEvent.getError().getMessage());
		sysLogEvent.setMarker(logEvent.getMarker() == null ? "" : logEvent.getMarker().getName());
		sysLogEvent.setThread(new Long(logEvent.getThreadId()).intValue());
		sysLogEvent.setThreadName(logEvent.getThreadName());
//		sysLogEvent.setPid(EnvUtil.getPid());
//		sysLogEvent.setServiceName(EnvUtil.getServiceName());
//		sysLogEvent.setComputer(EnvUtil.getComputerName());

		Gson gson = new GsonBuilder().create();
		String message = gson.toJson(sysLogEvent);

		return message;
	}

	public void close() {
		if(producer != null){
			producer.close();
		}
	}



}
