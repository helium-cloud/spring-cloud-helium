package org.helium.kafka.test.uek;

import org.apache.kafka.clients.BrokerHostMappingConfig;
import org.helium.kafka.UkProducer;
import org.helium.kafka.spi.producer.UkProducerManager;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.UUID;

/**
 * Created by angel on 1/18/17.
 */
public class UkProducerTest {
	public static void main(String args[]) throws IOException, InterruptedException {

		String path = "helium-data-kafka/src/test/resources/ukproducer.properties";
		Properties properties = new Properties();
		properties.load(new FileReader(path));
		BrokerHostMappingConfig.Instance.initConfig(new File("helium-data-kafka/src/test/resources/hostmapping.properties"));
		UkProducer producer = UkProducerManager.INSTANCE.getKafkaProducer("ukproducer", properties);

		for (int i = 0; i < 20; i++){
			String xxx = "{\"id\":w"+i+",\"platform\":\"platform\",\"business\":\"business\",\"owner\":owner,\"time\":\"1547706558111\",\"costNano\":0}";
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i< 20; i++){
//						Log ukArgs = new UkArgs();
//						ukArgs.setBusiness("LOGIN");
//						ukArgs.setOwner("8613701381347");
//						ukArgs.setPeer("8613701381347");
//						ukArgs.setUuid(UUID.randomUUID().toString());
//						ukArgs.setPlatform("ott");
//						ukArgs.setType("REGISTER");
//						ukArgs.setContent(xxx);
						producer.produce(xxx.getBytes());
					}
				}
			});
			thread.start();
		}
		Thread.sleep(10000);
	}
}
